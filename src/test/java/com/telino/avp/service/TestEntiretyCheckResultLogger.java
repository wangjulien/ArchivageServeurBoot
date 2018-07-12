package com.telino.avp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.service.journal.EntiretyCheckResultLogger;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.journal.JournalEventService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestEntiretyCheckResultLogger {

	@InjectMocks
	private EntiretyCheckResultLogger entiretyCheckResultLogger;

	@Mock
	private UserDao userDao;
	
	@Mock
	private DocumentDao documentDao;
	
	@Mock
	private JournalArchiveService journalArchiveService;

	@Mock
	private JournalEventService journalEventService;



	@Test
	public void log_error_result() throws AvpExploitException {
		UUID common = UUID.randomUUID();
		
		final Map<String, Object> input = new HashMap<>();
		final Map<UUID, FileReturnError> badDocsInUnit1 = new HashMap<>();
		badDocsInUnit1.put(common, FileReturnError.HASH_NOT_MATCH_ERROR);
		badDocsInUnit1.put(UUID.randomUUID(), FileReturnError.DECRYPT_ERROR);
		badDocsInUnit1.put(UUID.randomUUID(), FileReturnError.ENTIRETY_ERROR);
		
		final Map<UUID, FileReturnError> badDocsInUnit2 = new HashMap<>();
		badDocsInUnit2.put(common, FileReturnError.HASH_NOT_MATCH_ERROR);
		badDocsInUnit2.put(UUID.randomUUID(), FileReturnError.DECRYPT_ERROR);
		badDocsInUnit2.put(UUID.randomUUID(), FileReturnError.ENTIRETY_ERROR);
		
		entiretyCheckResultLogger.logErrorResult(input, badDocsInUnit1, badDocsInUnit2);

		verify(journalArchiveService, times(5)).setHorodatageAndSave(any(LogArchive.class));
		verify(journalEventService, times(6)).setHorodatageAndSave(any(LogEvent.class));
	}
}
