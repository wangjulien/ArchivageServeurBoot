package com.telino.avp.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.telino.avp.config.multids.MasterDsContextHolder;
import com.telino.avp.config.multids.MirrorDsContextHolder;
import com.telino.avp.dao.ExpTaskDao;
import com.telino.avp.dao.masterdao.MasterExpTaskRepository;
import com.telino.avp.dao.mirrordao.MirrorExpTaskRepository;
import com.telino.avp.entity.auxil.ExpComment;
import com.telino.avp.entity.auxil.ExpTask;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskType;

/**
 * Test le chargement de DataSource et Data Transaction context - Coucher
 * persistance fonctionne - Transaction bien rollback si Exception levee
 * 
 * @author jwang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConfigTestRepository.class })
public class TestExpTaskRepository {
	
	private static final String MASTER_DS_ID = "AVP_JW";
	private static final String MIRROR_DS_ID = "AVP_JW_M";

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private ExpTaskDao expTaskRepository;

	@Autowired
	private MasterExpTaskRepository masterExpTaskRepository;

	@Autowired
	private MirrorExpTaskRepository mirrorExpTaskRepository;

	private ExpTask expTaskToSave;

	@Before
	public void buildEntity() {
		
		MasterDsContextHolder.setCurrentDsId(MASTER_DS_ID);
		MirrorDsContextHolder.setCurrentDsId(MIRROR_DS_ID);
		
//		Document archive = new Document();
//		archive.setDocId(TestRepositoryConfig.TEST_DOC_ID);
		
		ExpComment com = new ExpComment();
		com.setComDate(ZonedDateTime.now());
		com.setComment("This is a comment");

		expTaskToSave = new ExpTask();
		expTaskToSave.setTaskId(UUID.randomUUID());
		expTaskToSave.setHorodatage(ZonedDateTime.now());
		expTaskToSave.setDateDeb(ZonedDateTime.now());
//		expTaskToSave.setDocument(archive);
		expTaskToSave.setTaskType(ExpTaskType.RELAUNCH_FILE_ENTIRETY_CHECK);
		expTaskToSave.addComment(com);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void dao_Should_Save_In_Both_Db() {
		assertNotNull(expTaskRepository);

		expTaskRepository.saveExpTask(expTaskToSave);

		// expTaskRepository.saveExpTasks(Arrays.asList(expTaskToSave));

		assertTrue(masterExpTaskRepository.findById(expTaskToSave.getTaskId()).isPresent()
				&& mirrorExpTaskRepository.findById(expTaskToSave.getTaskId()).isPresent());
	}

	@Test
	public void service_Should_Save_And_Find_Task_In_Two_Ds() {
		assertNotNull(expTaskRepository);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				expTaskRepository.saveExpTask(expTaskToSave);
//				masterExpTaskRepository.save(expTaskToSave);

				// Mannully declencher rollback
				status.setRollbackOnly();

			}
		});

		assertFalse(masterExpTaskRepository.findById(expTaskToSave.getTaskId()).isPresent());
		assertFalse(mirrorExpTaskRepository.findById(expTaskToSave.getTaskId()).isPresent());
	}
}
