package com.telino.avp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.TestConstants;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.masterdao.MasterDocumentRepository;
import com.telino.avp.dao.mirrordao.MirrorDocumentRepository;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Empreinte;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.DbEntityProtocol.DocumentStatut;

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
public class TestDocumentRepository {

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private MasterDocumentRepository masterDocumentRepository;

	@Autowired
	private MirrorDocumentRepository mirrorDocumentRepository;

	private Document documentOne;
	private Document documentTwo;

	@Before
	public void buildEntity() {

		Empreinte empreinteOne = new Empreinte();
		empreinteOne.setEmpreinte(TestConstants.TEST_HASH);

		Empreinte empreinteTwo = new Empreinte();
		empreinteTwo.setEmpreinte(TestConstants.TEST_HASH);

		documentOne = new Document();
		documentOne.setStatut(DocumentStatut.REARDY_FOR_ARCHIVE.getStatutCode());

		documentTwo = new Document();
		documentTwo.setStatut(DocumentStatut.ARCHIVED.getStatutCode());
		documentTwo.setArchiveEnd(ZonedDateTime.now().minus(1, ChronoUnit.DAYS));
		documentTwo.setLogicaldelete(false);
		
		// Persister
		documentOne.setEmpreinte(empreinteOne);
		documentTwo.setEmpreinte(empreinteTwo);
		
					
		documentDao.saveMetaDonneesDocument(documentOne);
		documentDao.saveMetaDonneesDocument(documentTwo);
	}

	@Test
	public void dao_Should_Save_In_Both_Db() {
		assertNotNull(documentDao);

		// Verifier le save
		assertEquals(documentTwo.getDocId(), documentDao.get(documentTwo.getDocId(), false).getDocId());
		assertEquals(documentTwo.getDocId(), documentDao.get(documentTwo.getDocId(), true).getDocId());
	}

	@Test
	public void fill_seconde_doc_empreinte_unique() {

		documentDao.fillEmpreinteUnique(documentTwo);

		assertEquals(TestConstants.TEST_HASH + String.valueOf(2),
				documentTwo.getEmpreinte().getEmpreinteUnique());

	}

	@Test
	public void set_title_for_docid() {
		documentDao.setTitleForDocId(TestConstants.DOC_TITILE, documentOne.getDocId());

		assertEquals(TestConstants.DOC_TITILE, documentDao.get(documentOne.getDocId(), false).getTitle());
		assertEquals(TestConstants.DOC_TITILE, documentDao.get(documentOne.getDocId(), true).getTitle());
	}

	@Test
	public void get_total_archive_num_and_last_archived() {
		// page=0 & limit=1 pour recuperer le dernier doc
		List<Document> docPage = documentDao.getDocListToCheck(0, 1);

		assertTrue(docPage.size() > 0);

		// << Hibernate return a BigInetger object in the return list for Postgres>>
		assertEquals(documentTwo.getDocId(), docPage.get(0).getDocId());
	}

	@Test
	public void restore_the_metadata_from() throws AvpExploitException {
		// Restore the mirror from master
		documentTwo = documentDao.get(documentTwo.getDocId(), false);
		
		documentTwo.setTitle(TestConstants.DOC_TITILE); // prepare the master title, mirror title is NUll
		masterDocumentRepository.save(documentTwo);

		// true means restore the Mirror
		documentDao.restoreTheMetaDataFrom(documentTwo.getDocId(), true);
		assertEquals(TestConstants.DOC_TITILE, documentDao.get(documentTwo.getDocId(), true).getTitle());

		// Restore the master from mirror
		documentTwo = documentDao.get(documentTwo.getDocId(), false); // prepare the master title with NULL,
		documentTwo.setTitle(null);
		masterDocumentRepository.save(documentTwo);

		// false means restore the Master
		documentDao.restoreTheMetaDataFrom(documentTwo.getDocId(), false);
		assertEquals(TestConstants.DOC_TITILE, documentDao.get(documentTwo.getDocId(), false).getTitle());

	}

	@Test
	public void restore_the_hash() {
		// Prepare the Mirror hash with NULL
		documentTwo = documentDao.get(documentTwo.getDocId(), true);
		documentTwo.getEmpreinte().setEmpreinte(null);
		mirrorDocumentRepository.save(documentTwo);

		documentDao.restoreTheHash(documentTwo.getDocId(), TestConstants.TEST_HASH, true);
		assertEquals(TestConstants.TEST_HASH,
				documentDao.get(documentTwo.getDocId(), true).getEmpreinte().getEmpreinte());

		// Prepare the Master hash with NULL
		documentTwo = documentDao.get(documentTwo.getDocId(), false);
		documentTwo.getEmpreinte().setEmpreinte(null);
		masterDocumentRepository.save(documentTwo);

		documentDao.restoreTheHash(documentTwo.getDocId(), TestConstants.TEST_HASH, false);
		assertEquals(TestConstants.TEST_HASH,
				documentDao.get(documentTwo.getDocId(), false).getEmpreinte().getEmpreinte());

	}

	@Test
	public void get_all_docid_ready_for_archive() {
		List<Document> docs = documentDao.getAllDocIdReadyForArchive();

		assertTrue(docs.size() > 0);
		// the last one is documentOne
		assertEquals(documentOne.getDocId(), Collections.max(docs, Comparator.comparing(Document::getTimestamp)).getDocId());
	}

	@Test
	public void get_all_doc_to_delete() {
		List<Document> docs = documentDao.getAllDocToDelete();

		assertTrue(docs.size() > 0);

		// the last one is documentTwo
		assertEquals(documentTwo.getDocId(), Collections.max(docs, Comparator.comparing(Document::getTimestamp)).getDocId());
	}

}
