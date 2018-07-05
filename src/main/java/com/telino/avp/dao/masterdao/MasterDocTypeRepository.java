package com.telino.avp.dao.masterdao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.context.DocType;

public interface MasterDocTypeRepository extends JpaRepository<DocType, Integer> {

	public Optional<DocType> findByDocTypeArchivageAndCategorie(String docType, String categorie);
}
