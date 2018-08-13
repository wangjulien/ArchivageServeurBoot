package com.telino.avp.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.telino.avp.TestConstants;
import com.telino.avp.dao.CommunicationDao;
import com.telino.avp.dao.RestitutionDao;
import com.telino.avp.entity.archive.Communication;
import com.telino.avp.entity.archive.CommunicationList;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Empreinte;
import com.telino.avp.entity.archive.Restitution;
import com.telino.avp.entity.archive.RestitutionList;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.DbEntityProtocol.CommunicationState;
import com.telino.avp.service.archivage.ComAndRestService;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.storage.AbstractStorageService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestComAndRestService {

	private static final UUID COM_ID = UUID.randomUUID();
	private static final UUID REST_ID = UUID.randomUUID();

	@InjectMocks
	private ComAndRestService comAndRestService;

	@Mock
	private CommunicationDao communicationDao;

	@Mock
	private RestitutionDao restitutionDao;

	@Mock
	private DocumentService documentService;

	@Mock
	private AbstractStorageService storageService;

	@Mock
	private JournalArchiveService journalArchiveService;
	
	private Document doc;
	
	private Document attestation;
	
	private Communication communication;
	
	private Restitution restitution;

	@Before
	public void buildEntity() throws AvpExploitException {
		// Mock Given
		communication = new Communication();
		communication.setCommunicationId(COM_ID);
		communication.setCommunicationStatus(CommunicationState.V);
		
		restitution = new Restitution();
		restitution.setRestitutionId(REST_ID);

		Empreinte empreinte = new Empreinte();
		empreinte.setEmpreinte("TEST EMPREINTE");

		doc = new Document();
		doc.setDocId(TestConstants.TEST_DOC_ID);
		doc.setTitle("TestTitle.pdf");
		doc.setContent("This is a test".getBytes());
		doc.setEmpreinte(empreinte);

		attestation = new Document();
		attestation.setDocId(UUID.randomUUID());
		attestation.setTitle("AttestationTitile.pdf");
		attestation.setContent("This is an attestation".getBytes());

		CommunicationList communicationList = new CommunicationList();
		communicationList.setDocument(doc);
		communication.addCommunicationList(communicationList);
		
		RestitutionList restitutionList = new RestitutionList();
		restitutionList.setDocument(doc);
		restitution.addRestitutionList(restitutionList);

		when(communicationDao.findByComId(COM_ID)).thenReturn(communication);
		when(restitutionDao.findByRestId(REST_ID)).thenReturn(restitution);
		
		when(storageService.get(TestConstants.TEST_DOC_ID)).thenReturn(doc);
		when(storageService.archive(anyString(), eq(doc))).thenReturn(attestation);
		when(storageService.get(attestation.getDocId())).thenReturn(attestation);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void get_communication() throws AvpExploitException {

		Map<String, Object> input = new HashMap<>();
		input.put("communicationid", COM_ID.toString());
		Map<String, Object> result = new HashMap<>();

		comAndRestService.getCommunication(input, result);

		// Check
		assertTrue(((byte[]) result.get("content")).length > 0);

		// The daos are called correctly
		verify(communicationDao).findByComId(COM_ID);
		verify(storageService).get(TestConstants.TEST_DOC_ID);
		verify(storageService).archive(anyString(), eq(doc));
		verify(storageService).get(attestation.getDocId());
		verify(documentService).control(any(Map.class));
		verify(journalArchiveService).setHorodatageAndSave(any(LogArchive.class));
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void get_restitution() throws AvpExploitException {

		Map<String, Object> input = new HashMap<>();
		input.put("restitutionid", REST_ID.toString());
		Map<String, Object> result = new HashMap<>();

		comAndRestService.getRestitution(input, result);

		// Check
		assertTrue(((byte[]) result.get("content")).length > 0);

		// The daos are called correctly
		verify(restitutionDao).findByRestId(REST_ID);
		verify(storageService).get(TestConstants.TEST_DOC_ID);
		verify(storageService).archive(anyString(), eq(doc));
		verify(storageService).get(attestation.getDocId());
		verify(documentService).control(any(Map.class));
		verify(journalArchiveService).setHorodatageAndSave(any(LogArchive.class));
	}

}
