package com.telino.avp.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.paramdao.SystEnvDao;
import com.telino.avp.entitysyst.SystEnv;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConfigTestRepository.class })
@Transactional
public class TestSystEnvDao {
	
	@Autowired
	private SystEnvDao systEnvDao;
	
	@Before
	public void buildEntity() {

		// Persister
		SystEnv systEnv = new SystEnv();
		systEnv.setEnvId(-10);
		systEnv.setBgsOn(true);

		systEnvDao.save(systEnv);
	}

	
	@Test
	public void find_all_by_bgs_on_is_true() {
		List<SystEnv> envs = systEnvDao.findAllByBgsOnIsTrue();
		
		assertTrue(envs.size() > 0);
	}

}
