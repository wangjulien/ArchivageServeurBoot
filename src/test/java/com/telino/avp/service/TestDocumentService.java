package com.telino.avp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telino.avp.TestConstants;
import com.telino.avp.dao.DepotDao;
import com.telino.avp.dao.DocTypeDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.DraftDao;
import com.telino.avp.dao.ProfileDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.entity.archive.Empreinte;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.context.DocType;
import com.telino.avp.entity.context.MimeType;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.entity.context.Type;
import com.telino.avp.entity.context.User;
import com.telino.avp.entity.param.Param;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.service.archivage.UserProfileRightService;
import com.telino.avp.service.journal.EntiretyCheckResultLogger;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.journal.JournalEventService;
import com.telino.avp.service.storage.AbstractStorageService;
import com.telino.avp.tools.RemoteCall;

import CdmsApi.client.SqlInfo;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestDocumentService {

	private static final Integer PROFILE_ID = -20;
	private static final String USER_ID = "ADMIN_TEST";

	@InjectMocks
	private DocumentService documentService;

	@Mock
	private DocumentDao documentDao;

	@Mock
	private DraftDao draftDao;

	@Mock
	private DepotDao depotDao;

	@Mock
	private DocTypeDao docTypeDao;

	@Mock
	private ProfileDao profileDao;

	@Mock
	private UserDao userDao;

	@Mock
	private UserProfileRightService userProfileRightService;

	@Mock
	private AbstractStorageService storageService;

	@Mock
	private JournalArchiveService journalArchiveService;

	@Mock
	private JournalEventService journalEventService;

	@Mock
	private RemoteCall remoteCall;

	@Mock
	private EntiretyCheckResultLogger entiretyCheckResultLogger;

	private static final Map<String, Object> input = new HashMap<>();

	@BeforeClass
	public static void generalSetup() {
		// initialization of context Param in a thread local variable

		Param appParam = new Param();
		appParam.setUpdateged(true);
		appParam.setServletneoged("localhost");
		appParam.setPortneoged("8080");
		appParam.setNodeneoged("neoged");
		appParam.setBaseneoged("baseNeoged");
		appParam.setLogread(true);

		SwitchDataSourceService.CONTEXT_APP_PARAM.set(appParam);
	}

	@Test(expected = AvpExploitException.class)
	public void control() throws AvpExploitException {
		input.clear();
		input.put("docid", TestConstants.TEST_DOC_ID.toString());

		// All goes well
		when(storageService.check(TestConstants.TEST_DOC_ID, false)).thenReturn(true);
		when(documentDao.get(TestConstants.TEST_DOC_ID, false)).thenReturn(new Document());

		documentService.control(input);

		verify(journalArchiveService).log(anyMap());

		// If Check file by Storage goes wrong
		when(storageService.check(TestConstants.TEST_DOC_ID, false)).thenReturn(false);
		documentService.control(input);
	}

	@Test(expected = AvpExploitException.class)
	public void checkfiles() throws JsonProcessingException, AvpExploitException {
		input.clear();
		input.put("docids", new ObjectMapper().writeValueAsString(Arrays.asList(UUID.randomUUID(), UUID.randomUUID())));

		when(journalArchiveService.getSellementLogArchiveForDocs(anyList()))
				.thenReturn(Stream.of(new LogArchive(), new LogArchive()).collect(Collectors.toSet()));

		final Map<String, Object> resultat = new HashMap<>();
		documentService.checkfiles(input, resultat);

		input.clear();
		documentService.checkfiles(input, resultat);
	}

	@Test
	public void delay() throws AvpExploitException {
		ZonedDateTime nowFixed = ZonedDateTime.now();
		ZonedDateTime newEndDate = nowFixed.plus(1, ChronoUnit.MONTHS);

		input.clear();
		input.put("docid", TestConstants.TEST_DOC_ID.toString());
		input.put("user", USER_ID);
		input.put("archive_end", newEndDate.toString());

		// When achievement end date is smaller than minimum conserved date
		Document document = new Document();
		document.setDocId(TestConstants.TEST_DOC_ID);
		document.setArchiveDate(nowFixed);
		document.setArchiveEnd(nowFixed);
		Profile profile = new Profile();
		profile.setParId(1);
		profile.setParConversation(5); // minimum keep for 5 monthes
		document.setProfile(profile);

		when(documentDao.get(TestConstants.TEST_DOC_ID, false)).thenReturn(document);
		when(userProfileRightService.canDoThePredict(eq(1), eq(USER_ID), any())).thenReturn(true);

		final Map<String, Object> resultat = new HashMap<>();
		documentService.delay(input, resultat);

		assertFalse(resultat.isEmpty());
		assertEquals("13", resultat.get("codeRetour"));

		// otherwise try with 10 monthes
		newEndDate = nowFixed.plus(10, ChronoUnit.MONTHS);
		input.put("archive_end", newEndDate.toString());
		resultat.clear();
		documentService.delay(input, resultat);

		assertTrue(resultat.isEmpty());
		assertTrue(document.getArchiveEnd().isEqual(newEndDate));

		verify(documentDao).saveMetaDonneesDocument(document);
		verify(journalArchiveService).log(anyMap());
	}

	@Test
	public void delete() throws AvpExploitException, ClassNotFoundException, IOException {
		Document document = new Document();
		document.setDocId(TestConstants.TEST_DOC_ID);
		document.setTitle("TestTitle");
		document.setElasticid("Test elastic ID"); // This decides whether noGED or not
		Profile profile = new Profile();
		profile.setParId(1);
		document.setProfile(profile);

		input.clear();
		input.put("docid", UUID.randomUUID().toString());
		input.put("user", USER_ID);
		input.put("elasticid", "Test elastic ID");

		when(documentDao.get(any(UUID.class), eq(false))).thenReturn(document);
		when(userProfileRightService.canDoThePredict(eq(1), eq(USER_ID), any())).thenReturn(true);
		when(storageService.delete(document)).thenReturn(true);

		//
		// simulate the NeoGed return info
		//
		HashMap<String, Object> tmpMap = new HashMap<>();
		tmpMap.put("archived", "true");
		LinkedList<Object> tmp = new LinkedList<>();
		tmp.add(tmpMap);
		SqlInfo output = new SqlInfo();
		output.codeRetour = "0";
		output.data.add(tmp);

		when(remoteCall.callServlet(anyMap(), anyString(), anyString())).thenReturn(output);

		when(storageService.archive(anyString(), eq(document))).thenReturn(document);
		when(storageService.get(document.getDocId())).thenReturn(document);

		// one document to delete
		final Map<String, Object> resultat = new HashMap<>();
		documentService.delete(input, resultat, false);

		assertEquals(ReturnCode.OK.toString(), (String) resultat.get("codeRetour"));

		// a List of documents to delete
		input.put("idlist", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString());
		resultat.clear();
		documentService.delete(input, resultat, true);

		assertNull(resultat.get("codeRetour"));

		verify(storageService, times(3)).archive(anyString(), eq(document));
		verify(storageService, times(3)).get(document.getDocId());
		verify(journalArchiveService, times(3)).log(anyMap());
	}

	@Test
	public void get() throws AvpExploitException {
		Empreinte empreinte = new Empreinte();
		Document document = new Document();
		document.setDocId(TestConstants.TEST_DOC_ID);
		document.setTitle("TestTitle");
		document.setContent("Test content".getBytes());
		document.setContentLength(1);
		document.setEmpreinte(empreinte);

		Profile profile = new Profile();
		profile.setParId(1);
		document.setProfile(profile);

		input.clear();
		input.put("docid", TestConstants.TEST_DOC_ID.toString());
		input.put("user", USER_ID);
		input.put("base64", "true");

		when(documentDao.get(TestConstants.TEST_DOC_ID, false)).thenReturn(document);
		when(userProfileRightService.canDoThePredict(eq(1), eq(USER_ID), any())).thenReturn(true);
		when(storageService.get(TestConstants.TEST_DOC_ID)).thenReturn(document);

		final Map<String, Object> resultat = new HashMap<>();
		documentService.get(input, resultat);

		assertEquals(Base64.getEncoder().encodeToString("Test content".getBytes()), (String) resultat.get("content"));

		verify(storageService).get(TestConstants.TEST_DOC_ID);
		verify(journalArchiveService).log(anyMap());
	}

	@Test
	public void get_info() {
		Document document = new Document();
		document.setDocId(TestConstants.TEST_DOC_ID);

		when(documentDao.get(TestConstants.TEST_DOC_ID, false)).thenReturn(document);

		input.clear();
		input.put("docid", TestConstants.TEST_DOC_ID.toString());
		final Map<String, Object> resultat = new HashMap<>();
		documentService.getInfo(input, resultat);

		assertEquals(TestConstants.TEST_DOC_ID.toString(), (String) resultat.get("docid"));
	}

	@Test
	public void get_list() {

		input.clear();
		input.put("user", USER_ID);

		// Prepare User and Profile
		//

		// Profile 1
		Profile profile1 = new Profile();
		profile1.setParId(PROFILE_ID);
		profile1.setArProfile("DocumentAr");

		// Profile 2
		Profile profile2 = new Profile();
		profile2.setParId(PROFILE_ID - 1);
		profile2.setArProfile("DocumentAr2");

		// User
		User user = new User();
		user.setUserId(USER_ID);
		user.setNom("Telino");

		// ParRight 1 with canRead
		ParRight parRight1 = new ParRight();
		parRight1.setParCanRead(true);

		profile1.addParRight(parRight1);
		user.addParRight(parRight1);

		// ParRight 2 with canDeposit
		ParRight parRight2 = new ParRight();
		parRight2.setParCanDeposit(true);

		profile2.addParRight(parRight2);
		user.addParRight(parRight2);

		when(userDao.findByUserId(USER_ID)).thenReturn(user);

		// Prepare documents their Keywords
		//

		Document doc1 = new Document();
		doc1.setDocId(UUID.randomUUID());
		doc1.setKeywords(
				"<Type=Factures démat><Catégorie=Client SD><Référence de facture=8030002839><Numéro de document SAP=><Fournisseur=CLS REMY COINTREAU><Acheteur=CIRCUIT VISITE CLS MERPINS><Date de la facture=2018-06-02><Montant TTC=-0.54><Type=A><Code SAP du Client=10>");
		Document doc2 = new Document();
		doc2.setDocId(UUID.randomUUID());
		doc2.setKeywords("<Type=Factures démat>");
		when(documentDao.findTop2ByProfileInOrderByTimestampDesc(anyList())).thenReturn(Arrays.asList(doc1, doc2));

		// Test
		final Map<String, Object> resultat = new HashMap<>();
		documentService.getList(input, resultat);

		assertNotNull(resultat.get("list"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> reponse = (List<Map<String, Object>>) resultat.get("list");
		assertEquals(2, reponse.size());

		assertEquals("Fournisseur=CLS REMY COINTREAU", (String) reponse.get(0).get("keyword5"));
		assertTrue(((String) reponse.get(1).get("keyword2")).isEmpty());
	}

	@Test
	public void logical_delete() {

		Document doc = new Document();
		doc.setLogicaldelete(false);
		when(documentDao.findAllByDocIdIn(anyList())).thenReturn(Arrays.asList(doc));

		// Doc list
		input.put("idlist", UUID.randomUUID().toString() + " , " + UUID.randomUUID());
		documentService.logicalDelete(input);

		assertTrue(doc.getLogicaldelete());

		doc.setLogicaldelete(false);
		input.put("docid", UUID.randomUUID().toString());
		documentService.logicalDelete(input);

		assertTrue(doc.getLogicaldelete());

		verify(documentDao, times(2)).saveAll(anyList());
	}

	@Test
	public void store() throws AvpExploitException, ClassNotFoundException, IOException {
		input.clear();
		input.put("$FROMAVP", "true");
		// input.put("$NOGED", "true");
		String base64TestContent = Base64.getEncoder().encodeToString("Test content".getBytes());
		input.put("content", base64TestContent);
		input.put("keywords", "keywords");
		input.put("doctype", "DocumentDocType");
		input.put("categorie", "DocumentCategorie");
		input.put("user", USER_ID);
		input.put("docsdate", new Date());

		// Prepare User and Profile
		//

		// DocType
		DocType docType = new DocType();
		docType.setDocTypeId(-20);
		Type type = new Type();
		type.setDocTypeArchivage("DocumentDocType");
		docType.setDocTypeArchivage(type);
		docType.setCategorie("DocumentCategorie");
		MimeType mimeType = new MimeType();
		mimeType.setContentType("application/octet-stream");
		docType.addMimeType(mimeType);
		
		DocType docType2 = new DocType();
		docType2.setDocTypeArchivage(new Type());

		// Profile 1 with DocType
		Profile profile1 = new Profile();
		profile1.setParId(PROFILE_ID);
		profile1.setArProfile("DocumentAr");
		profile1.addDocType(docType);

		// Profile 2
		Profile profile2 = new Profile();
		profile2.setParId(PROFILE_ID - 1);
		profile2.setArProfile("DocumentAr2");
		profile2.addDocType(docType2);

		// User
		User user = new User();
		user.setUserId(USER_ID);
		user.setNom("Telino");

		// ParRight 1 with canDeposit
		ParRight parRight1 = new ParRight();
		parRight1.setParCanDeposit(true);

		profile1.addParRight(parRight1);
		user.addParRight(parRight1);

		// ParRight 2 with canCommunicate
		ParRight parRight2 = new ParRight();
		parRight2.setParCanCommunicate(true);

		profile2.addParRight(parRight2);
		user.addParRight(parRight2);

		when(userDao.findByUserId(USER_ID)).thenReturn(user);
		when(docTypeDao.findByDocTypeArchivageAndCategorie("DocumentDocType", "DocumentCategorie")).thenReturn(docType);

		//
		// simulate the NeoGed return info
		//
		HashMap<String, Object> tmpMap = new HashMap<>();
		tmpMap.put("content_type", "application/octet-stream");
		tmpMap.put("doctype", "DocumentDocType");
		tmpMap.put("categorie", "DocumentCategorie");
		tmpMap.put("content", base64TestContent);
		tmpMap.put("content_length", 10);
		tmpMap.put("docsdate", new Timestamp(new Date().getTime()));
		LinkedList<Object> tmp = new LinkedList<>();
		tmp.add(tmpMap);
		SqlInfo output = new SqlInfo();
		output.codeRetour = "0";
		output.data.add(tmp);
		output.docId = "NEOGED DOC ID";

		when(remoteCall.callServlet(anyMap(), anyString(), anyString())).thenReturn(output);
		doAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			((Document) args[0]).setDocId(TestConstants.TEST_DOC_ID);
			return true;
		}).when(storageService).archive(any(Document.class));

		// Test no docid
		final Map<String, Object> resultat = new HashMap<>();

		documentService.store(input, resultat);

		assertEquals(PROFILE_ID, (Integer) input.get("par_id"));
		assertEquals(ReturnCode.OK.toString(), resultat.get("codeRetour"));

		// Test with docid
		input.put("docid", TestConstants.TEST_DOC_ID.toString());
		Draft draft = new Draft();
		draft.setContentType("application/octet-stream");
		draft.setContent("Test content".getBytes());
		draft.setContentLength(10);
		
		when(draftDao.get(TestConstants.TEST_DOC_ID)).thenReturn(draft);

		resultat.clear();
		documentService.store(input, resultat);

		assertEquals(ReturnCode.OK.toString(), resultat.get("codeRetour"));
	}
}
