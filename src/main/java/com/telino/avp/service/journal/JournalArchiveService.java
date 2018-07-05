package com.telino.avp.service.journal;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

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
import com.telino.avp.exception.AvpExploitException;
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
	public void checklogcheck(final UUID logId) throws AvpExploitException {

		LogArchive logArchive = logArchiveDao.findLogArchiveById(logId);

		// Controle of LOG_ARCHIVE in the DB master (isMirror = false)
		verifyJournal(logArchive, false);
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

		LogArchive logArchive = logArchiveDao.findLogArchiveById(logId);
		Document attestation = storageService.get(logArchive.getAttestation().getDocId());
		byte[] content = attestation.getContent();
		resultat.put("content", content);
		resultat.put("content_length", attestation.getContentLength().intValue());
		resultat.put("content_type", attestation.getContentType());
		resultat.put("title", attestation.getTitle());

		// TODO : } else {
		// resultat.put("codeRetour", "10");
		// resultat.put("message", "Document inexistant");
		// }
	}

	@Override
	public void log(Map<String, Object> input) throws AvpExploitException {
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

		} catch (PersistenceException e) {
			throw new AvpExploitException("619", e, "Recupération du contenu du journal", null, null,
					journal.getLogId().toString());
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
	protected void traitementPreScellement(List<Document> listArchive) throws AvpExploitException {

		try {
			// Get all documents ready to be sealed
			listArchive.addAll(documentDao.getAllDocIdReadyForArchive());

			for (Document doc : listArchive) {
				Map<String, Object> inputToLog = new HashMap<>();

				String operation = "Attestation de dépôt d'une archive";

				inputToLog.put("operation", operation);
				inputToLog.put("docid", doc.getDocId().toString());

				// Check the entirety for archive
				if (storageService.check(doc.getDocId(), true)) {

					inputToLog.put("userid", doc.getArchiverId());
					inputToLog.put("mailid", doc.getArchiverMail());
					inputToLog.put("docsname", doc.getTitle());
					inputToLog.put("logtype", LogArchiveType.A.toString());
					inputToLog.put("hash", doc.getEmpreinte().getEmpreinte());

					Document attestation = storageService.archive(operation, doc);
					inputToLog.put("attestationid", attestation.getDocId().toString());

					// TODO : Rollback when error is thrown
					// List<Document> listAttestation = new ArrayList<>();
					// listAttestation.add(attestation);

					log(inputToLog);

				} else {
					throw new AvpExploitException("503", null, "Recupération des métadonnées de l'archive", null,
							doc.toString(), null);
				}
			}
		} catch (PersistenceException e) {
			throw new AvpExploitException("619", e, "Contrôle d'intégrité des factures avant scellement du journal",
					null, null, null);
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
					draft.setStatut(DraftStatut.ARCHIVED);
					draft.setMotif("N° d''archive : " + doc.getDocId());
					draft.setDraftdate(ZonedDateTime.now());
					draftDao.saveDraft(draft);
				}
			}
		} catch (PersistenceException e) {
			throw new AvpExploitException("620", e, "Mise à jour des archives et drafts après scellement du journal",
					null, null, journal.getLogId().toString());
		}

	}

	@Override
	protected void traitementPostErreur(final List<Document> attestationList) throws AvpExploitException {

		for (Document attestation : attestationList) {
			storageService.delete(attestation);
		}
	}

	@Override
	protected String recupereContenu(final UUID logId, final boolean isMirror) throws AvpExploitException {
		try {

			List<LogArchive> logArchives = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(logId, isMirror);

			return logArchives.stream().map(LogArchive::buildContent).collect(Collectors.joining());
		} catch (PersistenceException e) {
			throw new AvpExploitException("702", null, "Recuperation du contenu du journal", null, null,
					logId.toString());
		}
	}

	public LogArchive findLogArchiveForDocId(UUID docId, boolean isMirror) {
		return logArchiveDao.findLogArchiveForDocId(docId, isMirror);
	}

	/**
	 * @param docIds
	 *            : une liste d'archive passées de controle de l'intégralité
	 * @return
	 * @throws AvpExploitException
	 */
	public Set<LogArchive> getSellementLogArchiveForDocs(final List<UUID> docIds) throws AvpExploitException {

		return logArchiveDao.getSellementLogArchiveForDocs(docIds);
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

			Optional.ofNullable(logArchive.getTimestampTokenBytes()).orElseThrow(
					() -> new AvpExploitException("518", null, "Récupération du tampon d'horodatage du journal", null,
							null, String.valueOf(logArchive.getLogId())));

			// Reinitialize the TimestampToken object
			tamponHorodatageService.initTamponHorodatage(logArchive);

			// Verifier journaul
			verifyJournal(logArchive, isMirror);

			return logArchive.getLogId();
		} catch (PersistenceException | CMSException | TSPException | IOException e) {
			throw new AvpExploitException("506", e, "Contrôle d'intégrité de journal", null, docId.toString(), null);
		}
	}

	@Override
	protected Journal bookLogId() throws AvpExploitException {
		try {
			return logArchiveDao.save(new LogArchive());
		} catch (PersistenceException e) {
			throw new AvpExploitException("619", e, "Attribution de l'identifiant du journal", null, null, null);
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
				throw new AvpExploitException("507", e, "Ajout d'une entrée dans le journal des archives", null, null,
						logArchive.getLogId().toString());
			}
		} else { // Otherwise use the timestamp of the system
			logArchive.setHorodatage(ZonedDateTime.now());
		}

		// Persister dans les deux DB l'entity valorise
		try {
			logArchiveDao.save(logArchive);

		} catch (PersistenceException e) {
			throw new AvpExploitException("607", e, "Ajout d'une entrée dans le journal des archives", null, null,
					logArchive.getLogId().toString());
		}
	}
}
