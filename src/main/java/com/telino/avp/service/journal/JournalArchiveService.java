package com.telino.avp.service.journal;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.DraftDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.entity.auxil.Journal;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.exception.AvpDaoException;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.protocol.DbEntityProtocol.DocumentStatut;
import com.telino.avp.protocol.DbEntityProtocol.DraftStatut;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;
import com.telino.avp.tools.BuildXmlFile;

/**
 * Classe de service pour LogArchive, regrouper tous les logique du metier
 * LogArchive
 * 
 * @author jwang
 *
 */
@Service
public class JournalArchiveService extends AbstractJournalService {

	@Autowired
	private LogArchiveDao logArchiveDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private DraftDao draftDao;

	@Autowired
	private UserDao userDao;

	/**
	 * Controle de LOG_ARCHIVE avec un logid dans input
	 * 
	 * @param logId
	 * @throws AvpExploitException
	 */
	public void checkLogArchive(final UUID logId) throws AvpExploitException {
		try {
			LogArchive logArchive = logArchiveDao.findLogArchiveById(logId);
			// Controle of LOG_ARCHIVE in the DB master (isMirror = false)
			verifyJournal(logArchive, false);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération du journal d'archive", null, logId.toString());
		}
	}

	/**
	 * Recuperation d'un attestation par logId
	 * 
	 * @param logId
	 * @param resultat
	 * @throws AvpExploitException
	 * @throws Exception
	 */
	public void getAttestation(final UUID logId, final Map<String, Object> resultat) throws AvpExploitException {

		try {
			LogArchive logArchive = logArchiveDao.findLogArchiveById(logId);
			Document attestation = storageService.get(logArchive.getAttestation().getDocId());
			byte[] content = attestation.getContent();
			resultat.put("content", content);
			resultat.put("content_length", attestation.getContentLength().intValue());
			resultat.put("content_type", attestation.getContentType());
			resultat.put("title", attestation.getTitle());
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération du journal d'archive", null, logId.toString());
		}
	}

	@Override
	public void log(final Map<String, Object> input) throws AvpExploitException {
		LogArchive logArchive = new LogArchive();

		if (input.get("logid") != null) {
			logArchive.setLogId(UUID.fromString(input.get("logid").toString()));
		}
		if (input.get("operation") != null) {
			logArchive.setOperation((String) input.get("operation"));
		}
		if (input.get("userid") != null) {
			logArchive.setUser(userDao.findByUserId(input.get("userid").toString()));
		}
		if (input.get("docid") != null) {
			logArchive.setDocument(documentDao.get(UUID.fromString(input.get("docid").toString()), false));
		}
		if (input.get("mailid") != null) {
			logArchive.setMailId((String) input.get("mailid"));
		}
		if (input.get("docsname") != null) {
			logArchive.setDocsName((String) input.get("docsname"));
		}
		if (input.get("logtype") != null) {
			logArchive.setLogType((String) input.get("logtype"));
		}
		if (input.get("hash") != null) {
			logArchive.setHash((String) input.get("hash"));
		}
		if (input.get("attestationid") != null) {
			logArchive.setAttestation(documentDao.get(UUID.fromString(input.get("attestationid").toString()), false));
		}

		if (input.get("timestamptoken") != null) {
			logArchive.setTimestampToken((TimeStampToken) input.get("timestamptoken"));
		}

		setHorodatageAndSave(logArchive);
	}

