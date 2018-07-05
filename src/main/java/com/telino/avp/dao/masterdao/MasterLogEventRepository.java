package com.telino.avp.dao.masterdao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.auxil.LogEvent;

public interface MasterLogEventRepository extends JpaRepository<LogEvent, UUID> {

	public List<LogEvent> findAllLogEventByTimestampForContent(@Param("timestamp") ZonedDateTime timestamp,
			@Param("evtTypeS") String arcTypeS);

	@Query(value = "select distinct ev.* from log_event ev join log_archive ar "
			+ "on ev.archiveid = ar.docid  and ar.logtype = :arcType and ev.logtype = :evtType and ev.statexp = :evtState "
			+ "order by ev.timestamp", nativeQuery = true)
	public List<LogEvent> findAllArchiveIdFailedCheckEntirety(@Param("arcType") String arcType,
			@Param("evtType") String evtType, @Param("evtState") String evtState);

	public Optional<LogEvent> findTopByLogTypeOrderByTimestampDesc(String evtTypeS);
}
