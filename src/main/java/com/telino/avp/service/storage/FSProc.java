package com.telino.avp.service.storage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.telino.avp.dto.DocumentDto;
import com.telino.avp.entity.param.StorageParam;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;

public interface FSProc {

	public void deleteFile(final String sha1Unique) throws AvpExploitException;

	public void checkFile(final String sha1Unique) throws AvpExploitException;

	public boolean checkFiles(final List<DocumentDto> documents, final Map<UUID, FileReturnError> badDocs)
			throws AvpExploitException;

	byte[] getFile(final String sha1Unique) throws AvpExploitException;

	public void init(final StorageParam storageParam) throws AvpExploitException;

	public void writeFile(final String sha1Unique, final String contentBase64) throws AvpExploitException;
}