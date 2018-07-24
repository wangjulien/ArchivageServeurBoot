package com.telino.avp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.TestConstants;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.mirrordao.MirrorLogArchiveRepository;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;

/**
 * Test le chargement de DataSource et Data Transaction context - Coucher
 * persistance fonctionne - Transaction bien rollback si Exception levee
 * 
 * @author jwang
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TestLogArchiveRepository {

	@Autowired
	private LogArchiveDao logArchiveDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private MirrorLogArchiveRepository mirrorLogArchiveRepository;

	private LogArchive logArchiveA;
	private LogArchive logArchiveS;
	private LogArchive logArchiveAfter;

	@Before
	public void buildEntity() throws InterruptedException {

		Document archive = new Document();
		archive.setDocId(TestConstants.TEST_DOC_ID);

		documentDao.saveMetaDonneesDocument(archive);

		// Log de archivage du document
		logArchiveA = new LogArchive();
		logArchiveA.setLogId(UUID.randomUUID());
		logArchiveA.setDocument(archive);
		logArchiveA.setHash(TestConstants.TEST_HASH);
		logArchiveA.setLogType(LogArchiveType.A.toString());

		// Persister dans les deux bases
		logArchiveDao.save(logArchiveA);
		Thread.sleep(10);
		
		// Log de scellement de journaux
		logArchiveS = new LogArchive();
		logArchiveS.setLogId(UUID.randomUUID());
		logArchiveS.setDocument(archive);
		logArchiveS.setHorodatage(ZonedDateTime.now());
		logArchiveS.setLogType(LogArchiveType.S.toString());
		
		logArchiveDao.save(logArchiveS);
		Thread.sleep(10);
		
		// Log apres scellement de journaux
		logArchiveAfter = new LogArchive();
		logArchiveAfter.setLogId(UUID.randomUUID());
		logArchiveAfter.setDocument(null);
		logArchiveAfter.setHash(TestConstants.TEST_HASH);
		logArchiveAfter.setLogType(LogArchiveType.L.toString());

		logArchiveDao.save(logArchiveAfter);
	}

	@Test
	public void dao_Should_Save_In_Both_Db() {
		assertNotNull(logArchiveDao);

		// Verifier le save
		assertEquals(logArchiveAfter.getLogId(),
				logArchiveDao.findLogArchiveById(logArchiveAfter.getLogId()).getLogId());
		assertTrue(mirrorLogArchiveRepository.findById(logArchiveAfter.getLogId()).isPresent());
	}

	@Test
	public void find_hash_for_doc() {
		// Verifier le findHash
		assertEquals(logArchiveDao.findHashForDocId(TestConstants.TEST_DOC_ID, false), TestConstants.TEST_HASH);
		assertEquals(logArchiveDao.findHashForDocId(TestConstants.TEST_DOC_ID, true), TestConstants.TEST_HASH);

	}

	@Test
	public void find_seal_log_archive_for_doc() {
		// Verifier le findHash
		assertEquals(logArchiveS.getLogId(),
				logArchiveDao.findLogArchiveForDocId(TestConstants.TEST_DOC_ID, false).getLogId());
		assertEquals(logArchiveS.getLogId(),
				logArchiveDao.findLogArchiveForDocId(TestConstants.TEST_DOC_ID, true).getLogId());

	}

	@Test
	public void find_all_log_before_logid() {
		List<LogArchive> listMaster = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(logArchiveAfter.getLogId(),
				false);
		List<LogArchive> listMirror = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(logArchiveAfter.getLogId(),
				true);

		// Il doit trouver le log apres scellement (y compris le scellement log)
		assertEquals(1, listMaster.size());
		assertEquals(1, listMirror.size());

		Set<UUID> logIdsMaster = listMaster.stream().map(LogArchive::getLogId).collect(Collectors.toSet());
		Set<UUID> logIdsMirror = listMaster.stream().map(LogArchive::getLogId).collect(Collectors.toSet());

		assertTrue(logIdsMaster.contains(logArchiveS.getLogId()));
		assertTrue(logIdsMirror.contains(logArchiveS.getLogId()));

	}

	@Test
	public void get_last_sealed_log() {
		LogArchive logArchive = logArchiveDao.getLastSealedLog().orElseThrow(EntityNotFoundException::new);

		assertEquals(logArchive.getLogId(), logArchiveS.getLogId());

		assertFalse(logArchive.getHorodatage().isBefore(ZonedDateTime.now().minus(1, ChronoUnit.HOURS)));
	}
}
