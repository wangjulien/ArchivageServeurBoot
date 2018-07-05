package com.telino.avp.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterBgServiceRepository;
import com.telino.avp.dao.mirrordao.MirrorBgServiceRepository;
import com.telino.avp.entity.param.BgService;

@Repository
@Transactional
public class BgServiceDao {

	@Autowired
	private MasterBgServiceRepository masterBgServiceRepository;
	
	@Autowired
	private MirrorBgServiceRepository mirrorBgServiceRepository;
	

	public List<BgService> findAll() {
		return masterBgServiceRepository.findAll();
	}

	public void save(final BgService bgs) {
		masterBgServiceRepository.save(bgs);
		mirrorBgServiceRepository.save(bgs);
	}
}
