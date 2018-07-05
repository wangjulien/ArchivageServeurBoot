package com.telino.avp.dao.masterdao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.Draft;

public interface MasterDraftRepository extends JpaRepository<Draft, UUID> {

	Optional<Draft> findByArchiveId(UUID archiveid);

	void deleteAllByDocIdIn(List<UUID> docIds);
}
