package com.telino.avp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.telino.avp.dao.DraftDao;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.service.archivage.DraftService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestDraftService {

	@InjectMocks
	private DraftService draftService;

	@Mock
	private DraftDao draftDao;

	private static final Map<String, Object> input = new HashMap<>();

	@Test
	public void delete_draft() throws AvpExploitException {
		input.clear();

		// Input empty
		try {
			draftService.deleteDraft(input);
		} catch (AvpExploitException e) {
			assertEquals(AvpExploitExceptionCode.DRAFT_DELETE_ERROR, e.getCodeErreur());
		}

		// Doc list
		input.put("idlist", UUID.randomUUID().toString() + " , " + UUID.randomUUID());
		draftService.deleteDraft(input);

		input.put("docid", UUID.randomUUID().toString());
		draftService.deleteDraft(input);

		verify(draftDao, times(2)).deleteAllByDocId(anyList());
	}

	@Test
	public void refus_draft() throws AvpExploitException {
		// input lacks document info
		input.clear();
		try {
			draftService.refusDraft(input);
		} catch (AvpExploitException e) {
			assertEquals(AvpExploitExceptionCode.DRAFT_REFUSE_ERROR, e.getCodeErreur());
		}
		// Doc list
		input.put("idlist", UUID.randomUUID().toString() + " , " + UUID.randomUUID());
		draftService.refusDraft(input);

		input.put("docid", UUID.randomUUID().toString());
		draftService.refusDraft(input);

		verify(draftDao, times(2)).findAllByDocId(anyList());
		verify(draftDao, times(2)).saveAll(anyList());
	}

	@Test
	public void update_draft() throws AvpExploitException {
		input.clear();

		input.put("docid", UUID.randomUUID().toString());
		draftService.updateDraft(input);

		// Doc list
		input.put("idlist", UUID.randomUUID().toString() + " , " + UUID.randomUUID());
		draftService.updateDraft(input);

		verify(draftDao, times(2)).saveAll(anyList());

	}

	@Test
	public void valide_draft() throws AvpExploitException {
		input.clear();
		try {
			draftService.valideDraft(input);
		} catch (AvpExploitException e) {
			assertEquals(AvpExploitExceptionCode.DRAFT_SAVE_ERROR, e.getCodeErreur());
		}
		
		// Doc list
		input.put("idlist", UUID.randomUUID().toString() + " , " + UUID.randomUUID());
		draftService.valideDraft(input);

		input.put("docid", UUID.randomUUID().toString());
		draftService.valideDraft(input);

		verify(draftDao, times(2)).findAllByDocId(anyList());
		verify(draftDao, times(2)).saveAll(anyList());
	}

	@Test
	public void get_draft_info() throws AvpExploitException {
		Draft draft = new Draft();
		draft.setDocId(UUID.randomUUID());

		when(draftDao.get(draft.getDocId())).thenReturn(draft);
		draftService.getDraftInfo(draft.getDocId(), new HashMap<>());

		verify(draftDao).get(draft.getDocId());
	}

	@Test
	public void read_draft() throws AvpExploitException {
		Draft draft = new Draft();
		draft.setDocId(UUID.randomUUID());
		draft.setTitle("TestTitle");
		draft.setContentType("PDF");

		input.clear();
		input.put("docid", draft.getDocId().toString());
		input.put("getAsPdf", "true");

		when(draftDao.get(draft.getDocId())).thenReturn(draft);
		final Map<String, Object> resultat = new HashMap<>();
		draftService.readDraft(input, resultat);

		assertNull(resultat.get("content_type"));
		assertEquals(draft.getTitle(), (String) resultat.get("title"));
		assertEquals(0, (int) resultat.get("content_length"));

		verify(draftDao).get(draft.getDocId());
	}

	@Test
	public void draft_store() throws AvpExploitException {
		// input.clear();
		// input.put("title", "TestTile.txt");
		// input.put("content", "Test content".getBytes());
		//
		// final Map<String, Object> resultat = new HashMap<>();
		// draftService.draftStore(input, resultat);
		//
		// assertTrue(resultat.isEmpty());
		//
		// verify(draftDao).saveDraft(any(Draft.class));
	}

}
