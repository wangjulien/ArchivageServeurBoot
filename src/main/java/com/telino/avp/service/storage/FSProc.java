package com.telino.avp.service.storage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.telino.avp.dto.DocumentDto;
import com.telino.avp.entity.param.StorageParam;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;

public interface FSProc {

	boolean deleteFile(String sha1Unique) throws AvpExploitException;

	boolean checkFile(String sha1Unique);
	
	boolean checkFiles(List<DocumentDto> documents, Map<UUID, FileReturnError> badDocs) throws AvpExploitException;

	byte[] getFile(String sha1Unique) throws AvpExploitException;

	void init(final StorageParam storageParam) throws Exception;

	boolean writeFile(String sha1Unique, String contentBase64) throws AvpExploitException;
}