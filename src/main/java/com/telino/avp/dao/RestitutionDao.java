package com.telino.avp.dao;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterRestitutionRepository;
import com.telino.avp.dao.mirrordao.MirrorRestitutionRepository;
import com.telino.avp.entity.archive.Restitution;

@Repository
@Transactional
public class RestitutionDao {

	@Autowired
	private MasterRestitutionRepository masterRestitutionRepository;
	
	@Autowired
	private MirrorRestitutionRepository mirrorRestitutionRepository;
	

	public void save(final Restitution rest) {
		if (Objects.isNull(rest.getRestitutionId()))
			rest.setRestitutionId(UUID.randomUUID());
		
		masterRestitutionRepository.save(rest);
		mirrorRestitutionRepository.save(rest);
	}


	public Restitution findByRestId(final UUID restId) {
		return masterRestitutionRepository.findById(restId).orElseThrow(EntityNotFoundException::new);
	}
}
