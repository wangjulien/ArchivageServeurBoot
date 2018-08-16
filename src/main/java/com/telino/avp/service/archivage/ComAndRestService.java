package com.telino.avp.service.archivage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.CommunicationDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.RestitutionDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.archive.Communication;
import com.telino.avp.entity.archive.CommunicationList;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Restitution;
import com.telino.avp.entity.archive.RestitutionList;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.exception.AvpDaoException;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.protocol.DbEntityProtocol.CommunicationState;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;
import com.telino.avp.protocol.DbEntityProtocol.RestitutionState;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.storage.AbstractStorageService;

@Service
public class ComAndRestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComAndRestService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private CommunicationDao communicationDao;

	@Autowired
	private RestitutionDao restitutionDao;

	@Autowired
	private UserProfileRightService userProfileRightService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private AbstractStorageService storageService;

	@Autowired
	private JournalArchiveService journalArchiveService;

	/**
	 * Chercher une communication metaDonnee dans DB
	 * 
	 * @param comId
	 * @return
	 */
	public Communication findByComId(final UUID comId) {
		return communicationDao.findByComId(comId);
	}

	/**
	 * Génère à la volée le fichier zip de communication d'une ou plusieurs archives
	 * dont la communication a été préalablement validée
	 * 
	 * @param input
	 *            contenant entre autres l'id de la communication à retsituter
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void getCommunication(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {
		Communication communication = null;
		try {
			communication = communicationDao.findByComId(UUID.fromString((String) input.get("communicationid")));
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_COM_ARCHIVE, e, "Recuperer une communication");
		}

		try (ByteArrayOutputStream outputContentZip = new ByteArrayOutputStream();
				ZipOutputStream output = new ZipOutputStream(outputContentZip)) {

			if (CommunicationState.V == communication.getCommunicationStatus()) {
				for (CommunicationList cl : communication.getCommunicationList()) {
					if (Objects.isNull(cl.getDocument())) {
						throw new AvpExploitException(AvpExploitExceptionCode.GET_COM_ARCHIVE, null,
								"Récupération de l'archive lors de la communication "
										+ communication.getCommunicationId().toString());
					}

					// Get the document, including content
					Document document = storageService.get(cl.getDocument().getDocId());
					Map<String, Object> inputDoc = new HashMap<>();
					inputDoc.put("docid", document.getDocId().toString());
					inputDoc.put("user", input.get("user"));
					inputDoc.put("mailid", input.get("mailid"));
					inputDoc.put("docsname", document.getTitle());

					// entirety check
					documentService.control(inputDoc);
					// add to Zip file
					addFileToZip(document, output);

					// Archive a attesation for the document
					Document attestation = storageService.archive("Attestation de copie conforme", document);
					// Reget the attestation since the contenu is crypted
					attestation = storageService.get(attestation.getDocId());
					// add to Zip file too
					addFileToZip(attestation, output);

					// Log the event for archive
					LogArchive logArchive = new LogArchive();
					logArchive.setOperation("Communication d'une archive");
					logArchive.setDocument(document);
					logArchive.setAttestation(attestation);
					logArchive.setUser(Objects.isNull(input.get("user")) ? null
							: userDao.findByUserId((String) input.get("user")));
					logArchive.setMailId((String) input.get("mailid"));
					logArchive.setDocsName(document.getTitle());
					logArchive.setHash(
							Objects.isNull(document.getEmpreinte()) ? "" : document.getEmpreinte().getEmpreinte());
					logArchive.setLogType(LogArchiveType.A.toString());

					journalArchiveService.setHorodatageAndSave(logArchive);
				}
			}
			LOGGER.debug("Done");

			resultat.put("content", outputContentZip.toByteArray());
			resultat.put("content_length", outputContentZip.toByteArray().length);
			resultat.put("content_type", "application/octet-stream");
			resultat.put("title", "communication_" + communication.getCommunicationId().toString() + ".zip");

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AvpExploitException(AvpExploitExceptionCode.GET_COM_ZIP_FILE, e,
					"Génération du fichier zip pour la communication");
		}

	}

	/**
	 * Enregister une communication avec les documents
	 * 
	 * @param input
	 * @return
	 */
	public Communication communication(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {
		//
		// If the user can communication a batch of docs
		if (!isAllDocAllowed(input, resultat, ParRight::isParCanCommunicate))
			return null;

		// Form the SQL Request
		// get file list
		String request = (String) input.get("sqlRequest");
		// remove order by
		if (request.contains(" order by ")) {
			request = request.substring(0, request.indexOf(" order by "));
		}
		// String request = "select docid, title from document ";
		String filter = "";
		if (input.get("criteres") != null && input.get("criteres").toString().length() > 0) {
			filter = (String) input.get("criteres");
		}
		if (input.get("dateinf") != null && input.get("dateinf").toString().length() > 0) {
			if (filter.length() > 0)
				filter += " and ";
			filter += " archive_date >= '" + (String) input.get("dateinf") + "'";
		}
		if (input.get("datesup") != null && input.get("datesup").toString().length() > 0) {
			if (filter.length() > 0)
				filter += " and ";
			filter += " archive_date <= '" + (String) input.get("datesup") + "'";
		}

		String where = " where ";

		if (request.toLowerCase().contains(" where "))
			where = " and ";

		if (input.get("idlist") != null && input.get("idlist").toString().length() > 0) {

			filter = " docid in (" + addApost((String) input.get("idlist")) + ")";
		}

		if (filter.length() > 0)
			request += where + filter;

		LOGGER.info(request);

		try {
			// Get the document list
			List<Document> documentList = documentDao.getDocumentsByQuery(request);

			// New communication instance
			Communication newCommunication = new Communication();
			newCommunication.setUserId((String) input.get("user"));
			newCommunication.setDomnNom((String) input.get("domnnom"));
			newCommunication.setCommunicationStatus(CommunicationState.E);
			newCommunication.setCommunicationMotif((String) input.get("communicationmotif"));
			newCommunication.setHorodatage(ZonedDateTime.now());
			// expiration d'une communication après 15 jours.
			newCommunication.setCommunicationEnd(ZonedDateTime.now().plus(15, ChronoUnit.DAYS));

			// Add the documents in the communication
			for (Document doc : documentList) {
				CommunicationList comList = new CommunicationList();
				comList.setCommunication(newCommunication);
				comList.setDocument(doc);
				comList.setTitle(doc.getTitle());
				comList.setCommunique(false);

				newCommunication.addCommunicationList(comList);
			}

			communicationDao.save(newCommunication);

			return newCommunication;
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_COM_DAO_ERROR, e,
					"Enregister une communication avec les documents");
		}

	}

	/**
	 * Valider une communication, et sa liste
	 * 
	 * @param communication
	 * @throws AvpExploitException
	 */
	public void validationCommunication(final Communication communication) throws AvpExploitException {
		try {
			communication.setCommunicationStatus(CommunicationState.V);

			// Persist the communication list associated with communique = true
			communication.getCommunicationList().forEach(cl -> cl.setCommunique(true));

			communicationDao.save(communication);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_COM_DAO_ERROR, e, "Valider une communication");
		}
	}

	/**
	 * Refuser une communication
	 * 
	 * @param comId
	 * @throws AvpExploitException
	 */
	public void refusCommunication(final UUID comId) throws AvpExploitException {
		try {
			// validateCommunication
			Communication communication = communicationDao.findByComId(comId);

			// Set state with R, for refused
			communication.setCommunicationStatus(CommunicationState.R);

			communicationDao.save(communication);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_COM_DAO_ERROR, e, "Refuser une communication");
		}
	}

	/**
	 * Génère à la volée le fichier zip de restitution d'une ou plusieurs archives
	 * 
	 * @param input
	 *            contenant entre autres l'id de la restitution
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void getRestitution(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {

		Restitution restitution = restitutionDao.findByRestId(UUID.fromString(input.get("restitutionid").toString()));

		try (ByteArrayOutputStream outputContentZip = new ByteArrayOutputStream();
				ZipOutputStream output = new ZipOutputStream(outputContentZip)) {

			for (RestitutionList rl : restitution.getRestitutionList()) {
				if (Objects.isNull(rl.getDocument())) {
					throw new AvpExploitException(AvpExploitExceptionCode.GET_COM_ARCHIVE, null,
							"Récupération de l'archive lors de la restitution "
									+ restitution.getRestitutionId().toString());
				}

				// Get the document, including content
				Document document = storageService.get(rl.getDocument().getDocId());
				Map<String, Object> inputDoc = new HashMap<>();
				inputDoc.put("docid", document.getDocId().toString());
				inputDoc.put("userid", input.get("user"));
				inputDoc.put("mailid", input.get("mailid"));
				inputDoc.put("docsname", document.getTitle());

				// entirety check
				documentService.control(inputDoc);
				// add to Zip file
				addFileToZip(document, output);

				// Archive a attesation for the document
				Document attestation = storageService.archive("Attestation de pré-restitution", document);
				// Reget the attestation since the contenu is crypted
				attestation = storageService.get(attestation.getDocId());
				// add to Zip file too
				addFileToZip(attestation, output);

				// Log the event for archive
				LogArchive logArchive = new LogArchive();
				logArchive.setOperation("Attestation de pré-restitution");
				logArchive.setDocument(document);
				logArchive.setAttestation(attestation);
				logArchive.setUser(
						Objects.isNull(input.get("user")) ? null : userDao.findByUserId((String) input.get("user")));
				logArchive.setMailId((String) input.get("mailid"));
				logArchive.setDocsName(document.getTitle());
				logArchive
						.setHash(Objects.isNull(document.getEmpreinte()) ? "" : document.getEmpreinte().getEmpreinte());
				logArchive.setLogType(LogArchiveType.A.toString());

				journalArchiveService.setHorodatageAndSave(logArchive);
			}
			LOGGER.debug("Done");

			resultat.put("content", outputContentZip.toByteArray());
			resultat.put("content_length", outputContentZip.toByteArray().length);
			resultat.put("content_type", "application/octet-stream");
			resultat.put("title", "resttitution_" + restitution.getRestitutionId().toString() + ".zip");

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AvpExploitException(AvpExploitExceptionCode.GET_REST_ZIP_FILE, e,
					"Génération du fichier zip pour la restitution");
		}

	}

	/**
	 * Demande d'une restitation
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void restitute(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {
		//
		// If the user can communication a batch of docs
		if (!isAllDocAllowed(input, resultat, ParRight::isParCanRestitute))
			return;

		// Form SQL request
		// get file list
		String request = (String) input.get("sqlRequest");
		// remove order by
		if (request.contains(" order by ")) {
			request = request.substring(0, request.indexOf(" order by "));
		}
		// String request = "select docid, title from document ";
		String filter = "";
		if (input.get("criteres") != null && input.get("criteres").toString().length() > 0) {
			filter = (String) input.get("criteres");
		}
		if (input.get("dateinf") != null && input.get("dateinf").toString().length() > 0) {
			if (filter.length() > 0)
				filter += " and ";
			filter += " archive_date >= '" + (String) input.get("dateinf") + "'";
		}
		if (input.get("datesup") != null && input.get("datesup").toString().length() > 0) {
			if (filter.length() > 0)
				filter += " and ";
			filter += " archive_date <= '" + (String) input.get("datesup") + "'";
		}

		String where = " where ";

		if (request.toLowerCase().contains(" where "))
			where = " and ";

		if (input.get("idlist") != null && input.get("idlist").toString().length() > 0) {

			filter = " docid in (" + addApost((String) input.get("idlist")) + ")";
		}

		if (filter.length() > 0)
			request += where + filter;

		LOGGER.info(request);

		try {
			// Get the document list
			List<Document> documentList = documentDao.getDocumentsByQuery(request);

			Restitution newRestitution = new Restitution();
			newRestitution.setUserId((String) input.get("user"));
			newRestitution.setDomnNom((String) input.get("domnnom"));
			newRestitution.setRestitutionMotif((String) input.get("restitutionmotif"));
			newRestitution.setRestitutionStatus(RestitutionState.E);
			newRestitution.setHorodatage(ZonedDateTime.now());
			newRestitution.setDestinataire((String) input.get("destinataire"));
			// expiration d'une Restitution après 62 jours.
			newRestitution.setRestitutionEnd(ZonedDateTime.now().plus(62, ChronoUnit.DAYS));

			// Add the documents in the communication
			for (Document doc : documentList) {
				RestitutionList restList = new RestitutionList();
				restList.setRestitution(newRestitution);
				restList.setDocument(doc);
				restList.setTitle(doc.getTitle());
				restList.setRestitue(false);

				newRestitution.addRestitutionList(restList);
			}

			restitutionDao.save(newRestitution);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_REST_DAO_ERROR, e,
					"Enregister une restitution avec les documents");
		}
	}

	/**
	 * Valider une restitution et supprimer les documents restitues
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Restitution validationRestitution(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {
		try {
			Restitution restitution = restitutionDao.findByRestId(UUID.fromString((String) input.get("restitutionid")));

			// Delete the documents of restitution
			for (RestitutionList rl : restitution.getRestitutionList()) {
				input.put("docid", rl.getDocument().getDocId().toString());
				input.put("elasticid", rl.getDocument().getElasticid());
				input.put("docsname", rl.getDocument().getTitle());

				// isBgTask = false
				documentService.delete(input, resultat, false);

				// Only when delete is successful, list is marked with Restitue = true
				if (ReturnCode.OK.toString().equals((String) resultat.get("codeRetour")))
					rl.setRestitue(true);
			}

			restitution.setRestitutionStatus(RestitutionState.V);
			restitutionDao.save(restitution);

			return restitution;
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_REST_DAO_ERROR, e,
					"Valider une restitution et suprimer les documents");
		}
	}

	/**
	 * Ajouter un fichier Zip
	 * 
	 * @param document
	 * @param zos
	 * @throws IOException
	 */
	private void addFileToZip(final Document document, final ZipOutputStream zos) throws IOException {
		byte[] buffer = new byte[1024];

		ZipEntry ze = new ZipEntry(document.getTitle());
		zos.putNextEntry(ze);
		try (ByteArrayInputStream in = new ByteArrayInputStream(document.getContent())) {

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
		}

		zos.closeEntry();
	}

	private String addApost(String docIdList) {
		return Arrays.asList(docIdList.replaceAll("\\s", "").split(",")).stream()
				.collect(Collectors.joining("','", "'", "'"));
	}

	private boolean isAllDocAllowed(final Map<String, Object> input, final Map<String, Object> resultat,
			final Predicate<ParRight> predicate) {
		//
		// If the user can communication a batch of docs
		//
		final String userId = (String) input.get("user");

		if (Objects.nonNull(input.get("idlist")) && !input.get("idlist").toString().isEmpty()) {
			// a list of drafts separated by ','
			List<UUID> docIds = Arrays.asList((input.get("idlist").toString().replaceAll("\\s", "").split(",")))
					.stream().map(UUID::fromString).collect(Collectors.toList());

			// Filter the docs do not have profil corresponds user's Par-right
			List<UUID> docNotAllowed = documentDao.findAllByDocIdIn(docIds).stream().filter(
					doc -> !userProfileRightService.canDoThePredict(doc.getProfile().getParId(), userId, predicate))
					.map(Document::getDocId).collect(Collectors.toList());

			// If there is, return information to the user
			if (!docNotAllowed.isEmpty()) {
				resultat.put("codeRetour", "1");
				resultat.put("message", "Opération non autorisée for document : " + docNotAllowed);
				return false;
			}
		}

		return true;
	}
}
