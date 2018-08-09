package com.telino.avp.service.journal;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bouncycastle.tsp.TimeStampToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.LogEventDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.Journal;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.exception.AvpDaoException;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.protocol.DbEntityProtocol.LogEventType;
import com.telino.avp.tools.BuildXmlFile;

/**
 * Classe de service pour LogEvent, regrouper tous les logique du metier
 * LogEvent
 * 
 * @author jwang
 *
 */
@Service
public class JournalEventService extends AbstractJournalService {

	@Autowired
	private LogEventDao logEventDao;

	@Autowired
	private LogArchiveDao logArchiveDao;

	@Autowired
	private DocumentDao documentDao;

	/**
	 * Controle de LOG_EVENT avec un logid dans input
	 * 
	 * @param logId
	 * @throws AvpExploitException
	 */
	public void checkLogEvent(final UUID logId) throws AvpExploitException {
		try {
			LogEvent logEvent = logEventDao.findLogEventById(logId);
			// Controle of LOG_EVENT in the DB master (isMirror = false)
			verifyJournal(logEvent, false);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération du journal d'archive", null, logId.toString());
		}
	}

	/**
	 * recuperer LogEvent info - le document XML du log
	 * 
	 * @param logId
	 * @param resultat
	 * @throws Exception
	 */
	public void getLogFile(final UUID logId, final Map<String, Object> resultat) throws AvpExploitException {
		try {
			LogEvent logEvent = logEventDao.findLogEventById(logId);
			// get the XML file of LogEvent
			Document journalxml = storageService.get(logEvent.getJournalXml().getDocId());

			byte[] content = journalxml.getContent();
			resultat.put("content", content);
			resultat.put("content_length", journalxml.getContentLength().intValue());
			resultat.put("content_type", journalxml.getContentType());
			resultat.put("title", journalxml.getTitle());
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.GET_LOG_DAO_ERROR, e,
					"Recupération du journal d'evenemnt", null, logId.toString());
		}
	}

	@Override
	public void log(final Map<String, Object> input) throws AvpExploitException {
		LogEvent logEvent = new LogEvent();

		if (input.get("logid") != null) {
			logEvent.setLogId(UUID.fromString(input.get("logid").toString()));
		}
		if (input.get("origin") != null) {
			logEvent.setOrigin((String) input.get("origin"));
		}
		if (input.get("processus") != null) {
			logEvent.setProcessus((String) input.get("processus"));
		}
		if (input.get("action") != null) {
			logEvent.setAction((String) input.get("action"));
		}
		if (input.get("detail") != null) {
			logEvent.setDetail((String) input.get("detail"));
		}
		if (input.get("operateur") != null) {
			logEvent.setOperateur((String) input.get("operateur"));
		}
		if (input.get("version") != null) {
			logEvent.setVersionProcessus((String) input.get("version"));
		}
		if (input.get("logtype") != null) {
			logEvent.setLogType((String) input.get("logtype"));
		}
		if (input.get("timestamptoken") != null) {
			logEvent.setTimestampToken((TimeStampToken) input.get("timestamptoken"));
		}
		if (input.get("archiveid") != null) {
			logEvent.setArchive(documentDao.get(UUID.fromString(input.get("archiveid").toString()), false));
		}
		if (input.get("journalid") != null) {
			logEvent.setLogArchive(
					logArchiveDao.findLogArchiveById(UUID.fromString(input.get("journalid").toString())));
		}
		if (input.get("trace") != null) {
			logEvent.setTrace((String) input.get("trace"));
		}
		if (input.get("methode") != null) {
			logEvent.setMethode((String) input.get("methode"));
		}
		if (input.get("hash") != null) {
			logEvent.setHash((String) input.get("hash"));
		}
		if (input.get("journalxmlid") != null) {
			logEvent.setJournalXml(documentDao.get(UUID.fromString(input.get("journalxmlid").toString()), false));
		}

		setHorodatageAndSave(logEvent);
	}

	@Override
	public byte[] buildStorageFormat(final Journal journal) throws AvpExploitException {
		try {
			// Recupere depuis DB master (isMirror = false)
			List<LogEvent> logArchives = logEventDao.findAllLogEventBeforeLogIdForContent(journal.getLogId(), false);

			Map<String, String> rootXmldata = new LinkedHashMap<>();
			rootXmldata.put("LogType", "Journal des évènements");
			rootXmldata.put("LogID", journal.getLogId().toString());
			rootXmldata.put("Date", journal.getHorodatage().toString());

			Map<String, String> structXml = new LinkedHashMap<>();
			structXml.put("LogEntryID", "getLogId");
			structXml.put("Timestamp", "getHorodatage");
			structXml.put("Process", "getProcessus");
			structXml.put("ProcessVersion", "getVersionProcessus");
			structXml.put("Origin", "getOrigin");
			structXml.put("Operator", "getOperateur");
			structXml.put("Action", "getAction");
			structXml.put("Details", "getDetail");
			structXml.put("LogEntryType", "getLogType");
			structXml.put("ArchiveRecordID", "getArchiveId");
			structXml.put("LogRecordID", "getJournalId");
			structXml.put("ArchiveRecordDigest", "getHash");
			// structXml.put("TimestampToken", "timestamptoken");

			return BuildXmlFile.buildLogFile(rootXmldata, structXml, logArchives);

		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.BUILD_LOG_FILE_GET_CONTENU_DAO_ERROR, e,
					"Recupération du contenu du journal d'evenement", null, journal.getLogId().toString());
		}
	}

	@Override
	protected void logScellement(final Document journalXml, final Journal journal) throws AvpExploitException {
		LogEvent logEvent = (LogEvent) journal;

		logEvent.setProcessus("Scellement du journal");
		logEvent.setAction("Scellement du journal des évènements");
		logEvent.setOrigin("ADELIS");
		logEvent.setOperateur("ADELIS");
		logEvent.setVersionProcessus("1");
		logEvent.setLogType(LogEventType.S.toString());
		// inputToLog.put("journalid", journal.getLogId().toString());
		logEvent.setJournalXml(journalXml);

		setHorodatageAndSave(logEvent);
	}

	@Override
	protected void traitementPreScellement(List<Document> listArchive, final List<Document> listAttestation) {

	}

	@Override
	protected void traitementPostScellement(final List<Document> listArchive, final Journal journal) {

	}

	@Override
	protected void traitementPostErreur(final List<Document> listAttestation) throws AvpExploitException {

	}

	@Override
	protected String recupereContenu(final UUID logId, final boolean isMirror) throws AvpExploitException {
		try {
			List<LogEvent> logEvents = logEventDao.findAllLogEventBeforeLogIdForContent(logId, isMirror);

			return logEvents.stream().map(LogEvent::buildContent).collect(Collectors.joining());
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_LOG_GET_CONTENU_DAO_ERROR, e,
					"Recuperation du contenu du journal d'evenement", null, logId.toString());
		}
	}

	@Override
	protected Journal bookLogId() throws AvpExploitException {
		try {
			return logEventDao.save(new LogEvent());
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_LOG_DAO_ERROR, e,
					"Attribution de l'identifiant du journal");
		}
	}

	public void setHorodatageAndSave(final LogEvent logEvent) throws AvpExploitException {
		if (null != logEvent.getTimestampToken()) {
			logEvent.setHorodatage(logEvent.getTimestampToken().getTimeStampInfo().getGenTime());

			try {
				if (null != logEvent.getTimestampToken().getEncoded())
					logEvent.setTimestampTokenBytes(logEvent.getTimestampToken().getEncoded());
			} catch (IOException e) {
				throw new AvpExploitException(AvpExploitExceptionCode.SAVE_LOG_BUILD_TAMPON_ERROR, e,
						"Ajout d'une entrée dans le journal des évènements", null, logEvent.getLogId().toString());
			}
		} else {
			logEvent.setHorodatage(ZonedDateTime.now());
		}

		// Persister dans les deux DB l'entity valorise
		try {
			logEventDao.save(logEvent);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.SAVE_LOG_DAO_ERROR, e,
					"Ajout d'une entrée dans le journal des évènements", null, logEvent.getLogId().toString());
		}
	}
}
