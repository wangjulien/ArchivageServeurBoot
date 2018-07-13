package com.telino.avp.service.archivage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.dao.CommunicationDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.RestitutionDao;
import com.telino.avp.entity.archive.Communication;
import com.telino.avp.entity.archive.CommunicationList;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Restitution;
import com.telino.avp.entity.archive.RestitutionList;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.exception.AvpExploitException;
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

		Communication communication = communicationDao
				.findByComId(UUID.fromString((String) input.get("communicationid")));

		try (ByteArrayOutputStream outputContentZip = new ByteArrayOutputStream();
				ZipOutputStream output = new ZipOutputStream(outputContentZip)) {

			if (CommunicationState.V == communication.getCommunicationStatus()) {
				for (CommunicationList cl : communication.getCommunicationList()) {
					if (Objects.isNull(cl.getDocument())) {
						throw new AvpExploitException("542", null, "Récupération de l'archive lors de la communication "
								+ communication.getCommunicationId().toString(), null, null, null);
					}

					// Get the document, including content
					Document document = storageService.get(cl.getDocument().getDocId());
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
					Document attestation = storageService.archive("Attestation de copie conforme", document);
					// Reget the attestation
					attestation = storageService.get(attestation.getDocId());
					// add to Zip file too
					addFileToZip(attestation, output);

					// Log the event for archive
					Map<String, Object> inputToLog = new HashMap<>();
					inputToLog.put("operation", "Communication d'une archive");
					inputToLog.put("docid", document.getDocId().toString());
					inputToLog.put("attestationid", attestation.getDocId().toString());
					inputToLog.put("userid", input.get("user"));
					inputToLog.put("mailid", input.get("mailid"));
					inputToLog.put("docsname", document.getTitle());
					inputToLog.put("hash", document.getEmpreinte().getEmpreinte());
					inputToLog.put("logtype", LogArchiveType.A.toString());
					journalArchiveService.log(inputToLog);
				}
			}

			LOGGER.debug("Done");

			resultat.put("content", outputContentZip.toByteArray());
			resultat.put("content_length", outputContentZip.toByteArray().length);
			resultat.put("content_type", "application/octet-stream");
			resultat.put("title", "communication_" + communication.getCommunicationId().toString() + ".zip");

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AvpExploitException("541", e, "Génération du fichier zip pour la communication", null, null,
					null);
		}

	}

	/**
	 * TODO : create a JPA dynamic query in the place of composite String query
	 * 
	 * @param input
	 * @return
	 */
	public Communication communication(final Map<String, Object> input) {
		
		// If the user can communication a batch of docs
		Document document = documentDao.get(UUID.fromString(input.get("docid").toString()), false);
		Objects.requireNonNull(document.getProfile(), "A document should have a profile!");
		
		final String userId = input.get("user").toString();

		if (!userProfileRightService.canDoThePredict(document.getProfile().getParId(), userId, ParRight::isParCanCommunicate)) {
//			resultat.put("codeRetour", "1");
//			resultat.put("message", "Opération non autorisée");
			return null;
		}

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

			filter = " docid in (" + input.get("idlist") + ")";
		}

		if (filter.length() > 0)
			request += where + filter;

		LOGGER.info(request);

		// Get the document list
		List<Document> documentList = documentDao.getDocumentsByQuery(request);

		// New communication instance
		Communication newCommunication = new Communication();
		newCommunication.setUserId(input.get("user").toString());
		newCommunication.setDomnNom(input.get("domnnom").toString());
		newCommunication.setCommunicationStatus(CommunicationState.E);
		newCommunication.setCommunicationMotif(input.get("communicationmotif").toString());
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
		}

		communicationDao.save(newCommunication);

		return newCommunication;

	}

	/**
	 * Valider une communication, et sa liste
	 * 
	 * @param communication
	 */
	public void validationCommunication(final Communication communication) {

		communication.setCommunicationStatus(CommunicationState.V);

		// Persist the communication list associated with communique = true
		communication.getCommunicationList().forEach(cl -> cl.setCommunique(true));

		communicationDao.save(communication);
	}

	/**
	 * Refuser une communication
	 * 
	 * @param comId
	 */
	public void refusCommunication(final UUID comId) {
		// validateCommunication
		Communication communication = communicationDao.findByComId(comId);

		// Set state with R, for refused
		communication.setCommunicationStatus(CommunicationState.R);

		communicationDao.save(communication);
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

		// String request = "select a.docid, b.elasticid, b.title " + " from
		// restitutionlist a "
		// + "left join document b on a.docid = b.docid "
		// + "left join restitutions c on a.restitutionid=c.restitutionid where" + "
		// a.restitutionid = "
		// + restitutionid;
		try (ByteArrayOutputStream outputContentZip = new ByteArrayOutputStream();
				ZipOutputStream output = new ZipOutputStream(outputContentZip)) {

			for (RestitutionList rl : restitution.getRestitutionList()) {
				if (Objects.isNull(rl.getDocument())) {
					throw new AvpExploitException("542", null, "Récupération de l'archive lors de la restitution "
							+ restitution.getRestitutionId().toString(), null, null, null);
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
				// Reget the attestation
				attestation = storageService.get(attestation.getDocId());
				// add to Zip file too
				addFileToZip(attestation, output);

				// Log the event for archive
				Map<String, Object> inputToLog = new HashMap<>();
				inputToLog.put("operation", "Attestation de pré-restitution");
				inputToLog.put("docid", document.getDocId().toString());
				inputToLog.put("attestationid", attestation.getDocId().toString());
				inputToLog.put("userid", input.get("user"));
				inputToLog.put("mailid", input.get("mailid"));
				inputToLog.put("docsname", document.getTitle());
				inputToLog.put("hash", document.getEmpreinte().getEmpreinte());
				inputToLog.put("logtype", LogArchiveType.A.toString());
				journalArchiveService.log(inputToLog);

			}
			LOGGER.debug("Done");

			resultat.put("content", outputContentZip.toByteArray());
			resultat.put("content_length", outputContentZip.toByteArray().length);
			resultat.put("content_type", "application/octet-stream");
			resultat.put("title", "resttitution_" + restitution.getRestitutionId().toString() + ".zip");

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AvpExploitException("541", e, "Génération du fichier zip pour la communication", null, null,
					null);
		}

	}

	/**
	 * Demande d'une restitation
	 * 
	 * @param input
	 * @param resultat
	 */
	public void restitute(final Map<String, Object> input, final Map<String, Object> resultat) {

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

			filter = " docid in (" + input.get("idlist") + ")";
		}

		if (filter.length() > 0)
			request += where + filter;

		LOGGER.info(request);

		// Get the document list
		List<Document> documentList = documentDao.getDocumentsByQuery(request);

		Restitution newRestitution = new Restitution();
		newRestitution.setUserId(input.get("user").toString());
		newRestitution.setDomnNom(input.get("domnnom").toString());
		newRestitution.setRestitutionMotif(input.get("restitutionmotif").toString());
		newRestitution.setRestitutionStatus(RestitutionState.E);
		newRestitution.setHorodatage(ZonedDateTime.now());
		newRestitution.setDestinataire(input.get("destinataire").toString());
		// expiration d'une Restitution après 62 jours.
		newRestitution.setRestitutionEnd(ZonedDateTime.now().plus(62, ChronoUnit.DAYS));

		// Add the documents in the communication
		for (Document doc : documentList) {
			RestitutionList restList = new RestitutionList();
			restList.setRestitution(newRestitution);
			restList.setDocument(doc);
			restList.setTitle(doc.getTitle());
			restList.setRestitue(false);
		}

		restitutionDao.save(newRestitution);
	}
	
	
	/**
	 * Valider une restitution et supprimer les documents restitues
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void validationRestitution(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {

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
}
