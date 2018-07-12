package com.telino.avp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telino.avp.TestConstants;
import com.telino.avp.entity.param.StorageParam;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.service.storage.FSProcRemote;
import com.telino.avp.tools.RemoteCall;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestFsProcRemote {

	private static final String ID_STORAGE = "TestIdStorage";

	@InjectMocks
	private FSProcRemote fsProcRemote;

	@Mock
	private RemoteCall remoteCall;

	@Before
	public void setup() throws Exception {

		StorageParam storageParam = new StorageParam();
		storageParam.setParamId(1);
		storageParam.setTypeStorage("FileStorage");
		storageParam.setRemoteOrLocal("remote");
		storageParam.setHostName("localhost");
		storageParam.setPort("8087");

		JSONObject result = new JSONObject();
		result.put("codeRetour", ReturnCode.OK.toString());
		result.put("message", ID_STORAGE);
		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		fsProcRemote.init(storageParam);
	}

	@Test
	public void write_file() throws ClassNotFoundException, IOException, AvpExploitException {
		JSONObject result = new JSONObject();
		result.put("codeRetour", ReturnCode.OK.toString());

		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());
		assertTrue(fsProcRemote.writeFile("sha1Unique", "contentBase64"));

		result.put("codeRetour", ReturnCode.KO.toString());
		result.put("message", "Empreinte unique servant au stockage non communiquée");
		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		try {
			fsProcRemote.writeFile("sha1Unique", "contentBase64");
		} catch (AvpExploitException e) {
			assertEquals("511", e.getMessage());
		}

		verify(remoteCall, times(3)).callServletWithJsonObject(any(JSONObject.class), anyString());
	}

	@Test
	public void delete_file() throws ClassNotFoundException, IOException, AvpExploitException {
		JSONObject result = new JSONObject();
		result.put("codeRetour", ReturnCode.OK.toString());

		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		assertTrue(fsProcRemote.deleteFile("sha1Unique"));

		result.put("codeRetour", ReturnCode.KO.toString());
		result.put("message", "Empreinte unique servant au stockage non communiquée");
		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		try {
			fsProcRemote.deleteFile("sha1Unique");
		} catch (AvpExploitException e) {
			assertEquals("511", e.getMessage());
		}

		verify(remoteCall, times(3)).callServletWithJsonObject(any(JSONObject.class), anyString());
	}

	@Test
	public void check_files() throws ClassNotFoundException, IOException, AvpExploitException {

		// Test all goes well
		JSONObject result = new JSONObject();
		result.put("codeRetour", ReturnCode.OK.toString());

		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		Map<UUID, FileReturnError> badDocs = new HashMap<>();
		assertTrue(fsProcRemote.checkFiles(Collections.emptyList(), badDocs));

		// Test the case where not all the entirety check are passed for the documents
		Map<UUID, FileReturnError> badDocsResult = new HashMap<>();
		badDocsResult.put(TestConstants.TEST_DOC_ID, FileReturnError.DECRYPT_ERROR);
		ObjectMapper jsonMapper = new ObjectMapper();
		result.put("codeRetour", ReturnCode.ERROR.toString());
		result.put("message", jsonMapper.writeValueAsString(badDocsResult));

		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		assertFalse(fsProcRemote.checkFiles(Collections.emptyList(), badDocs));
		assertEquals(FileReturnError.DECRYPT_ERROR, badDocs.get(TestConstants.TEST_DOC_ID));

		verify(remoteCall, times(3)).callServletWithJsonObject(any(JSONObject.class), anyString());
	}

	@Test
	public void get_file() throws ClassNotFoundException, IOException, AvpExploitException {
		// Test all goes well
		JSONObject result = new JSONObject();
		result.put("codeRetour", ReturnCode.OK.toString());
		result.put("content", Base64.getEncoder().encodeToString("This is a test".getBytes()));

		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		assertTrue(Arrays.equals("This is a test".getBytes(), fsProcRemote.getFile("sha1Unique")));
		
		// Test error
		result.put("codeRetour", ReturnCode.KO.toString());
		result.put("message", "Empreinte unique servant au stockage non communiquée");
		when(remoteCall.callServletWithJsonObject(any(JSONObject.class), anyString())).thenReturn(result.toString());

		try {
			fsProcRemote.getFile("sha1Unique");
		} catch (AvpExploitException e) {
			assertEquals("511", e.getMessage());
		}

		verify(remoteCall, times(3)).callServletWithJsonObject(any(JSONObject.class), anyString());
	}
}
