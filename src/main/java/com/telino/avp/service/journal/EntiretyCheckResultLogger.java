package com.telino.avp.service.journal;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;
import com.telino.avp.protocol.DbEntityProtocol.LogEventType;

/**
 * Logger des erreurs après la contrôle de l'intégralité sur un liste de
 * document archivés Les différentes erreurs sont loggées dans log_archive et
 * log_event
 * 
 * @author Jiliang.WANG
 *
 */
@Component
public class EntiretyCheckResultLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntiretyCheckResultLogger.class);
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DocumentDao documentDao;
	
	@Autowired
	private JournalArchiveService journalArchiveService;

	@Autowired
	private JournalEventService journalEventService;

	public void logErrorResult(final Map<String, Object> input, final Map<UUID, FileReturnError> badDocsInUnit1,
			final Map<UUID, FileReturnError> badDocsInUnit2) {

		//
		// log_archive
		//
		LogArchive logArchive = new LogArchive();
		logArchive.setUser(userDao.findByUserId((String) input.get("userid")));
		logArchive.setMailId((String) input.get("mailid"));
		logArchive.setLogType(LogArchiveType.C.toString());
		
		// Combiner les resultats d'un document
		Map<UUID, String> combinedLogResult = badDocsInUnit1.entrySet().stream().collect(
				Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDetail() + AvpProtocol.ERROR_IN_PRIN));

		badDocsInUnit2.forEach((k, v) -> combinedLogResult.merge(k, v.getDetail() + AvpProtocol.ERROR_IN_MIRO,
				(v1, v2) -> v1 + AvpProtocol.ERROR_IN_MIRO));

		combinedLogResult.forEach((id, error) -> {
			StringBuilder operation = new StringBuilder("Erreur de ");
			operation.append((String) input.get("operation"));
			operation.append("de l'archive ");
			operation.append(id.toString());
			logArchive.setOperation(operation.toString());
			logArchive.setDocument(documentDao.get(id, false));
			try {
				journalArchiveService.setHorodatageAndSave(logArchive);
			} catch (AvpExploitException e) {
				LOGGER.error("problème dans log_archive " + e.getMessage());
			}
		});

		//
		// log_event
		//
		LogEvent logEvent = new LogEvent();
		logEvent.setOrigin((String) input.get("origin"));
		logEvent.setProcessus((String) input.get("processus"));
		logEvent.setAction((String) input.get("action"));
		logEvent.setOperateur((String) input.get("operateur"));
		logEvent.setVersionProcessus((String) input.get("version"));
		logEvent.setLogType(LogEventType.C.toString());

		badDocsInUnit1.forEach((id, error) -> {
			logEvent.setArchive(documentDao.get(id, false));
			StringBuilder detail = new StringBuilder(error.toString());
			detail.append(AvpProtocol.ERROR_IN_PRIN);
			logEvent.setDetail(detail.toString());
			try {
				journalEventService.setHorodatageAndSave(logEvent);
			} catch (AvpExploitException e) {
				LOGGER.error("problème dans log_event " + e.getMessage());
			}
		});

		badDocsInUnit2.forEach((id, error) -> {
			logEvent.setArchive(documentDao.get(id, false));
			StringBuilder detail = new StringBuilder(error.toString());
			detail.append(AvpProtocol.ERROR_IN_MIRO);
			logEvent.setDetail(detail.toString());
			try {
				journalEventService.setHorodatageAndSave(logEvent);
			} catch (AvpExploitException e) {
				LOGGER.error("problème dans logevent " + e.getMessage());
			}
		});
	}
}
