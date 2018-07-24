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
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.tools.RemoteCall;

public class FSProcRemote implements FSProc {

	@Autowired
	private RemoteCall remoteCall;

	private static ThreadLocal<StorageParam> storageParamLocal = new ThreadLocal<>();

	@Override
	public void init(final StorageParam storageParam) throws Exception {

		// check remote FS parameters are correct, Remote Hostname should not be null
		if (storageParam.getTypeStorage().equals("FileStorage") && storageParam.getRemoteOrLocal().equals("remote")
				&& Objects.nonNull(storageParam.getHostName())) {
			storageParamLocal.set(storageParam);
		} else {
			throw new Exception(
					"Impossible d'initialiser le module de stockage local car les paramètres de stockages ne correspondent pas");
		}

		// If idStorage is not already assigned, 
		// require remote Storage Module to create one
		if (Objects.isNull(storageParam.getIdStorage())) {

			JSONObject param = new JSONObject();
			param.put("command", Commande.CREATE_STORAGE_UNIT.toString());

			String result;
			try {
				result = (String) remoteCall.callServletWithJsonObject(param, getServerUrlFromStorageParam(storageParamLocal.get()));
			} catch (ClassNotFoundException | IOException e) {
				throw new AvpExploitException("514", e, "Création d'un module de stockage", null, null, null);
			}
			JSONObject json = new JSONObject(result);

			if (!ReturnCode.OK.toString().equals(json.get("codeRetour"))) {
				throw new AvpExploitException("517", (Throwable) json.get("message"),
						"Création d'un module de stockage", null, null, null);
			} else {
				storageParam.setIdStorage((String) json.get("message"));
			}
		}
	}

	@Override
	public boolean writeFile(String sha1Unique, String contentBase64) throws AvpExploitException {
		
		JSONObject param = new JSONObject();
		param.put("command", Commande.ARCHIVE.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());
		param.put("content", contentBase64);
		param.put("empreinte", sha1Unique);

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param, getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException("514", e, "Archivage par le module de stockage", null, null, null);
		}
		JSONObject json = new JSONObject(result);
		if (!json.get("codeRetour").equals("OK")) {
			if (((String) json.get("message")).contains("Empreinte unique servant au stockage non communiquée")) {
				throw new AvpExploitException("511", null, "Archivage par le module de stockage", null, null, null);
			} else if (((String) json.get("message")).contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException("512", null, "Archivage par le module de stockage", null, null, null);
			} else if (((String) json.get("message")).contains("Contenu du fichier à archiver vide")) {
				throw new AvpExploitException("516", null, "Archivage par le module de stockage", null, null, null);
			} else {
				throw new AvpExploitException("515", (Throwable) json.get("message"),
						"Création d'un module de stockage", null, null, null);
			}
		}

		// TODO : toujour return TRUE, inutile de utiliser return pour process control
		return true;
	}

	@Override
	public boolean deleteFile(String sha1Unique) throws AvpExploitException {
		
		JSONObject param = new JSONObject();
		param.put("command", Commande.DELETE.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());
		param.put("empreinte", sha1Unique);

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param, getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException("514", e, "Suppression d'une archive par le module de stockage", null, null,
					null);
		}
		JSONObject json = new JSONObject(result);
		if (!json.get("codeRetour").equals("OK")) {
			if (((String) json.get("message")).contains("Empreinte unique servant au stockage non communiquée")) {
				throw new AvpExploitException("511", null, "Suppression d'une archive par le module de stockage", null,
						null, null);
			} else if (((String) json.get("message")).contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException("512", null, "Suppression d'une archive par le module de stockage", null,
						null, null);
			} else {
				throw new AvpExploitException("515", (Throwable) json.get("message"),
						"Suppression d'une archive par le module de stockage", null, null, null);
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean checkFile(String sha1Unique) {
		return false;
	}

	@Override
	public boolean checkFiles(List<DocumentDto> documents, Map<UUID, FileReturnError> badDocs)
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
			throw new AvpExploitException("510", e, "Récupération d'une archive par le module de stockage", null, null,
					null);
		}

		//
		// Envoyer la commande et récupérer la réponse
		//

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param, getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException("514", e, "Check des archives par le module de stockage", null, null, null);
		}

		//
		// Exploiter le résultat dans la réponse
		//

		JSONObject json = new JSONObject(result);

		// S'il y des problèmes : CodeRetour != OK
		if (!ReturnCode.OK.toString().equals(json.get("codeRetour").toString())) {

			if (json.get("message") == null || ((String) json.get("message")).isEmpty()) {
				throw new AvpExploitException("510", null, "Message retour est vide", null, null, null);
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
					throw new AvpExploitException("510", e, "Récupération d'une archive par le module de stockage",
							null, null, null);
				}
				return false;
			}

			if (message.contains("Liste Documents à controler non communiquée")) {
				throw new AvpExploitException("511", null, "Check des archives par le module de stockage", null, null,
						null);
			} else if (message.contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException("512", null, "Check des archives par le module de stockage", null, null,
						null);
			} else {

				throw new AvpExploitException("510", new Exception((String) json.get("message")),
						"Récupération d'une archive par le module de stockage", null, null, null);
			}
		} else {
			return true;
		}
	}

	@Override
	public byte[] getFile(String sha1Unique) throws AvpExploitException {
	
		JSONObject param = new JSONObject();
		param.put("command", Commande.GET_DOC.toString());
		param.put("idstorage", storageParamLocal.get().getIdStorage());
		param.put("empreinte", sha1Unique);

		String result;
		try {
			result = (String) remoteCall.callServletWithJsonObject(param, getServerUrlFromStorageParam(storageParamLocal.get()));
		} catch (ClassNotFoundException | IOException e) {
			throw new AvpExploitException("514", e, "Récupération d'une archive par le module de stockage", null, null,
					null);
		}
		JSONObject json = new JSONObject(result);
		if (!json.get("codeRetour").equals("OK")) {
			if (((String) json.get("message")).contains("Empreinte unique servant au stockage non communiquée")) {
				throw new AvpExploitException("511", null, "Récupération d'une archive par le module de stockage", null,
						null, null);
			} else if (((String) json.get("message")).contains("id du module de stockage non communiqué")) {
				throw new AvpExploitException("512", null, "Récupération d'une archive par le module de stockage", null,
						null, null);
			} else {
				throw new AvpExploitException("510", (Throwable) json.get("message"),
						"Récupération d'une archive par le module de stockage", null, null, null);
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
