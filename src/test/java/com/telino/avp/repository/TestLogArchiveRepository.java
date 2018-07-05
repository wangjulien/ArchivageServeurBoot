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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConfigTestRepository.class })
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
	public void buildEntity() {

		Document archive = new Document();
		archive.setDocId(ConfigTestRepository.TEST_DOC_ID);

		documentDao.saveMetaDonneesDocument(archive);

		// Log de archivage du document
		logArchiveA = new LogArchive();
		logArchiveA.setLogId(UUID.randomUUID());
		logArchiveA.setDocument(archive);
		logArchiveA.setHash(ConfigTestRepository.TEST_HASH);
		logArchiveA.setLogType(LogArchiveType.A.toString());

		// Log de scellement de journaux
		logArchiveS = new LogArchive();
		logArchiveS.setLogId(UUID.randomUUID());
		logArchiveS.setDocument(archive);
		logArchiveS.setHorodatage(ZonedDateTime.now());
		logArchiveS.setLogType(LogArchiveType.S.toString());

		// Log apres scellement de journaux
		logArchiveAfter = new LogArchive();
		logArchiveAfter.setLogId(UUID.randomUUID());
		logArchiveAfter.setDocument(null);
		logArchiveAfter.setHash(ConfigTestRepository.TEST_HASH);
		logArchiveAfter.setLogType(LogArchiveType.A.toString());

		// Persister dans les deux bases
		logArchiveDao.save(logArchiveA);
		logArchiveDao.save(logArchiveS);
		logArchiveDao.save(logArchiveAfter);
	}

	@Test
	public void dao_Should_Save_In_Both_Db() {
		assertNotNull(logArchiveDao);

		// Verifier le save
		assertEquals(logArchiveAfter.getLogId(), logArchiveDao.findLogArchiveById(logArchiveAfter.getLogId()).getLogId());
		assertTrue(mirrorLogArchiveRepository.findById(logArchiveAfter.getLogId()).isPresent());
	}

	@Test
	public void find_hash_for_doc() {
		// Verifier le findHash
		assertEquals(logArchiveDao.findHashForDocId(ConfigTestRepository.TEST_DOC_ID, false),
				ConfigTestRepository.TEST_HASH);
		assertEquals(logArchiveDao.findHashForDocId(ConfigTestRepository.TEST_DOC_ID, true),
				ConfigTestRepository.TEST_HASH);

	}

	@Test
	public void find_seal_log_archive_for_doc() {
		// Verifier le findHash
		assertEquals(logArchiveDao.findLogArchiveForDocId(ConfigTestRepository.TEST_DOC_ID, false).getLogId(),
				logArchiveS.getLogId());
		assertEquals(logArchiveDao.findLogArchiveForDocId(ConfigTestRepository.TEST_DOC_ID, true).getLogId(),
				logArchiveS.getLogId());

	}

	@Test
	public void find_all_log_before_logid() {
		List<LogArchive> listMaster = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(logArchiveAfter.getLogId(),
				false);
		List<LogArchive> listMirror = logArchiveDao.findAllLogArchiveBeforeLogIdForContent(logArchiveAfter.getLogId(),
				true);

		// Il doit trouver le log apres scellement (y compris le scellement log)
		assertEquals(listMaster.size(), 1);
		assertEquals(listMirror.size(), 1);

		Set<UUID> logIdsMaster = listMaster.stream().map(LogArchive::getLogId).collect(Collectors.toSet());
		Set<UUID> logIdsMirror = listMaster.stream().map(LogArchive::getLogId).collect(Collectors.toSet());

		assertTrue(logIdsMaster.contains(logArchiveS.getLogId()));
		assertTrue(logIdsMirror.contains(logArchiveS.getLogId()));

	}

	@Test
	public void get_last_sealed_log() {
		LogArchive logArchive = logArchiveDao.getLastSealedLog();

		assertEquals(logArchive.getLogId(), logArchiveS.getLogId());

		assertFalse(logArchive.getHorodatage().isBefore(ZonedDateTime.now().minus(1, ChronoUnit.HOURS)));
	}
}
