package com.telino.avp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.telino.avp.TestConstants;
import com.telino.avp.dao.ChiffrementDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.EncryptionKeyDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.entity.archive.Chiffrement;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Empreinte;
import com.telino.avp.entity.archive.EncryptionKey;
import com.telino.avp.entity.param.Param;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.service.storage.FSProc;
import com.telino.avp.service.storage.FsStorageService;
import com.telino.avp.utils.AesCipherException;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestFsStorageService {

	private static final UUID CRYPTAGE_ID = UUID.randomUUID();

	@InjectMocks
	private FsStorageService fsStorageService;

	@Mock
	private DocumentService documentService;

	@Mock(name = "fsprocMaster")
	private FSProc fsprocMaster;

	@Mock(name = "fsprocMirror")
	private FSProc fsprocMirror;

	@Mock
	private EncryptionKeyDao encryptionKeyDao;

	@Mock
	private ChiffrementDao chiffrementDao;

	@Mock
	private DocumentDao documentDao;

	@Mock
	private LogArchiveDao logArchiveDao;

	private Document document;

	@BeforeClass
	public static void generalSetup() {
		// initialization of context Param in a thread local variable

		Param appParam = new Param();
		appParam.setCryptage(true);
		appParam.setCryptageid(CRYPTAGE_ID);

		SwitchDataSourceService.CONTEXT_APP_PARAM.set(appParam);
	}

	@Before
	public void setup() {
		Empreinte print = new Empreinte();

		document = new Document();
		document.setDocId(TestConstants.TEST_DOC_ID);
		document.setTitle("TestTitle.pdf");
		document.setArchiveDate(ZonedDateTime.now());
		document.setContent("Test content".getBytes());
		document.setEmpreinte(print);
	}

	@Test
	public void archive() throws AesCipherException, AvpExploitException {

		EncryptionKey encrytionKey = new EncryptionKey();
		encrytionKey.setEncodedkey("secret key".getBytes());
		Chiffrement chiffrement = new Chiffrement();
		chiffrement.setAlgorythm("AES");
		chiffrement.setEncryptionKey(encrytionKey);

		Map<String, byte[]> resultCrypt = new HashMap<>();
		resultCrypt.put("crypted", "Test content crypted".getBytes());
		resultCrypt.put("iv", "Initial vector".getBytes());

		// test crypte
		when(chiffrementDao.findChiffrementByCrytId(CRYPTAGE_ID)).thenReturn(chiffrement);
		when(documentService.encrypt(document.getContent(), encrytionKey)).thenReturn(resultCrypt);
		when(documentService.computeTelinoPrint(document)).thenReturn("Internal Telino print");
		when(documentService.computePrint(document)).thenReturn("Simple print");

		doAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			((Document) args[0]).getEmpreinte().setEmpreinteUnique("Unique print");
			return null; // void method in a block-style lambda, so return null
		}).when(documentDao).fillEmpreinteUnique(document);

		// test all goes well
		fsStorageService.archive(document);

		assertEquals("Internal Telino print", document.getEmpreinte().getEmpreinteInterne());
		assertEquals("Simple print", document.getEmpreinte().getEmpreinte());
		assertEquals("Unique print", document.getEmpreinte().getEmpreinteUnique());
		assertTrue(Arrays.equals("Initial vector".getBytes(), document.getCryptageIv()));
		assertTrue(Arrays.equals("Test content crypted".getBytes(), document.getContent()));

		// When error
		document.setContent("Test content".getBytes());
		doThrow(new AvpExploitException(AvpExploitExceptionCode.STORAGE_WRITE_ERROR, null, null))
		.when(fsprocMaster).writeFile(anyString(), anyString());

		try {
			fsStorageService.archive(document);
		} catch (AvpExploitException e) {
			assertEquals(AvpExploitExceptionCode.STORAGE_WRITE_ERROR, e.getCodeErreur());
		}

		verify(chiffrementDao, times(2)).findChiffrementByCrytId(CRYPTAGE_ID);
		verify(fsprocMaster, times(2)).writeFile(anyString(), anyString());
		verify(fsprocMirror).deleteFile("Unique print");
		verify(documentDao).saveMetaDonneesDocument(document);
	}

	@Test
	public void delete() throws AvpExploitException {

		document.getEmpreinte().setEmpreinteUnique("Unique print");

		fsStorageService.delete(document);

		verify(fsprocMirror).deleteFile("Unique print");
		verify(fsprocMaster).deleteFile("Unique print");
	}

	@Test
	public void check() throws AvpExploitException, AesCipherException {

		document.getEmpreinte().setEmpreinteInterne("Internal Telino print");
		document.getEmpreinte().setEmpreinte("Simple print");
		document.getEmpreinte().setEmpreinteUnique("Unique print");
		document.setCryptage(true);

		// test decryptage
		EncryptionKey encrytionKey = new EncryptionKey();
		encrytionKey.setEncodedkey("secret key".getBytes());
		Chiffrement chiffrement = new Chiffrement();
		chiffrement.setAlgorythm("AES");
		chiffrement.setEncryptionKey(encrytionKey);
		document.setChiffrement(chiffrement);
		document.setCryptageIv("Initial vector".getBytes());

		when(documentService.decrypt(document.getContent(), encrytionKey, document.getCryptageIv()))
				.thenReturn("Test content decrypted".getBytes());

		// test all goes well
		when(documentService.computeTelinoPrint(document)).thenReturn("Internal Telino print");
		when(documentService.computePrint(document)).thenReturn("Simple print");
		when(documentDao.get(eq(TestConstants.TEST_DOC_ID), anyBoolean())).thenReturn(document);
		when(fsprocMirror.getFile("Unique print")).thenReturn(document.getContent());
		when(fsprocMaster.getFile("Unique print")).thenReturn(document.getContent());

		// To archive
		fsStorageService.check(TestConstants.TEST_DOC_ID, true);
		assertTrue(Arrays.equals("Test content decrypted".getBytes(), document.getContent()));
		verify(documentService, times(2)).computeTelinoPrint(document);

		// Already archived
		when(logArchiveDao.findHashForDocId(TestConstants.TEST_DOC_ID, false)).thenReturn("Simple print");
		fsStorageService.check(TestConstants.TEST_DOC_ID, false);
		verify(documentService, times(2)).computePrint(document);

		verify(fsprocMirror, times(2)).getFile("Unique print");
		verify(fsprocMaster, times(2)).getFile("Unique print");
		verify(documentDao, times(4)).get(eq(TestConstants.TEST_DOC_ID), anyBoolean());
		verify(documentService, times(4)).decrypt("Test content".getBytes(), encrytionKey, document.getCryptageIv());
	}

	@Test
	public void check_files() throws AvpExploitException {

		List<UUID> docIds = new ArrayList<>();
		docIds.add(TestConstants.TEST_DOC_ID);
		Map<UUID, FileReturnError> badDocsInUnit1 = new HashMap<>();
		Map<UUID, FileReturnError> badDocsInUnit2 = new HashMap<>();

		document.getEmpreinte().setEmpreinte("Test Hash");

		// When print check not passed
		when(documentDao.getDocumentToCreateDto(docIds, false)).thenReturn(Arrays.asList(document));
		when(documentDao.getDocumentToCreateDto(docIds, true)).thenReturn(Arrays.asList(document));

		assertFalse(fsStorageService.checkFiles(docIds, badDocsInUnit1, badDocsInUnit2));

		assertEquals(FileReturnError.HASH_NOT_MATCH_ERROR, badDocsInUnit1.get(TestConstants.TEST_DOC_ID));
		assertEquals(FileReturnError.HASH_NOT_MATCH_ERROR, badDocsInUnit2.get(TestConstants.TEST_DOC_ID));

		// Print Hash check passed
		badDocsInUnit1.clear();
		badDocsInUnit2.clear();
		when(logArchiveDao.findHashForDocId(eq(TestConstants.TEST_DOC_ID), anyBoolean())).thenReturn("Test Hash");
		when(fsprocMaster.checkFiles(anyList(), eq(badDocsInUnit1))).thenReturn(true);
		when(fsprocMirror.checkFiles(anyList(), eq(badDocsInUnit2))).thenReturn(true);

		// All goes well
		assertTrue(fsStorageService.checkFiles(docIds, badDocsInUnit1, badDocsInUnit2));
		assertTrue(badDocsInUnit1.isEmpty());
		assertTrue(badDocsInUnit2.isEmpty());

		verify(fsprocMaster, times(2)).checkFiles(anyList(), eq(badDocsInUnit1));
		verify(fsprocMirror, times(2)).checkFiles(anyList(), eq(badDocsInUnit2));
	}
}
