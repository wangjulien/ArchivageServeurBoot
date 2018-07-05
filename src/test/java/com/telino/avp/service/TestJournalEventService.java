package com.telino.avp.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.LogEventDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.journal.JournalEventService;
import com.telino.avp.service.journal.TamponHorodatageService;
import com.telino.avp.service.storage.AbstractStorageService;
import com.telino.avp.utils.Sha;

@RunWith(MockitoJUnitRunner.class)
@SpringJUnitConfig(ConfigTestService.class)
public class TestJournalEventService {

	@InjectMocks
	private JournalEventService journalEventService;

	@Mock
	private LogEventDao logEventDao;

	@Mock
	private LogArchiveDao logArchiveDao;

	@Mock
	private DocumentDao documentDao;

	@Mock
	private TamponHorodatageService tamponHorodatageService;
	
	@Mock
	private AbstractStorageService storageService;

	private static LogEvent logEvent;

	private static LogEvent logEventBefore;

	@BeforeClass
	public static void buildEntity() throws Exception {

		logEventBefore = new LogEvent();
		logEventBefore.setLogId(UUID.randomUUID());
		logEventBefore.setHorodatage(ZonedDateTime.now());

		logEvent = new LogEvent();
		logEvent.setLogId(ConfigTestService.LOG_EVENT_ID);
		logEvent.setContenu(logEventBefore.buildContent());
		logEvent.setHash(Sha.encode(logEvent.getContenu(), "utf-8"));

		TamponHorodatageService tamponHorodatageService = new TamponHorodatageService();
		tamponHorodatageService.demanderTamponHorodatage(logEvent);
	}

	@Test
	public void verify_journal() throws AvpExploitException, TSPValidationException, OperatorCreationException,
			TSPException, CMSException, IOException {

		when(logEventDao.findAllLogEventBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean()))
				.thenReturn(Arrays.asList(logEventBefore));

		journalEventService.verifyJournal(logEvent, false);

		verify(logEventDao, times(2)).findAllLogEventBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID),
				anyBoolean());
		verify(tamponHorodatageService).initTamponHorodatage(logEvent);
		verify(tamponHorodatageService).verifyTamponHorodatage(logEvent);
	}
	
	@Test
	public void sceller_journal() throws AvpExploitException {
		Document journalXml = new Document();
		
		when(logEventDao.save(any(LogEvent.class))).thenReturn(logEvent);
		when(logEventDao.findAllLogEventBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean()))
		.thenReturn(Arrays.asList(logEventBefore));
		when(storageService.archive(journalEventService, logEvent)).thenReturn(journalXml);
		
		journalEventService.scellerJournal();
		
		assertNotNull(logEvent.getTimestampTokenBytes());
		
		verify(logEventDao).findAllLogEventBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID),
				anyBoolean());
		verify(logEventDao, times(2)).save(any(LogEvent.class));
	}

	@Test
	public void build_storage_format() throws AvpExploitException {
		when(logEventDao.findAllLogEventBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean()))
				.thenReturn(Arrays.asList(logEventBefore));

		journalEventService.buildStorageFormat(logEvent);

		verify(logEventDao).findAllLogEventBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean());

	}

}
