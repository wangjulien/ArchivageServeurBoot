package com.telino.avp.dao.masterdao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.auxil.LogArchive;

public interface MasterLogArchiveRepository extends JpaRepository<LogArchive, UUID> {

	public List<LogArchive> findAllLogArchiveByTimestampForContent(@Param("timestamp") ZonedDateTime timestamp,
			@Param("arcTypeS") String arcTypeS);

	public Optional<LogArchive> findLogArchiveForDocId(@Param("docid") UUID docId, @Param("arcTypeA") String arcTypeA,
			@Param("arcTypeS") String arcTypeS);

	public String findHashForDocId(@Param("docid") UUID docId, @Param("arcTypeA") String arcTypeA);

	public Optional<LogArchive> findTopByLogTypeOrderByTimestampDesc(String arcTypeS);
}
