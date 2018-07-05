package com.telino.avp.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
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
import com.telino.avp.dao.DraftDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.entity.archive.Empreinte;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.journal.TamponHorodatageService;
import com.telino.avp.service.storage.AbstractStorageService;
import com.telino.avp.utils.Sha;

@RunWith(MockitoJUnitRunner.class)
@SpringJUnitConfig(ConfigTestService.class)
public class TestJournalArchivageService {

	@InjectMocks
	private JournalArchiveService journalArchiveService;

	@Mock
	private LogArchiveDao logArchiveDao;

	@Mock
	private DraftDao draftDao;

	@Mock
	private DocumentDao documentDao;
	
	@Mock
	private UserDao userDao;

	@Mock
	private TamponHorodatageService tamponHorodatageService;
	
	@Mock
	private AbstractStorageService storageService;

	private static LogArchive logArchive;

	private static LogArchive logArchiveBefore;

	@BeforeClass
	public static void buildEntity() throws Exception {

		logArchiveBefore = new LogArchive();
		logArchiveBefore.setLogId(UUID.randomUUID());
		logArchiveBefore.setHorodatage(ZonedDateTime.now());

		logArchive = new LogArchive();
		logArchive.setLogId(ConfigTestService.LOG_EVENT_ID);
		logArchive.setContenu(logArchiveBefore.buildContent());
		logArchive.setHash(Sha.encode(logArchive.getContenu(), "utf-8"));

		TamponHorodatageService tamponHorodatageService = new TamponHorodatageService();
		tamponHorodatageService.demanderTamponHorodatage(logArchive);
	}

	@Test
	public void verify_journal() throws AvpExploitException, TSPValidationException, OperatorCreationException,
			TSPException, CMSException, IOException {

		when(logArchiveDao.findAllLogArchiveBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean()))
				.thenReturn(Arrays.asList(logArchiveBefore));

		journalArchiveService.verifyJournal(logArchive, false);

		verify(logArchiveDao, times(2)).findAllLogArchiveBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID),
				anyBoolean());
		verify(tamponHorodatageService).initTamponHorodatage(logArchive);
		verify(tamponHorodatageService).verifyTamponHorodatage(logArchive);
	}
	
	@Test
	public void sceller_journal() throws AvpExploitException {
		Empreinte empreinte = new Empreinte();
		Document journalXml = new Document();
		journalXml.setDocId(UUID.randomUUID());
		journalXml.setEmpreinte(empreinte);
		
		// pre treatment before sealing the log archive : archive all the documents ready for achievement
		when(documentDao.getAllDocIdReadyForArchive()).thenReturn(Arrays.asList(journalXml));
		when(storageService.check(journalXml.getDocId(), true)).thenReturn(true);
		when(storageService.archive(anyString(), eq(journalXml))).thenReturn(journalXml);
		// sealing
		when(logArchiveDao.save(any(LogArchive.class))).thenReturn(logArchive);
		when(logArchiveDao.findAllLogArchiveBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean()))
		.thenReturn(Arrays.asList(logArchiveBefore));
		when(storageService.archive(journalArchiveService, logArchive)).thenReturn(journalXml);
		// post sealing
		when(draftDao.hasDraft(journalXml.getDocId())).thenReturn(Optional.of(new Draft()));
		
		journalArchiveService.scellerJournal();
		
		assertNotNull(logArchive.getTimestampTokenBytes());
		
		verify(documentDao).getAllDocIdReadyForArchive();
		verify(storageService).check(journalXml.getDocId(), true);
		verify(storageService).archive(anyString(), eq(journalXml));
		verify(draftDao).hasDraft(journalXml.getDocId());
		
		verify(logArchiveDao).findAllLogArchiveBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID),
				anyBoolean());
		verify(logArchiveDao, times(3)).save(any(LogArchive.class));
	}

	@Test
	public void build_storage_format() throws AvpExploitException {
		when(logArchiveDao.findAllLogArchiveBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean()))
				.thenReturn(Arrays.asList(logArchiveBefore));

		journalArchiveService.buildStorageFormat(logArchive);

		verify(logArchiveDao).findAllLogArchiveBeforeLogIdForContent(eq(ConfigTestService.LOG_EVENT_ID), anyBoolean());

	}

}
