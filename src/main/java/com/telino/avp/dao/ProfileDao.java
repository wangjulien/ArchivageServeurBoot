package com.telino.avp.dao;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterProfileRepository;
import com.telino.avp.dao.mirrordao.MirrorProfileRepository;
import com.telino.avp.entity.context.Profile;

@Repository
@Transactional
public class ProfileDao {

	@Autowired
	private MasterProfileRepository masterProfileRepository;

	@Autowired
	private MirrorProfileRepository mirrorProfileRepository;

	public void saveProfile(final Profile profile) {
		
//		if (Objects.isNull(profile.getParId()))
//			profile.setParId(UUID.randomUUID());
		
		masterProfileRepository.save(profile);
		mirrorProfileRepository.save(profile);
	}

	public Profile findByParId(final Integer parId) {
		return masterProfileRepository.findById(parId).orElseThrow(EntityNotFoundException::new);
	}
	
	public void flush() {
		masterProfileRepository.flush();
		mirrorProfileRepository.flush();
	}
}
