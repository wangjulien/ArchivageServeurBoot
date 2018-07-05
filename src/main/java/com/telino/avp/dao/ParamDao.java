package com.telino.avp.dao;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterParamRepository;
import com.telino.avp.dao.mirrordao.MirrorParamRepository;
import com.telino.avp.entity.param.Param;

@Repository
@Transactional
public class ParamDao {

	@Autowired
	private MasterParamRepository masterParamRepository;

	@Autowired
	private MirrorParamRepository mirrorParamRepository;

	public void saveParam(final Param param) {
		masterParamRepository.save(param);
		mirrorParamRepository.save(param);
	}

	public Param getInitialParam(final Integer id) {
		return masterParamRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}
}
