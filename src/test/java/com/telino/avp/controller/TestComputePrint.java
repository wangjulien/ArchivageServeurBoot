package com.telino.avp.controller;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.service.storage.FsStorageService;
import com.telino.avp.utils.AesCipherException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestComputePrint {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private FsStorageService fsStorageService;

	@Autowired
	private SwitchDataSourceService switchDataSourceService;

	@Autowired
	private DocumentDao documentDao;

	@Test
	public void computePrint() throws AvpExploitException, AesCipherException {

		switchDataSourceService.switchDataSourceFor("AVP_TEST");

		Document document = documentDao.get(UUID.fromString("633eb3f0-e67e-4476-b5d8-0d3a3306d33e"), false);
		document.setContent(documentService.decrypt(document.getContent(), document.getChiffrement().getEncryptionKey(), document.getCryptageIv()));

		assertEquals(document.getEmpreinte().getEmpreinteInterne(), documentService.computeTelinoPrint(document));

		Document storageDoc = fsStorageService.get(UUID.fromString("633eb3f0-e67e-4476-b5d8-0d3a3306d33e"));
		System.out.println(documentService.computePrint(storageDoc));
		
		assertEquals(document.getEmpreinte().getEmpreinteInterne(), documentService.computeTelinoPrint(storageDoc));
	}

}
