package com.telino.avp.dao;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterApplicationRepository;
import com.telino.avp.entity.param.Application;

@Repository
@Transactional
public class ApplicationDao {

	@Autowired
	private MasterApplicationRepository masterApplicationRepository;

	public Application findApplication(final String applicationCode) {
		return masterApplicationRepository.findByApplicationCodeAndApplicationValidationIsTrue(applicationCode)
				.orElseThrow(EntityNotFoundException::new);
	}
}
