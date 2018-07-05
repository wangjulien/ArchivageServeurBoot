package com.telino.avp.dao.masterdao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.context.Profile;

public interface MasterDocumentRepository extends JpaRepository<Document, UUID> {

	public List<Document> findAllDocIdByStatut(@Param("statut") int statut);

	public int countByStatut(int statut);
	
	public List<Document> findAllByStatutOrderByTimestampDesc(int statut, Pageable pageable);

	public long countByEmpreinteEmpreinte(String empreinte);

	public List<Document> getAllByArchiveEndBeforeAndLogicaldeleteIsFalse(ZonedDateTime now);

	public List<Document> findTop2ByProfileInOrderByTimestampDesc(List<Profile> profiles);

	public Optional<Document> findByMd5AndDomnNom(String md5, String domnNom);
}
