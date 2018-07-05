package com.telino.avp.dao;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterLogArchiveRepository;
import com.telino.avp.dao.mirrordao.MirrorLogArchiveRepository;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;

/**
 * Surcouche DAO pour LogArchive
 * 
 * @author Jiliang.WANG
 *
 */
@Repository
@Transactional
public class LogArchiveDao {

	@Autowired
	private MasterLogArchiveRepository masterLogArchiveRepository;

	@Autowired
	private MirrorLogArchiveRepository mirrorLogArchiveRepository;

	/**
	 * Recupere le LogArchive d'un Doc dont type A
	 * 
	 * @param docId
	 * @param isMirror
	 * @return
	 */
	public String findHashForDocId(final UUID docId, final boolean isMirror) {
		if (isMirror)
			return mirrorLogArchiveRepository.findHashForDocId(docId, LogArchiveType.A.toString());
		else
			return masterLogArchiveRepository.findHashForDocId(docId, LogArchiveType.A.toString());
	}

	/**
	 * Recupere LogArchive de scellement pour un document, soit dans DB master m
	 * soit dans DB mirror
	 * 
	 * @param docId
	 * @param isMirror
	 * @return
	 */
	public LogArchive findLogArchiveForDocId(final UUID docId, final boolean isMirror) {
		if (isMirror)
			return mirrorLogArchiveRepository
					.findLogArchiveForDocId(docId, LogArchiveType.A.toString(), LogArchiveType.S.toString())
					.orElseThrow(EntityNotFoundException::new);
		else
			return masterLogArchiveRepository
					.findLogArchiveForDocId(docId, LogArchiveType.A.toString(), LogArchiveType.S.toString())
					.orElseThrow(EntityNotFoundException::new);
	}

	/**
	 * Recupere tous les LogArchive depuis dernier Scellement, pour creer un Content
	 * a sceller - soit dans DB master m soit dans DB mirror
	 * 
	 * @param logId
	 * @param isMirror
	 * @return
	 */
	public List<LogArchive> findAllLogArchiveBeforeLogIdForContent(final UUID logId, final boolean isMirror) {

		// find first the LogArchive by logId
		LogArchive logArchive = masterLogArchiveRepository.findById(logId).orElseThrow(EntityNotFoundException::new);

		if (isMirror)
			return mirrorLogArchiveRepository.findAllLogArchiveByTimestampForContent(logArchive.getTimestamp(),
					LogArchiveType.S.toString());
		else
			return masterLogArchiveRepository.findAllLogArchiveByTimestampForContent(logArchive.getTimestamp(),
					LogArchiveType.S.toString());
	}

	/**
	 * Enregistrer dans les deux DB un entity LogArchive
	 * 
	 * @param logArchive
	 * @return
	 */
	public LogArchive save(LogArchive logArchive) {

		if (Objects.isNull(logArchive.getLogId()))
			logArchive.setLogId(UUID.randomUUID());

		// Timestamp to have a chrono order
		if (Objects.isNull(logArchive.getTimestamp()))
			logArchive.setTimestamp(ZonedDateTime.now());

		// Persist
		logArchive = masterLogArchiveRepository.save(logArchive);

		logArchive = mirrorLogArchiveRepository.save(logArchive);

		return logArchive;
	}

	/**
	 * Recupere entity par ID
	 * 
	 * @param id
	 * @return
	 */
	public LogArchive findLogArchiveById(final UUID id) {
		return masterLogArchiveRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	/**
	 * Recupere tous les ID des LogArchive type sellement pour une liste de Docs
	 * 
	 * @param docIds
	 * @return
	 */
	public Set<LogArchive> getSellementLogArchiveForDocs(List<UUID> docIds) {

		Set<LogArchive> logIds = new HashSet<>();

		for (UUID docId : docIds) {
			// On cherche dans DB master
			try {
				LogArchive logArchive = findLogArchiveForDocId(docId, false);

				logIds.add(logArchive);
			} catch (EntityNotFoundException e) {
				// If there isn't any log for the doc, continue
			}
		}
		return logIds;
	}

	/**
	 * Recupere dernier scllement log
	 * 
	 * @return
	 */
	public Optional<LogArchive> getLastSealedLog() {
		return masterLogArchiveRepository.findTopByLogTypeOrderByTimestampDesc(LogArchiveType.S.toString());
	}
}
