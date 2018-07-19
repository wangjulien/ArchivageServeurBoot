package com.telino.avp.dao;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterDocTypeRepository;
import com.telino.avp.dao.mirrordao.MirrorDocTypeRepository;
import com.telino.avp.entity.context.DocType;

@Repository
@Transactional
public class DocTypeDao {

	@Autowired
	private MasterDocTypeRepository masterDocTypeRepository;

	@Autowired
	private MirrorDocTypeRepository mirrorDocTypeRepository;

	public void saveDocType(final DocType docType) {
		// if (Objects.isNull(docType.getDocTypeId()))
		// docType.setDocTypeId(UUID.randomUUID());

		masterDocTypeRepository.save(docType);
		mirrorDocTypeRepository.save(docType);
	}

	public DocType findByDocTypeArchivageAndCategorie(final String docType, final String categorie) {
		return masterDocTypeRepository.findByDocTypeArchivageDocTypeArchivageAndCategorie(docType, categorie)
				.orElseThrow(EntityNotFoundException::new);
	}

	public DocType findByDocTypeId(final Integer docTypeId) {
		return masterDocTypeRepository.findById(docTypeId).orElseThrow(EntityNotFoundException::new);
	}
}
