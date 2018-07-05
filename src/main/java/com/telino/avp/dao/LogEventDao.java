package com.telino.avp.dao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterLogEventRepository;
import com.telino.avp.dao.mirrordao.MirrorLogEventRepository;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;
import com.telino.avp.protocol.DbEntityProtocol.LogEventState;
import com.telino.avp.protocol.DbEntityProtocol.LogEventType;

/**
 * Surcouche DAO pour LogEvent
 * 
 * @author Jiliang.WANG
 *
 */
@Repository
@Transactional
public class LogEventDao {

	@Autowired
	private MasterLogEventRepository masterLogEventRepository;

	@Autowired
	private MirrorLogEventRepository mirrorLogEventRepository;

	/**
	 * Recupere tous les Log_Event depuis dernier sellement jusqu'a logId
	 * 
	 * @param logId
	 * @param isMirror
	 * @return
	 */
	public List<LogEvent> findAllLogEventBeforeLogIdForContent(final UUID logId, final boolean isMirror) {
		// find first the LogEvent by logId
		LogEvent logEvent = masterLogEventRepository.findById(logId).orElseThrow(EntityNotFoundException::new);

		if (isMirror)
			return mirrorLogEventRepository.findAllLogEventByTimestampForContent(logEvent.getTimestamp(),
					LogEventType.S.toString());
		else
			return masterLogEventRepository.findAllLogEventByTimestampForContent(logEvent.getTimestamp(),
					LogEventType.S.toString());
	}

	/**
	 * Find tous les LogArchive le controle de integralite est echoue
	 * 
	 * @return
	 */
	public List<LogEvent> findAllArchiveIdFailedCheckEntirety() {
		return masterLogEventRepository.findAllArchiveIdFailedCheckEntirety(LogArchiveType.C.toString(),
				LogEventType.C.toString(), LogEventState.I.toString());
	}

	/**
	 * Marquer le LogEvent est exploite "T"
	 * 
	 * @param logEvents
	 * @return
	 */
	public void terminateExploitedEvent(List<LogEvent> logEvents) {
		if (logEvents.isEmpty())
			return;

		logEvents.forEach(e -> e.setStatExp(LogEventState.T));

		masterLogEventRepository.saveAll(logEvents);
		mirrorLogEventRepository.saveAll(logEvents);
	}

	/**
	 * Enregistrer dans les deux DB un entity LogEvent
	 * 
	 * @param logEvent
	 * @return
	 */
	public LogEvent save(LogEvent logEvent) {

		if (Objects.isNull(logEvent.getLogId()))
			logEvent.setLogId(UUID.randomUUID());

		// Timestamp to have a chrono order
		if (Objects.isNull(logEvent.getTimestamp()))
			logEvent.setTimestamp(ZonedDateTime.now());

		logEvent = masterLogEventRepository.save(logEvent);
		logEvent = mirrorLogEventRepository.save(logEvent);

		return logEvent;
	}

	/**
	 * Recupere entity par ID
	 * 
	 * @param id
	 * @return
	 */
	public LogEvent findLogEventById(final UUID id) {

		return masterLogEventRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	/**
	 * Recupere dernier scllement log
	 * 
	 * @return
	 */
	public Optional<LogEvent> getLastSealedLog() {
		return masterLogEventRepository.findTopByLogTypeOrderByTimestampDesc(LogEventType.S.toString());
	}
}
