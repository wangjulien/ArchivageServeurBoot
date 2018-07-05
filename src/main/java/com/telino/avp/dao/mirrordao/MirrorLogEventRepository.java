package com.telino.avp.dao.mirrordao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.auxil.LogEvent;

public interface MirrorLogEventRepository extends JpaRepository<LogEvent, UUID> {
	
	public List<LogEvent> findAllLogEventByTimestampForContent(@Param("timestamp") ZonedDateTime timestamp,
			@Param("evtTypeS") String arcTypeS);
}
