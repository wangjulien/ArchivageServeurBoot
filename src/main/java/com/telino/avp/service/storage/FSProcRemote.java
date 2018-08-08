package com.telino.avp.service.storage;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telino.avp.dto.DocumentDto;
import com.telino.avp.entity.param.StorageParam;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.tools.RemoteCall;

public class FSProcRemote implements FSProc {

	@Autowired
	private RemoteCall remoteCall;

	// Thread local variable for saving storage Param during all HttpRequest
	private ThreadLocal<StorageParam> storageParamLocal = new ThreadLocal<>();

	@Override
	public void init(final StorageParam storageParam) throws AvpExploitException {

		// check remote FS parameters are correct, Remote Hostname should not be null
		if (storageParam.getTypeStorage().equals("FileStorage") && storageParam.getRemoteOrLocal().equals("remote")
				&& Objects.nonNull(storageParam.getHostName())) {
			storageParamLocal.set(storageParam);
		} else {
			throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_UNIT_PARAM_ERROR, null,
					"Initialisation le module de stockage");
		}

		// If idStorage is not already assigned,
		// require remote Storage Module to create one
		if (Objects.isNull(storageParam.getIdStorage())) {

			JSONObject param = new JSONObject();
			param.put("command", Commande.CREATE_STORAGE_UNIT.toString());

			String result;
			try {
				result = (String) remoteCall.callServletWithJsonObject(param,
						getServerUrlFromStorageParam(storageParamLocal.get()));
			} catch (ClassNotFoundException | IOException e) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CALL_SERVLET_ERROR, e,
						"Création d'un module de stockage");
			}
			JSONObject json = new JSONObject(result);

			if (!ReturnCode.OK.toString().equals(json.get("codeRetour"))) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CREATE_ERROR,
						new Exception((String) json.get("message")), "Création d'un module de stockage");
			} else {
				storageParam.setIdStorage((String) json.get("message"));
			}
		}
	}

	@Override
	public void writeFile(final String sha1Unique, final String contentBase64) throws AvpExploitException {

		JSONObject param = new JSONObject();
		param.put("command", Commande.ARCHIVE.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());
		param.put("content", contentBase64);
		param.put("empreinte", sha1Unique);

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param,
					getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CALL_SERVLET_ERROR, e,
					"Archivage par le module de stockage");
		}
		JSONObject json = new JSONObject(result);
		if (!json.get("codeRetour").equals("OK")) {

			if (json.get("message") == null || ((String) json.get("message")).isEmpty()) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_OUTPUT_ERROR, null,
						"Extraire message retour - null");
			}
			String message = (String) json.get("message");

			if (((String) json.get("message")).contains("Empreinte unique servant au stockage non communiquée")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_PRINT, new Exception(message),
						"Archivage par le module de stockage");
			} else if (((String) json.get("message")).contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_ID, new Exception(message),
						"Archivage par le module de stockage");
			} else if (((String) json.get("message")).contains("Contenu du fichier à archiver vide")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_CONTENT,
						new Exception(message), "Archivage par le module de stockage");
			} else {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_WRITE_ERROR, new Exception(message),
						"Archivage d'un module de stockage");
			}
		}
	}

	@Override
	public void deleteFile(final String sha1Unique) throws AvpExploitException {

		JSONObject param = new JSONObject();
		param.put("command", Commande.DELETE.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());
		param.put("empreinte", sha1Unique);

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param,
					getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CALL_SERVLET_ERROR, e,
					"Suppression d'une archive par le module de stockage");
		}
		JSONObject json = new JSONObject(result);
		if (!json.get("codeRetour").equals("OK")) {

			if (json.get("message") == null || ((String) json.get("message")).isEmpty()) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_OUTPUT_ERROR, null,
						"Extraire message retour - null");
			}
			String message = (String) json.get("message");

			if (((String) json.get("message")).contains("Empreinte unique servant au stockage non communiquée")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_PRINT, new Exception(message),
						"Suppression d'une archive par le module de stockage");
			} else if (((String) json.get("message")).contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_ID, new Exception(message),
						"Suppression d'une archive par le module de stockage");
			} else {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_DELET_ERROR, new Exception(message),
						"Suppression d'une archive par le module de stockage");
			}
		}

	}

	@Override
	public void checkFile(final String sha1Unique) throws AvpExploitException {

		// TODO : implementation of checkfile by Storage Module
		throw new AvpExploitException(AvpExploitExceptionCode.SYSTEM_ERROR, null, "Check file");
	}

	@Override
	public boolean checkFiles(final List<DocumentDto> documents, final Map<UUID, FileReturnError> badDocs)
			throws AvpExploitException {
		//
		// Préparer la commande "checkfiles" à envoyer
		//

		JSONObject param = new JSONObject();
		param.put("command", Commande.CHECK_FILES.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());

		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			param.put("documents", jsonMapper.writeValueAsString(documents));
		} catch (JsonProcessingException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_INPUT_ERROR, e,
					"Parser documents a controler en JSON pour module de storage");
		}

		//
		// Envoyer la commande et récupérer la réponse
		//

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param,
					getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CALL_SERVLET_ERROR, e,
					"Check des archives par le module de stockage");
		}

		//
		// Exploiter le résultat dans la réponse
		//

		JSONObject json = new JSONObject(result);

		// S'il y des problèmes : CodeRetour != OK
		if (!ReturnCode.OK.toString().equals(json.get("codeRetour").toString())) {

			if (json.get("message") == null || ((String) json.get("message")).isEmpty()) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_OUTPUT_ERROR, null,
						"Extraire message retour - null");
			}
			String message = (String) json.get("message");

			// Si le CodeRetour est ERROR, alors,
			// c'est un problème de controle de l'intégralité de certains archives.
			if (ReturnCode.ERROR.toString().equals(json.get("codeRetour").toString())) {
				// Alors récupérer la liste de ID d'archives ayant problème
				try {
					badDocs.putAll(jsonMapper.readValue(message, new TypeReference<HashMap<UUID, FileReturnError>>() {
					}));
				} catch (IOException e) {
					throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_OUTPUT_ERROR, e,
							"Extraire les documents ne passent pas controle integralite");
				}
				return false;
			}

			if (message.contains("Liste Documents à controler non communiquée")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_PRINT, new Exception(message),
						"Check des archives par le module de stockage");
			} else if (message.contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_ID, new Exception(message),
						"Check des archives par le module de stockage");
			} else {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CHECK_ERROR, new Exception(message),
						"Récupération d'une archive par le module de stockage");
			}
		} else {
			return true;
		}
	}

	@Override
	public byte[] getFile(final String sha1Unique) throws AvpExploitException {

		JSONObject param = new JSONObject();
		param.put("command", Commande.GET_DOC.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());
		param.put("empreinte", sha1Unique);

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param,
					getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_CALL_SERVLET_ERROR, e,
					"Récupération d'une archive par le module de stockage");
		}
		JSONObject json = new JSONObject(result);
		if (!json.get("codeRetour").equals("OK")) {

			if (json.get("message") == null || ((String) json.get("message")).isEmpty()) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_OUTPUT_ERROR, null,
						"Extraire message retour - null");
			}

			String message = (String) json.get("message");

			if (message.contains("Empreinte unique servant au stockage non communiquée")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_PRINT, new Exception(message),
						"Récupération d'une archive par le module de stockage");
			} else if (message.contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INPUT_LACK_ID, new Exception(message),
						"Récupération d'une archive par le module de stockage");
			} else {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_READ_ERROR, new Exception(message),
						"Récupération d'une archive par le module de stockage");
			}
		} else {
			String contentBase64 = json.get("content").toString();
			return Base64.getDecoder().decode(contentBase64);
		}
	}

	private String getServerUrlFromStorageParam(final StorageParam storageParam) {
		return "http://" + storageParam.getHostName() + ":" + storageParam.getPort() + "/" + storageParam.getServlet();
	}
}
