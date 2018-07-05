package com.telino.avp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.LogEventDao;
import com.telino.avp.dao.mirrordao.MirrorLogEventRepository;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;
import com.telino.avp.protocol.DbEntityProtocol.LogEventState;
import com.telino.avp.protocol.DbEntityProtocol.LogEventType;

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
public class TestLogEventRepository {

	@Autowired
	private LogEventDao logEventDao;

	@Autowired
	private LogArchiveDao logArchiveDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private MirrorLogEventRepository mirrorLogEventRepository;

	private LogEvent logEventC;
	private LogEvent logEventS;
	private LogEvent logEventAfter;

	@Before
	public void buildEntity() {

		Document archive = new Document();
		archive.setDocId(ConfigTestRepository.TEST_DOC_ID);

		documentDao.saveMetaDonneesDocument(archive);

		// Log de controle integralite du document
		logEventC = new LogEvent();
		logEventC.setLogId(UUID.randomUUID());
		logEventC.setStatExp(LogEventState.I);
		logEventC.setArchive(archive);
		logEventC.setLogType(LogEventType.C.toString());

		LogArchive logArchiveC = new LogArchive();
		logArchiveC.setLogId(UUID.randomUUID());
		logArchiveC.setDocument(archive);
		logArchiveC.setHorodatage(ZonedDateTime.now());
		logArchiveC.setLogType(LogArchiveType.C.toString());

		// Log de scellement de journaux
		logEventS = new LogEvent();
		logEventS.setLogId(UUID.randomUUID());
		logEventS.setHorodatage(ZonedDateTime.now());
		logEventS.setLogType(LogEventType.S.toString());

		// Log apres scellement de journaux
		logEventAfter = new LogEvent();
		logEventAfter.setLogId(UUID.randomUUID());
		logEventAfter.setStatExp(LogEventState.I);

		// Persister dans les deux bases
		logEventDao.save(logEventC);
		logEventDao.save(logEventS);
		logArchiveDao.save(logArchiveC);
		logEventDao.save(logEventAfter);
	}

	@Test
	public void dao_Should_Save_In_Both_Db() {
		assertNotNull(logEventDao);

		// Verifier le save
		assertEquals(logEventAfter.getLogId(), logEventDao.findLogEventById(logEventAfter.getLogId()).getLogId());
		assertTrue(mirrorLogEventRepository.findById(logEventAfter.getLogId()).isPresent());
	}

	@Test
	public void find_all_log_before_logid() {
		List<LogEvent> listMaster = logEventDao.findAllLogEventBeforeLogIdForContent(logEventAfter.getLogId(), false);
		List<LogEvent> listMirror = logEventDao.findAllLogEventBeforeLogIdForContent(logEventAfter.getLogId(), true);

		// Il doit trouver le log apres scellement (y compris le scellement log)
		assertEquals(listMaster.size(), 1);
		assertEquals(listMirror.size(), 1);

		Set<UUID> logIdsMaster = listMaster.stream().map(LogEvent::getLogId).collect(Collectors.toSet());
		Set<UUID> logIdsMirror = listMaster.stream().map(LogEvent::getLogId).collect(Collectors.toSet());

		assertTrue(logIdsMaster.contains(logEventS.getLogId()));
		assertTrue(logIdsMirror.contains(logEventS.getLogId()));

	}

	@Test
	public void find_all_archiveid_failed_check_entirety() {
		// Verifier le findHash
		assertEquals(logEventDao.findAllArchiveIdFailedCheckEntirety().get(0).getLogId(), logEventC.getLogId());
	}

	@Test
	public void terminate_exploited_event() {
		logEventDao.terminateExploitedEvent(Arrays.asList(logEventAfter));

		assertEquals(logEventDao.findLogEventById(logEventAfter.getLogId()).getStatExp(), LogEventState.T);
		assertEquals(mirrorLogEventRepository.findById(logEventAfter.getLogId()).get().getStatExp(), LogEventState.T);
	}

	@Test
	public void get_last_sealed_log() {
		LogEvent logEvent = logEventDao.getLastSealedLog().orElseThrow(EntityNotFoundException::new);

		assertEquals(logEvent.getLogId(), logEventS.getLogId());

		assertFalse(logEvent.getHorodatage().isBefore(ZonedDateTime.now().minus(1, ChronoUnit.HOURS)));
	}
}
