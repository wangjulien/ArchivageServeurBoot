package com.telino.avp.dao.mirrordao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.Draft;

public interface MirrorDraftRepository extends JpaRepository<Draft, Long> {

	void deleteAllByDocIdIn(List<UUID> docIds);

}
