package com.telino.avp.dao;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterCommunicationRepository;
import com.telino.avp.dao.mirrordao.MirrorCommunicationRepository;
import com.telino.avp.entity.archive.Communication;

@Repository
@Transactional
public class CommunicationDao {

	@Autowired
	private MasterCommunicationRepository masterCommunicationRepository;
	
	@Autowired
	private MirrorCommunicationRepository mirrorCommunicationRepository;
	

	public void save(final Communication comm) {
		if (Objects.isNull(comm.getCommunicationId()))
			comm.setCommunicationId(UUID.randomUUID());
		
		masterCommunicationRepository.save(comm);
		mirrorCommunicationRepository.save(comm);
	}


	public Communication findByComId(final UUID comId) {
		return masterCommunicationRepository.findById(comId).orElseThrow(EntityNotFoundException::new);
	}
}
