package com.telino.avp.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.ParamDao;
import com.telino.avp.dao.mirrordao.MirrorParamRepository;
import com.telino.avp.entity.param.Param;
import com.telino.avp.entity.param.StorageParam;

/**
 * Test chargement et mise a jour Param et StorageParam
 * 
 * @author jwang
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TestParamRepository {

	private static final String ID_STORAGE_MASTER = "Test master ID storage";
	private static final String ID_STORAGE_MIRROR = "Test mirror ID storage";

	@Autowired
	private ParamDao paramDao;

	@Autowired
	private MirrorParamRepository mirrorParamRepository;

	private Param appParam;

	@Before
	public void buildEntity() {

		StorageParam master = new StorageParam();
		master.setParamId(-1);
		StorageParam mirror = new StorageParam();
		mirror.setParamId(-2);

		appParam = new Param();
		appParam.setParamId(-1);
		appParam.setMasterStorageParam(master);
		appParam.setMirrorStorageParam(mirror);

		// Persister
		paramDao.saveParam(appParam);
	}

	@Test
	public void update_idstorage_should_be_saved() {
		appParam.getMasterStorageParam().setIdStorage(ID_STORAGE_MASTER);
		appParam.getMirrorStorageParam().setIdStorage(ID_STORAGE_MIRROR);
		
		paramDao.saveParam(appParam);

		assertEquals(ID_STORAGE_MASTER,
				paramDao.getInitialParam(appParam.getParamId()).getMasterStorageParam().getIdStorage());
		assertEquals(ID_STORAGE_MASTER,
				mirrorParamRepository.findById(appParam.getParamId()).get().getMasterStorageParam().getIdStorage());
		
		assertEquals(ID_STORAGE_MIRROR,
				paramDao.getInitialParam(appParam.getParamId()).getMirrorStorageParam().getIdStorage());
		assertEquals(ID_STORAGE_MIRROR,
				mirrorParamRepository.findById(appParam.getParamId()).get().getMirrorStorageParam().getIdStorage());
	}
}