	@Override
	public byte[] buildStorageFormat(final Journal journal) throws AvpExploitException {
		try {
			// Recupere depuis DB master (isMirror = false)
			List<LogArchive> logArchives = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(journal.getLogId(),
					false);

			// creer en XML du contenu log doc
			Map<String, String> rootXmldata = new LinkedHashMap<>();
			rootXmldata.put("LogType", "Journal de cycle de vie des archives");
			rootXmldata.put("LogID", journal.getLogId().toString());
			rootXmldata.put("Date", journal.getHorodatage().toString());

			Map<String, String> structXml = new LinkedHashMap<>();
			structXml.put("LogEntryID", "getLogId");
			structXml.put("Timestamp", "getHorodatage");
			structXml.put("Operation", "getOperation");
			structXml.put("LogEntryType", "getLogType");
			structXml.put("CertificateID", "getAttestationId");
			structXml.put("ArchiveRecordID", "getDocumentId");
			structXml.put("ArchiveRecordDigest", "getHash");
			// structXml.put("TimestampToken", "timestamptoken");

			return BuildXmlFile.buildLogFile(rootXmldata, structXml, logArchives);

		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.BUILD_LOG_FILE_GET_CONTENU_DAO_ERROR, e,
					"Recupération du contenu du journal d'archive", null, journal.getLogId().toString());
		}
	}

	@Override
	protected void logScellement(final Document journalXml, final Journal journal) throws AvpExploitException {

		LogArchive logArchive = (LogArchive) journal;

		logArchive.setOperation("Scellement du journal");
		logArchive.setAttestation(journalXml);
		logArchive.setLogType(LogArchiveType.S.toString());
		logArchive.setUser(userDao.findByUserId("system"));

		setHorodatageAndSave(logArchive);
	}

	@Override
	protected void traitementPreScellement(final List<Document> listArchive, final List<Document> listAttestation)
			throws AvpExploitException {

		try {
			// Get all documents ready to be sealed
			listArchive.addAll(documentDao.getAllDocIdReadyForArchive());

			for (Document doc : listArchive) {
				// Check entirety of the document to seal
				// An AvpExploitException will be raised if check is not good
				storageService.check(doc.getDocId(), true);
				
				// Create an attestation of depose
				String operation = "Attestation de dépôt d'une archive";
				Document attestation = storageService.archive(operation, doc);
				// Delete the list when error is thrown
				listAttestation.add(attestation);

				LogArchive logArchive = new LogArchive();
				logArchive.setOperation(operation);
				logArchive.setDocument(doc);
				logArchive.setUser(userDao.findByUserId(doc.getArchiverId()));
				logArchive.setMailId(doc.getArchiverMail());
				logArchive.setDocsName(doc.getTitle());
				logArchive.setLogType(LogArchiveType.A.toString());
				logArchive.setHash(Objects.isNull(doc.getEmpreinte()) ? "" : doc.getEmpreinte().getEmpreinte());
				logArchive.setAttestation(attestation);
				
				// Persist the LOG
				setHorodatageAndSave(logArchive);
			}
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération des métadonnées de l'archive");
		}

	}

	@Override
	protected void traitementPostScellement(final List<Document> listArchive, final Journal journal)
			throws AvpExploitException {
		try {
			for (Document doc : listArchive) {

				// Set archived statut for the Doc to be archived
				doc.setStatut(DocumentStatut.ARCHIVED.getStatutCode());
				documentDao.saveMetaDonneesDocument(doc);

				Optional<Draft> draftOpt = draftDao.hasDraft(doc.getDocId());

				if (draftOpt.isPresent()) {
					Draft draft = draftOpt.get();
					// update drafte info
					draft.setTransmis(false);
					draft.setStatut(DraftStatut.ARCHIVED.toString());
					draft.setMotif("N° d''archive : " + doc.getDocId());
					draft.setDraftdate(ZonedDateTime.now());
					draftDao.saveDraft(draft);
				}
			}
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SEAL_LOG_ARCHIVE_DRAFT_ERROR, e,
					"Mise à jour des archives et drafts après scellement du journal", null,
					journal.getLogId().toString());
		}

	}

	@Override
	protected void traitementPostErreur(final List<Document> listAttestation) throws AvpExploitException {

		for (Document attestation : listAttestation) {
			storageService.delete(attestation);
		}
	}

	@Override
	protected String recupereContenu(final UUID logId, final boolean isMirror) throws AvpExploitException {
		try {
			List<LogArchive> logArchives = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(logId, isMirror);

			return logArchives.stream().map(LogArchive::buildContent).collect(Collectors.joining());
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_LOG_GET_CONTENU_DAO_ERROR, e,
					"Recuperation du contenu du journal d'archive", null, logId.toString());
		}
	}

	public LogArchive findLogArchiveForDocId(UUID docId, boolean isMirror) throws AvpExploitException {
		try {
			return logArchiveDao.findLogArchiveForDocId(docId, isMirror);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération du journal d'archive pour une archive", docId.toString(), null);
		}
	}

	/**
	 * @param docIds
	 *            : une liste d'archive passées de controle de l'intégralité
	 * @return
	 * @throws AvpExploitException
	 */
	public Set<LogArchive> getSellementLogArchiveForDocs(final List<UUID> docIds) throws AvpExploitException {
		try {
			return logArchiveDao.getSellementLogArchiveForDocs(docIds);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération des journaux d'archive pour une liste d'archive", docIds.toString(), null);
		}
	}

	/**
	 * Verification sellement des log_archives pour un document
	 * 
	 * @param docId
	 * @throws AvpExploitException
	 */
	public UUID verifySellementLogForDocId(final UUID docId, final boolean isMirror) throws AvpExploitException {
		try {
			Journal logArchive = logArchiveDao.findLogArchiveForDocId(docId, isMirror);

			Optional.ofNullable(logArchive.getTimestampTokenBytes())
					.orElseThrow(() -> new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, null,
							"Récupération du tampon d'horodatage du journal", null, logArchive.getLogId().toString()));

			// Reinitialize the TimestampToken object
			tamponHorodatageService.initTamponHorodatage(logArchive);

			// Verifier journaul
			verifyJournal(logArchive, isMirror);

			return logArchive.getLogId();
		} catch (CMSException | TSPException | IOException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_LOG_TAMPON_ERROR, e,
					"Initialisation tompon horodatage de journal", docId.toString(), null);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, null,
					"Recupération du journal d'archive", docId.toString(), null);
		}
	}

	@Override
	protected Journal bookLogId() throws AvpExploitException {
		try {
			return logArchiveDao.save(new LogArchive());
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_LOG_DAO_ERROR, e,
					"Attribution de l'identifiant du journal", null, null);
		}
	}

	public void setHorodatageAndSave(final LogArchive logArchive) throws AvpExploitException {
		//
		// Assign a Timestamp
		//
		if (!Objects.isNull(logArchive.getTimestampToken())) { // if log has a official Timestamp

			logArchive.setHorodatage(logArchive.getTimestampToken().getTimeStampInfo().getGenTime());

			try {
				if (null != logArchive.getTimestampToken().getEncoded())
					logArchive.setTimestampTokenBytes(logArchive.getTimestampToken().getEncoded());
			} catch (IOException e) {
				throw new AvpExploitException(AvpExploitExceptionCode.SAVE_LOG_BUILD_TAMPON_ERROR, e,
						"Ajout d'une entrée dans le journal des archives", null, logArchive.getLogId().toString());
			}
		} else { // Otherwise use the timestamp of the system
			logArchive.setHorodatage(ZonedDateTime.now());
		}

		// Persister dans les deux DB l'entity valorise
		try {
			logArchiveDao.save(logArchive);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_LOG_DAO_ERROR, e,
					"Ajout d'une entrée dans le journal des archives", null, logArchive.getLogId().toString());
		}
	}
}
