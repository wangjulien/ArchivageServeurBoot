package com.telino.avp.service.archivage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telino.avp.dao.DepotDao;
import com.telino.avp.dao.DocTypeDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.DraftDao;
import com.telino.avp.dao.ProfileDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.entity.archive.EncryptionKey;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.entity.context.DocType;
import com.telino.avp.entity.context.MimeType;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.entity.context.User;
import com.telino.avp.entity.param.Param;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.protocol.DbEntityProtocol.DocumentStatut;
import com.telino.avp.protocol.DbEntityProtocol.DraftStatut;
import com.telino.avp.protocol.DbEntityProtocol.LogArchiveType;
import com.telino.avp.protocol.DbEntityProtocol.LogEventType;
import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.service.journal.EntiretyCheckResultLogger;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.journal.JournalEventService;
import com.telino.avp.service.journal.TamponHorodatageService;
import com.telino.avp.service.storage.AbstractStorageService;
import com.telino.avp.tools.RemoteCall;
import com.telino.avp.tools.ServerProc;
import com.telino.avp.utils.AesCipher;
import com.telino.avp.utils.AesCipherException;
import com.telino.avp.utils.Sha;
import com.telino.avp.utils.TlnMd5;

import CdmsApi.client.SqlInfo;
import tools.ApercuManager;

/**
 * Calcul empreinte pour document et chiffrement/dechifrement
 * 
 * @author jwang
 *
 */
@Service
public class DocumentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

	private static final String ALGO_AES = AesCipher.ALGO_NAME;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private DraftDao draftDao;

	@Autowired
	private DepotDao depotDao;

	@Autowired
	private DocTypeDao docTypeDao;

	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserProfileRightService userProfileRightService;

	@Autowired
	private AbstractStorageService storageService;

	@Autowired
	private JournalArchiveService journalArchiveService;

	@Autowired
	private JournalEventService journalEventService;

	@Autowired
	private RemoteCall remoteCall;

	@Autowired
	private EntiretyCheckResultLogger entiretyCheckResultLogger;

	public static String getCrypteAlgo() {
		return ALGO_AES;
	}

	/**
	 * controle de l'integralite de document
	 * 
	 * @param Map
	 */
	public void control(final Map<String, Object> input) throws AvpExploitException {

		UUID docId = UUID.fromString((String) input.get("docid"));

		// Controle de l'integralite par module de storage
		boolean checked = storageService.check(docId, false);

		if (!checked) {
			throw new AvpExploitException("503", null, "Contrôle d'intégrité d'une archive", null,
					input.get("docid").toString(), null);
		}

		try {
			// Get the sealing log for the document
			LogArchive logArchive = journalArchiveService.findLogArchiveForDocId(docId, false);
			// check the entirety of the sealing log
			journalArchiveService.verifyJournal(logArchive, false);

			// TODO : improve the exception management
		} catch (PersistenceException e) {
			throw new AvpExploitException("506", e, "Contrôle d'intégrité d'une archive", null, docId.toString(), null);
		} catch (Exception e) {
			if (e.getMessage().contains("horodatage")) {
				throw new AvpExploitException("504", null, "Vérification de l'intégrité d'une archive", null,
						(String) input.get("docid"), null);
			} else {
				throw new AvpExploitException("505", null, "Vérification de l'intégrité d'une archive", null,
						(String) input.get("docid"), null);
			}
		}

		// Log the entirety controle in LOG_ARCHIVE
		try {
			Document document = documentDao.get(docId, false);

			Map<String, Object> inputToLog = new HashMap<String, Object>();
			inputToLog.put("operation", "Contrôle d'intégrité de l'archive" + input.get("docid").toString());
			inputToLog.put("docid", input.get("docid"));
			inputToLog.put("userid", input.get("user"));
			inputToLog.put("mailid", input.get("mailid"));
			inputToLog.put("docsname", input.get("docsname"));
			inputToLog.put("hash",
					Objects.isNull(document.getEmpreinte()) ? "" : document.getEmpreinte().getEmpreinte());
			inputToLog.put("logtype", LogArchiveType.C.toString());

			journalArchiveService.log(inputToLog);
		} catch (AvpExploitException e) {
			LOGGER.error("Impossible de logué le controle d'intégrité dans le cycle de vie des archives");
			throw e;
		}
	}

	/**
	 * Controle de l'integralte d'une liste de documents archives
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void checkfiles(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {
		// Test
		try {
			Objects.requireNonNull(input.get("docids"), "Input 'docids' is null!");

			ObjectMapper jsonMapper = new ObjectMapper();
			List<UUID> docIds = new ArrayList<>(
					Arrays.asList(jsonMapper.readValue((String) input.get("docids"), UUID[].class)));

			Map<UUID, FileReturnError> badDocsInUnit1 = new HashMap<>();
			Map<UUID, FileReturnError> badDocsInUnit2 = new HashMap<>();
			if (!storageService.checkFiles(docIds, badDocsInUnit1, badDocsInUnit2)) {

				entiretyCheckResultLogger.logErrorResult(input, badDocsInUnit1, badDocsInUnit2);
				resultat.put("codeRetour", ReturnCode.KO.toString());
				resultat.put("message", "Controle de l'integralite de ces documents n'est pas passe : \n"
						+ badDocsInUnit1 + "\n" + badDocsInUnit2);
			}

			// Controle le sellement de journaux des archives
			// Verification de sellement de log_archivage
			docIds.removeAll(badDocsInUnit1.keySet());
			docIds.removeAll(badDocsInUnit2.keySet());

			if (!checkScellementLogArchive(docIds, input)) {
				resultat.put("codeRetour", ReturnCode.KO.toString());
				String message = (String) resultat.get("message");
				resultat.put("message",
						message + "\nControle de scellement des journaux echoues pour ces documents : \n" + docIds);
			}

		} catch (NullPointerException | IOException e) {
			throw new AvpExploitException("507", e, "Entrée 'input' ne peut pas être parsé en JSON", null, null, null);
		}
	}

	private boolean checkScellementLogArchive(final List<UUID> docIds, final Map<String, Object> input)
			throws AvpExploitException {
		boolean allGoesWell = true;

		// Verification de sellement de log_archivage
		// Recupere logid de log_archive

		Set<LogArchive> logArchives = journalArchiveService.getSellementLogArchiveForDocs(docIds);

		for (LogArchive log : logArchives) {
			try {
				// Verify the log in DB master (isMirror = false)
				journalArchiveService.verifyJournal(log, false);

			} catch (AvpExploitException e) {
				allGoesWell = false;

				LogEvent logEvent = new LogEvent();
				logEvent.setOrigin((String) input.get("origin"));
				logEvent.setProcessus((String) input.get("processus"));
				logEvent.setOperateur((String) input.get("operateur"));
				logEvent.setVersionProcessus((String) input.get("version"));
				logEvent.setAction(e.getAction());
				logEvent.setLogType(LogEventType.C.toString());
				logEvent.setLogArchive(log);
				logEvent.setMethode(e.getMethodName());
				logEvent.setDetail(AvpExploitException.getTableLibelleErreur().get(e.getMessage())[0]);
				logEvent.setTrace(e.getMessage() + "." + Arrays.toString(e.getStackTrace()));

				try {
					// Log the Exception in LOG_EVENT
					journalEventService.setHorodatageAndSave(logEvent);
				} catch (AvpExploitException e1) {
					LOGGER.error("problème dans logevent");
				}
			} catch (Exception e) {
				allGoesWell = false;

				LogEvent logEvent = new LogEvent();
				logEvent.setOrigin((String) input.get("origin"));
				logEvent.setProcessus((String) input.get("processus"));
				logEvent.setOperateur((String) input.get("operateur"));
				logEvent.setVersionProcessus((String) input.get("version"));
				logEvent.setLogType(LogEventType.C.toString());
				logEvent.setDetail(e.getMessage());
				logEvent.setLogArchive(log);
				logEvent.setTrace(e.getMessage() + "." + Arrays.toString(e.getStackTrace()));

				try {
					journalEventService.setHorodatageAndSave(logEvent);
				} catch (AvpExploitException e1) {
					LOGGER.error("problème dans logevent");
				}
			}
		}

		return allGoesWell;
	}

	/**
	 * Prolonger un fin de conservation d'archive
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void delay(final Map<String, Object> input, final Map<String, Object> resultat) throws AvpExploitException {

		// If the user can DELAY a doc
		final UUID docId = UUID.fromString((String) input.get("docid"));
		final String userId = (String) input.get("user");

		final Document document = documentDao.get(docId, false);
		Objects.requireNonNull(document.getProfile(), "A document should have a profile!");

		if (!userProfileRightService.canDoThePredict(document.getProfile().getParId(), userId,
				ParRight::isParCanDelay)) {
			resultat.put("codeRetour", "1");
			resultat.put("message", "Opération non autorisée");
			return;
		}

		// ResultSet rs = st.executeQuery(
		// "select mindestructiondelay, a.archive_end, a.archive_date,
		// b.par_conservation "
		// + "from document a "
		// + "join profils b on a.par_id = b.par_id "
		// + "left join destructioncriterias c on c.destructioncriteriaid =
		// b.destructioncriteriaid "
		// + " where docid = " + docid);

		int minDelay = 0;
		// If a destruction criteria is associated with the profile
		if (Objects.nonNull(document.getProfile().getDestructionCriteria())) {
			minDelay = document.getProfile().getDestructionCriteria().getMinDestructionDelay();
		} else {
			// Otherwise use the profile's conversation
			minDelay = document.getProfile().getParConversation();
		}

		ZonedDateTime minDate = document.getArchiveDate().plus(minDelay, ChronoUnit.MONTHS);
		ZonedDateTime sDate = ZonedDateTime.parse((String) input.get("archive_end"));

		if (sDate.isBefore(minDate)) {
			resultat.put("codeRetour", "13");
			resultat.put("message", "La nouvelle date de destruction doit-être supérieure à la date d'archivage + "
					+ minDate.toString());
			return;
		}

		// Update DateArchiveEnd
		document.setArchiveEnd(sDate);
		documentDao.saveMetaDonneesDocument(document);

		// Create a LogArchive for the extension of archive end
		Map<String, Object> inputToLog = new HashMap<>();
		inputToLog.put("operation", "modification délai d'archivage au " + sDate.toString());
		inputToLog.put("docid", docId.toString());
		inputToLog.put("userid", userId);
		inputToLog.put("mailid", (String) input.get("mailid"));
		inputToLog.put("docsname", (String) input.get("docsname"));
		inputToLog.put("hash", Objects.isNull(document.getEmpreinte()) ? "" : document.getEmpreinte().getEmpreinte());
		inputToLog.put("logtype", LogArchiveType.A.toString());

		journalArchiveService.log(inputToLog);
	}

	/**
	 * Suppression d'une liste ou un document
	 * 
	 * @param input
	 * @param resultat
	 * @throws Exception
	 */
	public void delete(final Map<String, Object> input, final Map<String, Object> resultat, final boolean isBgTask)
			throws AvpExploitException {
		Boolean deleteAll = true;
		String message = "";
		if (Objects.nonNull(input.get("idlist")) && input.get("idlist").toString().length() > 0) {
			String[] arrayIdDocument = input.get("idlist").toString().split(",");

			for (String docId : arrayIdDocument) {
				Document documentUnitaire = documentDao.get(UUID.fromString(docId), false);

				Map<String, String> resultDelete = deleteDocumentUnitaire(input, documentUnitaire, isBgTask);

				if (!ReturnCode.OK.toString().equals(resultDelete.get("codeRetour"))) {
					deleteAll = false;
					message += "\n Archive " + documentUnitaire + " : " + resultDelete.get("message");
				}
			}
			if (!deleteAll) {
				resultat.put("codeRetour", ReturnCode.KO.toString());
				resultat.put("message", message);
			}
		} else {
			Document document = documentDao.get(UUID.fromString(input.get("docid").toString()), false);
			Map<String, String> resultDelete = deleteDocumentUnitaire(input, document, isBgTask);

			resultat.put("codeRetour", resultDelete.get("codeRetour"));
			resultat.put("message", resultDelete.get("message"));
		}
	}

	/**
	 * Permet la suppression d'une archive
	 * 
	 * @param input
	 * @param document
	 *            le document que l'on souhait supprimer
	 * @return l'objet resultat contenant un code retour et un message
	 * @throws Exception
	 */
	private Map<String, String> deleteDocumentUnitaire(final Map<String, Object> input, final Document document,
			final boolean isBgTask) throws AvpExploitException {

		// TODO : !!! valorization noGED flag for delete function
		final boolean noGED = Objects.isNull(document.getElasticid());

		Objects.requireNonNull(document.getProfile(), "A document should have a profile!");

		Map<String, String> resultDelete = new HashMap<>();

		String userId = input.get("user").toString();
		if (!isBgTask && !userProfileRightService.canDoThePredict(document.getProfile().getParId(), userId,
				ParRight::isParCanDestroy)) {
			resultDelete.put("codeRetour", "1");
			resultDelete.put("message", "Opération non autorisée");
			return resultDelete;
		}

		boolean shouldUpdateGed = false;
		Map<String, Object> resultGED = null;

		if (!noGED && SwitchDataSourceService.CONTEXT_APP_PARAM.get().isUpdateged()) {
			try {
				resultGED = getGEDContent(input);
			} catch (PersistenceException e) {
				throw new AvpExploitException("611", e, "Récupérer les informations de l'archive présentes dans la GED",
						null, document.getDocId().toString(), null);
			}

			if (resultGED.get("archived") == null || !resultGED.get("archived").toString().equals("true")) {
				// TODO : here the getGEDContent error information is not raised.

				shouldUpdateGed = false;
			} else {
				shouldUpdateGed = true;
			}
		}

		// Launch delete action by Storage Service
		if (storageService.delete(document)) {
			if (shouldUpdateGed) {
				if (!noGED) {
					HashMap<String, Object> GEDInfo = new HashMap<String, Object>();
					GEDInfo.put("user", userId);
					GEDInfo.put("mailowner", resultGED.get("mailowner"));
					GEDInfo.put("domainowner", resultGED.get("domainowner"));
					GEDInfo.put("externallink", " ");
					GEDInfo.put("elasticid", input.get("elasticid"));
					if (input.get("elasticid") != null)
						try {
							// TODO : updateGED return information is not used
							updateGED(GEDInfo, Commande.DELETE);
						} catch (Exception e) {
							throw new AvpExploitException("611", e,
									"Mettre à jour la GED pour la suppression de l'archive", null,
									document.getDocId().toString(), null);
						}
				}
			}

			String operation = "Suppression de l'archive";
			String titre = "Attestation de destruction de l'archive";
			if (input.get("restitutionid") != null) {
				operation = "Validation de la restitution de l'archive";
				titre = "Attestation de restitution";
			}

			// Creation of a attestation
			Document attestation = storageService.archive(titre, document);
			attestation = storageService.get(attestation.getDocId());

			// Log in LogArchive
			Map<String, Object> inputToLog = new HashMap<>();
			inputToLog.put("operation", operation);
			inputToLog.put("docid", document.getDocId().toString());
			inputToLog.put("userid", userId);
			inputToLog.put("mailid", (String) input.get("mailid"));
			inputToLog.put("docsname", document.getTitle());
			inputToLog.put("attestationid", attestation.getDocId().toString());
			inputToLog.put("hash",
					Objects.isNull(document.getEmpreinte()) ? "" : document.getEmpreinte().getEmpreinte());
			inputToLog.put("logtype", LogArchiveType.A.toString());
			journalArchiveService.log(inputToLog);

			resultDelete.put("codeRetour", ReturnCode.OK.toString());
			resultDelete.put("message", "");
		} else {
			resultDelete.put("codeRetour", ReturnCode.KO.toString());
			resultDelete.put("message", "Impossible de détruire cette archive. Veuillez reessayer ultérieurement.");
		}

		return resultDelete;
	}

	/**
	 * Recupere contenue depuis GED
	 * 
	 * @param input
	 * @return
	 */
	private Map<String, Object> getGEDContent(final Map<String, Object> input) {

		// Find a user context parameter
		Param appParam = SwitchDataSourceService.CONTEXT_APP_PARAM.get();

		String servlet = appParam.getServletneoged();
		String port = appParam.getPortneoged();
		String node = appParam.getNodeneoged();
		String base = appParam.getBaseneoged();

		Map<String, Object> reponse = new HashMap<>();
		Map<String, Object> elasticRequest = new HashMap<>();

		// if (sqlRecord.debug) elasticRequest.put("debug", true);
		SqlInfo output = new SqlInfo();
		try {

			elasticRequest.put("elasticCommand", "getDocAndKeyWords(_id=" + input.get("elasticid") + ")");
			elasticRequest.put("elasticType", "documents");
			elasticRequest.put("elasticFields", "content,keywords,content_size,content_type,domainowner,mailowner");
			elasticRequest.put("user", input.get("user"));
			elasticRequest.put("nomBase", base);
			elasticRequest.put("elasticFields",
					"docsname,docsfile,keywords,docsfile.content,content_length,docsfile.content_type,"
							+ "docsdate,docstype,mailowner,domainowner,categorie,archived,docsfile.author,description");

			String url = ServerProc.GetUrl(port, node);

			output = (SqlInfo) remoteCall.callServlet(elasticRequest, url + servlet + "/startElastic", base);

			if (output.codeRetour.length() > 0 && !output.codeRetour.equals("0") || output.data.size() == 0) {
				LOGGER.error("Erreur, code retour: " + output.codeRetour + " - nfz42013");

				reponse.put("codeRetour", output.codeRetour);
				reponse.put("message", output.message);
				return reponse;
			}

			reponse.put("codeRetour", "OK");
			reponse.put("message", output.message);

			@SuppressWarnings("unchecked")
			Map<String, Object> contents = (HashMap<String, Object>) output.data.get(0).get(0);

			reponse.put("docsname", contents.get("docsname"));
			// reponse.put("content", output.data.get(0).get(0));
			reponse.put("content", contents.get("content"));
			reponse.put("keywords", contents.get("keywords"));
			reponse.put("index", contents.get("index"));
			reponse.put("content_length", contents.get("content_length"));
			reponse.put("content_type", contents.get("content_type"));
			reponse.put("docsdate", contents.get("docsdate"));
			reponse.put("doctype", contents.get("docstype"));
			reponse.put("mailowner", contents.get("mailowner"));
			reponse.put("domainowner", contents.get("domainowner"));
			reponse.put("categorie", contents.get("categorie"));
			reponse.put("archived", contents.get("archived"));
			reponse.put("author", contents.get("author"));
			reponse.put("description", contents.get("description"));
			return reponse;

		} catch (Exception e) {
			LOGGER.error("Exception is : ", e);
			reponse.put("codeRetour", "99");
			reponse.put("message", e.getMessage());
			return reponse;
		}

	}

	private Map<String, Object> updateGED(Map<String, Object> input, Commande command) {

		Param appParam = SwitchDataSourceService.CONTEXT_APP_PARAM.get();

		String servlet = appParam.getServletneoged();
		String port = appParam.getPortneoged();
		String node = appParam.getNodeneoged();
		String base = appParam.getBaseneoged();

		Map<String, Object> reponse = new HashMap<>();
		Map<String, Object> elasticRequest = new HashMap<>();

		// if (sqlRecord.debug) elasticRequest.put("debug", true);
		SqlInfo output = new SqlInfo();
		try {
			LOGGER.info("updating " + input.get("elasticid") + " " + command.toString() + " - nfz42013");

			elasticRequest.put("elasticCommand", "update()");
			elasticRequest.put("elasticType", "documents");
			// elasticRequest.put("elasticFields","content,keywords,content_size,content_type");
			elasticRequest.put("secuUsers", input.get("user"));

			if (command == Commande.STORE) {
				elasticRequest.put("elasticDocType", input.get("doctype"));
				elasticRequest.put("elasticDocComment", input.get("keywords"));
				elasticRequest.put("archived", true);
				elasticRequest.put("archive_date", input.get("archive_date"));
				elasticRequest.put("archivage_profile_id", input.get("archivage_profile_id"));
				elasticRequest.put("archiver_id", input.get("user"));
				elasticRequest.put("archive_status", input.get("archive_status"));
				elasticRequest.put("externallink", "<sourcetype=AVP><docid=" + input.get("docid") + ">");
			} else if (command == Commande.DELETE) {
				elasticRequest.put("archived", false);
				elasticRequest.put("externallink", " ");
			}
			elasticRequest.put("elasticId", input.get("elasticid"));
			elasticRequest.put("mailOwner", input.get("mailowner"));
			elasticRequest.put("domaineOwner", input.get("domainowner"));
			elasticRequest.put("nomBase", base);

			String url = ServerProc.GetUrl(port, node);

			output = (SqlInfo) remoteCall.callServlet(elasticRequest, url + servlet + "/startElastic", base);

			// output = RC.Call(request, http + session.host+ ":"+session.port+
			// "/"+ session.servlet +"/startCdmsSql2", nomBase);
			if (output.codeRetour.length() > 0 && !output.codeRetour.equals("0")) {
				LOGGER.info("Erreur, code retour: " + output.codeRetour + " - nfz42013");

				reponse.put("codeRetour", output.codeRetour);
				reponse.put("message", output.message);
				return reponse;
			}
			// LOGGER.info(output.data.get(0).get(0));
			reponse.put("codeRetour", "OK");
			reponse.put("message", output.message);

			return reponse;

		} catch (Exception e) {
			e.printStackTrace();
			reponse.put("codeRetour", "99");
			reponse.put("message", e.getMessage());
			return reponse;
		}
	}

	/**
	 * Recupere un docment avec contenu
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 * @throws Exception
	 */
	public void get(final Map<String, Object> input, final Map<String, Object> resultat) throws AvpExploitException {

		// If the user can READ a doc
		Document document = documentDao.get(UUID.fromString(input.get("docid").toString()), false);
		Objects.requireNonNull(document.getProfile(), "A document should have a profile!");

		final String userId = input.get("user").toString();

		if (!userProfileRightService.canDoThePredict(document.getProfile().getParId(), userId,
				ParRight::isParCanRead)) {
			resultat.put("codeRetour", "1");
			resultat.put("message", "Opération non autorisée");
			return;
		}

		document = storageService.get(document.getDocId());
		LOGGER.debug("recupération du content : " + document.getEmpreinte().getEmpreinteUnique());

		byte[] content = document.getContent();
		if (Objects.isNull(input.get("base64"))) {

			if (Objects.nonNull(input.get("getAsPdf")) && input.get("getAsPdf").equals("true")) {

				if (document.getTitle().endsWith(".eml")) {

				} else {
					ApercuManager AM = new ApercuManager();
					// content = AM.convert(content, "Visualisation impossible",
					// document.getContent_type(),
					// document.getTitle(), openofficepath,
					// Math.toIntExact(document.getContent_length()),
					// maxconvertsize);
					document.setContentType(AM.type);
				}
			}

			resultat.put("content", content);
		} else {
			resultat.put("content", Base64.getEncoder().encodeToString(content));
		}

		resultat.put("content_length",
				Objects.isNull(document.getContentLength()) ? 0 : document.getContentLength().intValue());
		resultat.put("content_type", document.getContentType());
		resultat.put("title", document.getTitle());

		LOGGER.debug("resultat : " + resultat.toString());

		if (SwitchDataSourceService.CONTEXT_APP_PARAM.get().isLogread()) {
			LOGGER.info("logging... - nfz42013");

			Map<String, Object> inputToLog = new HashMap<>();
			inputToLog.put("operation", "Ouverture de l'archive " + input.get("docid").toString());
			inputToLog.put("docid", (String) input.get("docid"));
			inputToLog.put("userid", userId);
			inputToLog.put("mailid", (String) input.get("mailid"));
			inputToLog.put("docsname", document.getTitle());
			inputToLog.put("hash",
					Objects.isNull(document.getEmpreinte()) ? "" : document.getEmpreinte().getEmpreinte());
			inputToLog.put("logtype", LogArchiveType.L.toString());
			journalArchiveService.log(inputToLog);
		}
	}

	/**
	 * Recupere l'info d'une archive(document), sans contenu
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void getInfo(final Map<String, Object> input, final Map<String, Object> resultat) {
		// isMirror = false, get info from master DB
		Document doc = documentDao.get(UUID.fromString(input.get("docid").toString()), false);

		resultat.put("docid", doc.getDocId().toString());
		resultat.put("doctype", doc.getDoctype());
		resultat.put("domnnom", doc.getDomnNom());
		resultat.put("lot", doc.getLot());
		resultat.put("iddepot", Objects.isNull(doc.getDepot()) ? "" : doc.getDepot().getIdDepot().toString());
		resultat.put("conteneur", doc.getConteneur());
		resultat.put("categorie", doc.getCategorie());
		resultat.put("title", doc.getTitle());
		resultat.put("description", doc.getDescription());
		resultat.put("date", Date.from(doc.getDate().toInstant())); // Convert to Date for the sack of GWT Front
		resultat.put("archiver_id", doc.getArchiverId());
		resultat.put("content_type", doc.getContentType());
		resultat.put("content_length", Objects.isNull(doc.getContentLength()) ? "" : doc.getContentLength().intValue());
		resultat.put("archive_date", Date.from(doc.getArchiveDate().toInstant())); // Convert to Date for the sack of
																					// GWT Front
		resultat.put("archive_end", Date.from(doc.getArchiveEnd().toInstant())); // Convert to Date for the sack of GWT
																					// Front
		resultat.put("application", doc.getApplication());
		resultat.put("keywords",
				Objects.isNull(doc.getKeywords()) ? "" : doc.getKeywords().replaceAll("<", "").replaceAll(">", ""));
		resultat.put("author", doc.getAuthor());
		resultat.put("archiver_mail", doc.getArchiverMail());
		resultat.put("mailowner", doc.getMailowner());
		resultat.put("domaineowner", doc.getDomaineowner());
		resultat.put("par_id", Objects.isNull(doc.getProfile()) ? 0 : doc.getProfile().getParId());
		resultat.put("ar_profile", Objects.isNull(doc.getProfile()) ? "" : doc.getProfile().getArProfile());
		resultat.put("elasticid", doc.getElasticid());
		resultat.put("serviceverseur", doc.getServiceverseur());
		resultat.put("organisationversante", doc.getOrganisationversante());
		resultat.put("organisationverseuse", doc.getOrganisationverseuse());

		LOGGER.info("logging...  - nfz42013");
		// TODO : } else {
		// resultat.put("codeRetour", "10");
		// resultat.put("message", "Document inexistant");
		// }
	}

	/**
	 * recupere certain keyword par utilisateur?
	 * 
	 * @param input
	 * @param resultat
	 */
	public void getList(final Map<String, Object> input, final Map<String, Object> resultat) {

		User user = userDao.findByUserId((String) input.get("user"));

		// Find the profile for a given user where can Read or can Deposit
		List<Profile> profiles = user.getParRights().stream().filter(pr -> pr.isParCanRead() || pr.isParCanDeposit())
				.map(ParRight::getProfile).collect(Collectors.toList());

		// Find 2 document with all the profiles, cf: getkeywords view in DB
		List<Document> keyWordsView = documentDao.findTop2ByProfileInOrderByTimestampDesc(profiles);

		List<Map<String, Object>> reponse = new LinkedList<>();
		for (Document kw : keyWordsView) {
			if (Objects.isNull(kw.getKeywords()))
				continue;

			Map<String, Object> ligne = new HashMap<>();

			// Extract the first 5 key words
			List<String> keywords = Arrays.asList(kw.getKeywords().split(">")).stream().map(e -> e.replace("<", ""))
					.collect(Collectors.toList());
			// Completed if these is not 5
			for (int i = 0, s = keywords.size(); i < (5 - s); i++) {
				keywords.add("");
			}

			ligne.put("docid", kw.getDocId().toString());
			ligne.put("domnnom", kw.getDomnNom());
			ligne.put("doctype", kw.getDoctype());
			ligne.put("keyword1", keywords.get(0));
			ligne.put("keyword2", keywords.get(1));
			ligne.put("keyword3", keywords.get(2));
			ligne.put("keyword4", keywords.get(3));
			ligne.put("keyword5", keywords.get(4));
			ligne.put("keywords", kw.getKeywords());
			ligne.put("categorie", kw.getCategorie());
			ligne.put("title", kw.getTitle());
			ligne.put("date", Date.from(kw.getDate().toInstant())); // Convert to Date for the sack of GWT Front
			ligne.put("archiver_id", kw.getArchiverId());
			ligne.put("content_type", kw.getContentType());
			ligne.put("content_length", Objects.isNull(kw.getContentLength()) ? 0 : kw.getContentLength().intValue());
			ligne.put("archive_date", Date.from(kw.getArchiveDate().toInstant())); // Convert to Date for the sack of
																					// GWT Front
			ligne.put("archive_end", Date.from(kw.getArchiveEnd().toInstant())); // Convert to Date for the sack of GWT
																					// Front
			ligne.put("application", kw.getApplication());
			ligne.put("archiver_mail", kw.getArchiverMail());
			ligne.put("par_id", Objects.isNull(kw.getProfile()) ? 0 : kw.getProfile().getParId());
			ligne.put("ar_profile", Objects.isNull(kw.getProfile()) ? "" : kw.getProfile().getArProfile());
			ligne.put("elasticid", kw.getElasticid());

			reponse.add(ligne);
		}

		resultat.put("list", reponse);

		// TODO : ??? why result is limited by 2.
		// String request = "select a.docid, a.domnnom, doctype_archivage,
		// keyword1,keyword2,keyword3,keyword4,keyword5, keywords, categorie,title,
		// date, archiver_id, content_type, "
		// + "content_length, archive_date , archive_end , application, archiver_mail,
		// a.par_id, b.ar_profile, a.elasticid "
		// + "from getkeywords a " + "join profils b on a .par_id = b.par_id "
		// + "where a.par_id in (select x.par_id from profils x join par_rights y on
		// x.par_id = y.par_id and y.userid = '$utilisateur' "
		// + "and (y.par_canread is true or y.par_candeposit is true)) limit 2";
	}

	/**
	 * Suppression logical d'un/des documents : maj 'logicaldelete' a true
	 * 
	 * @param input
	 */
	public void logicalDelete(final Map<String, Object> input) {
		List<UUID> docIds = null;
		// one document or a list of document
		if (Objects.nonNull(input.get("docid")))
			docIds = Arrays.asList(UUID.fromString(input.get("docid").toString()));
		else if (Objects.nonNull(input.get("idlist")) && !input.get("idlist").toString().isEmpty()) {
			// a list of drafts separated by ','
			docIds = Arrays.asList((input.get("idlist").toString().replaceAll("\\s", "").split(","))).stream()
					.map(UUID::fromString).collect(Collectors.toList());
		}

		if (Objects.nonNull(docIds)) {
			// Mettre a jour logicaldete
			List<Document> documents = documentDao.findAllByDocIdIn(docIds);
			documents.forEach(d -> {
				d.setLogicaldelete(true);
				d.setLogicaldeletedate(ZonedDateTime.now());
			});

			documentDao.saveAll(documents);
		}
	}

	/**
	 * Enregistre d'un draft
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void store(final Map<String, Object> input, final Map<String, Object> resultat) throws AvpExploitException {
		Object content = input.get("content");

		// Flag
		boolean storeFromAVP = false;
		boolean noGED = false;

		if (Objects.nonNull(input.get("$FROMAVP")) || Objects.nonNull(content)) {
			if (Objects.nonNull(input.get("$FROMAVP")) && Objects.nonNull(input.get("docid"))) {

				storeFromAVP = true;

				Draft draft = new Draft();
				try {
					draft = draftDao.get(UUID.fromString(input.get("docid").toString()));
					LOGGER.debug(draft.toString());
				} catch (Exception e) {
					resultat.put("codeRetour", "10");
					resultat.put("message", "Document inexistant");
				}

				input.put("content", draft.getContent());
				if (Objects.isNull(input.get("doctype")) || input.get("doctype").toString().length() == 0)
					input.put("doctype", draft.getDoctype());
				if (Objects.isNull(input.get("categorie")) || input.get("categorie").toString().length() == 0)
					input.put("categorie", draft.getCategorie());
				if (Objects.isNull(input.get("keywords")) || input.get("keywords").toString().length() == 0)
					input.put("keywords", draft.getKeywords());
				input.put("domaineowner", draft.getDomaineowner());
				input.put("organisationversante", draft.getOrganisationversante());
				input.put("description", draft.getDescription());
				input.put("docsdate", draft.getDocsdate());
				input.put("domnnom", draft.getDomnNom());
				input.put("mailowner", draft.getMailowner());
				input.put("content_length",
						Objects.isNull(draft.getContentLength()) ? 0 : draft.getContentLength().intValue());
				input.put("content_type", draft.getContentType());
				input.put("docsname", draft.getTitle());
				input.put("title", draft.getTitle());
				input.put("pronom_id", draft.getPronomId());
				input.put("pronom_type", draft.getPronomType());
			} else {
				try (ByteArrayInputStream BA = new ByteArrayInputStream(
						Base64.getDecoder().decode((String) input.get("content")))) {

					if (Objects.nonNull(input.get("content_type"))) {
						input.put("content_type", input.get("content_type").toString());
					} else {
						String content_type = URLConnection.guessContentTypeFromStream(BA);
						// on force le content_type
						if (content_type != null)
							input.put("content_type", content_type);
						else if (content_type == null)
							input.put("content_type", "application/octet-stream");
					}
				} catch (IOException e) {
					resultat.put("codeRetour", "99");
					resultat.put("message", e.getMessage());
				}
			}

			if (Objects.isNull(input.get("keywords"))) {
				resultat.put("codeRetour", "4");
				resultat.put("message", "Les mots-clés ne sont pas renseignés");
				return;

			}

			if (Objects.isNull(input.get("doctype"))) {
				resultat.put("codeRetour", "8");
				resultat.put("message", "Le type de document n'est pas renseigné");
				return;

			}

			// check mime_type and assign a default profile if it is not supplied
			String user = (String) input.get("user");
			if (!checkDocumentTypeAndAssignDefaultProfile(user, input, resultat)) {
				resultat.put("codeRetour", "5");
				resultat.put("message", "Ce format de document ne peut pas être archivé");
				return;
			}

			if (Objects.nonNull(input.get("$NOGED"))
					|| !SwitchDataSourceService.CONTEXT_APP_PARAM.get().isUpdateged()) {
				noGED = true;
			} else {
				String result = null;
				try {
					result = storeInGed(input, resultat, storeFromAVP);
				} catch (AvpExploitException e) {
					resultat.put("codeRetour", "7");
					resultat.put("message", e.getMessage());
				}
				if (result == null) {
					resultat.put("codeRetour", "7");
					resultat.put("message", "Stockage dans la GED impossible");

					return;
				} else {
					LOGGER.info("Docid is " + result + " - nfz42013");
					input.put("elasticid", result);
				}

			}
		}
		storeFromGED(input, resultat, storeFromAVP, noGED);

	}

	private boolean checkDocumentTypeAndAssignDefaultProfile(final String userId, final Map<String, Object> input,
			final Map<String, Object> resultat) {
		try {

			User user = userDao.findByUserId(userId);

			String doctype = (String) input.get("doctype");
			String categorie = (String) input.get("categorie");

			//
			// one combine of "doctype" and "categorie" is associated with one and only one
			// Profile
			//
			Optional<ParRight> docTypeDefaultProfile = user.getParRights().stream()
					.filter(pr -> !pr.getProfile().getDocTypes().stream()
							.filter(dt -> Objects.isNull(doctype)
									? Objects.isNull(dt.getDocTypeArchivage().getDocTypeArchivage())
									: doctype.equals(dt.getDocTypeArchivage().getDocTypeArchivage()))
							.filter(dt -> Objects.isNull(categorie) ? Objects.isNull(dt.getCategorie())
									: categorie.equals(dt.getCategorie()))
							.collect(Collectors.toSet()).isEmpty()) // les Profils dont DocType and Categorie ne
																	// matchent pas, sont
					.findFirst();

			// After filter all DocType for the profiles, if no DocType left, then doctype
			// is not supported
			if (!docTypeDefaultProfile.isPresent()) {
				String message = "Actions non autorisées pour ce type de document";
				resultat.put("message", message);
				resultat.put("codeRetour", "1");
				LOGGER.info(message + " - nfz42013");
				return false;
			}

			ParRight defaultParRight = docTypeDefaultProfile.get();

			// For the DocType (doctype and/or categorie), can be Deposited
			if (!defaultParRight.isParCanDeposit()) {
				String message = "Archivage non autorisé pour ce type de document";
				resultat.put("message", message);
				resultat.put("codeRetour", "2");
				LOGGER.info(message + " - nfz42013");
				return false;
			}

			// check mime format
			Set<String> allMimeType = docTypeDao.findByDocTypeArchivageAndCategorie(doctype, categorie).getMimeTypes()
					.stream().map(MimeType::getContentType).collect(Collectors.toSet());

			String inputMimeType = (String) input.get("content_type");

			LOGGER.info("content_type : " + allMimeType);
			LOGGER.info("input content_type : " + inputMimeType);

			// Check if the input mime format is in the DB result,
			// otherwise, if input "pronom_type" is "PDFA" and "pdfa" is a permitted doc
			// type
			if ((Objects.nonNull(inputMimeType) && !allMimeType.isEmpty() && allMimeType.contains(inputMimeType))
					|| (allMimeType.contains("pdfa") && Objects.nonNull(input.get("pronom_type"))
							&& input.get("pronom_type").toString().contains("PDFA"))) {

				// assign default Profile id
				if (Objects.isNull(input.get("par_id"))) {
					input.put("par_id", defaultParRight.getProfile().getParId());
				}

				return true;
			} else {
				String message = "Ce document doit-être converti pour pouvoir être archivé";
				resultat.put("message", message);
				resultat.put("codeRetour", "3");
				LOGGER.info(message + " - nfz42013");
				return false;
			}

		} catch (Exception e) {
			resultat.put("codeRetour", "99");
			resultat.put("message", e.getMessage());
			LOGGER.error("Exception is : ", e);
			return false;
		}
	}

	private String storeInGed(final Map<String, Object> input, final Map<String, Object> resultat,
			final boolean storeFromAVP) throws AvpExploitException {

		try {
			// Find a user context parameter
			Param appParam = SwitchDataSourceService.CONTEXT_APP_PARAM.get();

			String servlet = appParam.getServletneoged();
			String port = appParam.getPortneoged();
			String node = appParam.getNodeneoged();
			String base = appParam.getBaseneoged();

			// HashMap<String,Object> reponse = new HashMap<String,Object>();
			Map<String, Object> elasticRequest = new HashMap<>();

			// if (sqlRecord.debug) elasticRequest.put("debug", true);
			SqlInfo output = new SqlInfo();
			// get file mime_type

			elasticRequest.put("elasticCommand", "put()");
			elasticRequest.put("elasticType", "documents");
			elasticRequest.put("elasticDocType", input.get("doctype"));

			// fileBase64
			String content_type = null;
			if (storeFromAVP) {
				try (ByteArrayInputStream BA = new ByteArrayInputStream((byte[]) input.get("content"))) {
					content_type = URLConnection.guessContentTypeFromStream(BA);
					elasticRequest.put("elasticTaille", input.get("content_length"));
					elasticRequest.put("fileContent", input.get("content"));
				}
			} else {
				try (ByteArrayInputStream BA = new ByteArrayInputStream(
						Base64.getDecoder().decode(input.get("content").toString()))) {
					content_type = URLConnection.guessContentTypeFromStream(BA);
					elasticRequest.put("elasticTaille", input.get("content_size"));
					elasticRequest.put("fileBase64", input.get("content"));
				}
			}
			if (Objects.isNull(content_type))
				content_type = "application/octet-stream";

			LOGGER.info("content_type is " + content_type + " - nfz42013");
			elasticRequest.put("elasticContentType", content_type);
			elasticRequest.put("elasticDocName", input.get("docsname"));
			elasticRequest.put("secuUsers", input.get("user"));
			elasticRequest.put("mailOwner", input.get("mailowner"));
			elasticRequest.put("domaineOwner", input.get("domainowner"));
			elasticRequest.put("docsdate", input.get("docsdate"));
			elasticRequest.put("comment", input.get("keywords"));

			elasticRequest.put("nomBase", base);

			String url = ServerProc.GetUrl(port, node);

			output = (SqlInfo) remoteCall.callServlet(elasticRequest, url + servlet + "/startElastic", base);

			// output = RC.Call(request, http + session.¬1512host+
			// ":"+session.port+ "/"+ session.servlet +"/startCdmsSql2",
			// nomBase);
			if (Objects.isNull(output) || Objects.isNull(output.docId) || output.docId.length() == 0) {
				LOGGER.error("Erreur, code retour: " + output.codeRetour + " - nfz42013");

				resultat.put("codeRetour", output.codeRetour);
				resultat.put("message", output.message);
				return null;
			}
			// // LOGGER.info(output.data.get(0).get(0));

			return output.docId;

		} catch (Exception e) {
			throw new AvpExploitException("303", e, "Mettre à jour la GED avec l'archive", null, null, null);
		}
	}

	private void storeFromGED(final Map<String, Object> input, final Map<String, Object> resultat,
			final boolean storeFromAVP, final boolean noGED) throws AvpExploitException {
		String user = (String) input.get("user");
		LOGGER.info("Storing document - nfz42013");

		UUID draftid = null;

		Map<String, Object> result = new HashMap<>();
		if (storeFromAVP) {
			Iterator<String> itor = input.keySet().iterator();
			while (itor.hasNext()) {
				String key = itor.next();
				result.put(key, input.get(key));

			}
			draftid = UUID.fromString(input.get("docid").toString());
			result.put("draftid", draftid);
			result.put("codeRetour", "OK");
		} else if (input.get("$FROMAVP").equals("NO")) {
			result.put("content_type", input.get("content_type"));
			result.put("content_length", input.get("content_length"));
			result.put("title", input.get("title"));
			result.put("docsname", input.get("docsname"));
			result.put("codeRetour", "OK");
		} else {
			try {
				result = getGEDContent(input);
			} catch (Exception e1) {
				throw new AvpExploitException("301", e1, "Récupérer de la GED les informations du document à archiver",
						null, null, null);
			}
		}

		if (ReturnCode.OK.toString().equals(result.get("codeRetour"))) {
			Document document = new Document();

			LOGGER.info("Storing document .... suite - nfz42013");

			document.setArchiveDate(ZonedDateTime.now());
			document.setTitle((String) result.get("docsname"));
			if (input.get("doctype") != null)
				result.put("doctype", input.get("doctype"));
			if (input.get("categorie") != null)
				result.put("categorie", input.get("categorie"));
			if (input.get("domnnom") != null)
				result.put("domainowner", input.get("domnnom"));
			if (input.get("mailowner") != null)
				result.put("mailowner", input.get("mailowner"));
			if (input.get("archiver_id") != null)
				user = input.get("archiver_id").toString();

			document.setDoctype((String) result.get("doctype"));
			document.setCategorie((String) result.get("categorie"));
			document.setApplication((String) input.get("application"));
			if (!checkDocumentTypeAndAssignDefaultProfile(user, result, resultat)) {
				LOGGER.error("document non conforme - nfz42013");
				return;
			}

			ZonedDateTime docsdate = null;
			byte[] content = new byte[0];
			if (storeFromAVP) {
				content = (byte[]) result.get("content");
				// From AVP : from the docsdate of Draft. Already in ZonedDateTime type
				docsdate = (ZonedDateTime) result.get("docsdate");
			} else if (input.get("$FROMAVP").equals("NO")) {
				content = Base64.getDecoder().decode((String) input.get("content"));
				docsdate = TamponHorodatageService.convertToSystemZonedDateTime((Date) input.get("docsdate"));
			} else {
				content = Base64.getDecoder().decode((String) result.get("content"));
				docsdate = TamponHorodatageService.convertToSystemZonedDateTime((Timestamp) result.get("docsdate"));
			}

			String md5 = this.getMd5(content);

			Param appParam = SwitchDataSourceService.CONTEXT_APP_PARAM.get();
			// doublon gets his value from "archivage_doublon"
			if (appParam.isArchivage_doublon()) {
				if (documentDao.findByMd5AndDomnNom(md5, (String) input.get("domnnom")).isPresent()) {
					resultat.put("codeRetour", "99");
					resultat.put("message", "Ce document a déjà été archivé");
					return;
				}
			}

			// Keywords
			String keywords = (String) input.get("keywords");
			if (Objects.isNull(keywords)) {
				keywords = (String) result.get("keywords");
			}

			Integer content_length = (Integer) result.get("content_length");

			document.setCryptage(appParam.isCryptage());
			document.setDate(docsdate);
			document.setContent(content);
			document.setContentLength(content_length);
			document.setMd5(md5);
			document.setKeywords(keywords);
			document.setContentType((String) result.get("content_type"));
			try {
				document.setArchiveEnd(getDestructionDate(user, document.getDoctype(), document.getCategorie(),
						document.getArchiveDate()));
			} catch (PersistenceException e1) {
				throw new AvpExploitException("610", e1, "Calcule de la date d''expiration du document", null, null,
						null);
			}
			document.setAuthor((String) result.get("author"));
			document.setMailowner((String) result.get("mailowner"));
			document.setDomaineowner((String) result.get("domainowner"));
			document.setDescription((String) result.get("description"));
			document.setArchiverMail((String) input.get("mailid"));
			document.setProfile(profileDao.findByParId((Integer) input.get("par_id")));
			if (Objects.nonNull(input.get("iddepot"))) {
				document.setDepot(depotDao.findByDepotId(UUID.fromString((String) input.get("iddepot"))));
			}
			document.setElasticid((String) input.get("elasticid"));
			document.setDomnNom((String) input.get("domnnom"));
			document.setServiceverseur((String) input.get("serviceverseur"));
			document.setOrganisationversante((String) input.get("organisationversante"));
			document.setServiceverseur(appParam.getNeogedserver());
			document.setLot((String) input.get("lot"));
			document.setConteneur((String) input.get("conteneur"));
			document.setArchiverId(user);
			document.setStatut(DocumentStatut.REARDY_FOR_ARCHIVE.getStatutCode());

			if (storageService.archive(document)) {
				LOGGER.info("document stored - nfz42013");
				LOGGER.info("empreinte " + document.getDocId() + " stored - nfz42013");
				if (!noGED) {
					HashMap<String, Object> GEDInfo = new HashMap<String, Object>();
					GEDInfo.put("user", document.getArchiverId());
					GEDInfo.put("elasticid", document.getElasticid());
					GEDInfo.put("archivage_profile_id",
							Objects.isNull(document.getProfile()) ? null : document.getProfile().getParId());
					GEDInfo.put("docid", document.getDocId().toString());
					GEDInfo.put("keywords", document.getKeywords());
					GEDInfo.put("doctype", document.getDoctype());
					GEDInfo.put("mailowner", document.getMailowner());
					GEDInfo.put("domainowner", document.getDomaineowner());
					GEDInfo.put("archive_date", document.getArchiveDate());
					GEDInfo.put("archive_status", DraftStatut.ARCHIVING.toString());

					try {
						updateGED(GEDInfo, Commande.STORE);
					} catch (PersistenceException e) {
						throw new AvpExploitException("611", e,
								"Mise à jour de la GEd avec les informations d''archivage", null,
								document.getDocId().toString(), null);
					}
					LOGGER.info("document " + document.getDocId() + " updated in ged - nfz42013");
				}
				if (storeFromAVP) {
					try {
						// Update Draft's state
						draftDao.updateStoredDraft(draftid, document.getDocId(), DraftStatut.ARCHIVING, "Pré-archivé");
					} catch (PersistenceException e) {
						throw new AvpExploitException("101", e,
								"Mise à jour du draft avec les informations d''archivage", null,
								document.getDocId().toString(), null);
					}
				}
				resultat.put("codeRetour", "OK");
				resultat.put("message", "");
				resultat.put("docid", document.getDocId().toString());
			} else {
				resultat.put("codeRetour", "99");
				resultat.put("message", "impossible de stocker le document dans le file système");
			}

		} else {
			resultat.put("codeRetour", "55");
			resultat.put("message", "document not found");

		}
	}

	/**
	 * Calculer la date fin de conservation
	 * 
	 * @param userId
	 * @param doctype
	 * @param categorie
	 * @param horodatage
	 * @return
	 */
	private ZonedDateTime getDestructionDate(final String userId, final String doctype, final String categorie,
			final ZonedDateTime horodatage) {

		DocType docType = docTypeDao.findByDocTypeArchivageAndCategorie(doctype, categorie);

		Profile profile = docType.getProfile();

		// If DocType is not associated with any profil, 60 months are added for archive
		if (Objects.isNull(profile) || Objects.isNull(profile.getParConversation()))
			return horodatage.plus(60, ChronoUnit.MONTHS);

		// If several profiles are found,
		return horodatage.plus(profile.getParConversation(), ChronoUnit.MONTHS);
	}

	/**
	 * Crypter un contenu en byte,
	 * 
	 * @param bytesToEncrypt
	 * @param encryptionKey
	 * @return un map contient le contenu chiffre et le initial vector
	 * @throws AesCipherException
	 */
	public Map<String, byte[]> encrypt(final byte[] bytesToEncrypt, final EncryptionKey encryptionKey)
			throws AesCipherException {

		return AesCipher.encrypt(encryptionKey.getEncodedkey(), bytesToEncrypt);
	}

	/**
	 * Decrypter un message byte
	 * 
	 * @param secretKey
	 * @param initVector
	 * @param bytesToDecrypt
	 * @return message bypt dechiffre
	 * @throws AesCipherException
	 */
	public byte[] decrypt(final byte[] bytesToDecrypt, final EncryptionKey encryptionKey, final byte[] initVector)
			throws AesCipherException {
		return AesCipher.decrypt(encryptionKey.getEncodedkey(), initVector, bytesToDecrypt);
	}

	/**
	 * Calcul l'empreinte (hash) du document
	 * 
	 * @param document
	 *            le document
	 * @return l'empreinte calculée
	 * @throws AvpExploitException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public String computePrint(final Document document) throws AvpExploitException {
		try {
			return Sha.encode(document.getTitle() + document.getArchiveDate().toString()
					+ Base64.getEncoder().encodeToString(document.getContent()), "utf-8");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO : AVPExploitException a gerer
			throw new AvpExploitException("1", e);
		}
	}

	/**
	 * Calcul l'empreinte TELINO (hash) du document (mot de passe 1 et 2 et contenu
	 * du document)
	 * 
	 * @param document
	 *            le document
	 * @return l'empreinte calculée
	 * @throws AvpExploitException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public String computeTelinoPrint(final Document document) throws AvpExploitException {
		try {
			return Sha.encode(ServerProc.password1 + ServerProc.password2
					+ Base64.getEncoder().encodeToString(document.getContent()), "utf-8");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO : AVPExploitException a gerer
			throw new AvpExploitException("1", e);
		}
	}

	private String getMd5(byte[] bytes) {
		String md5 = "";
		try {
			TlnMd5 tlnMd5 = new TlnMd5(TlnMd5.MD5);
			md5 = tlnMd5.computeDigest(bytes);
		} catch (Exception e) {
			e.printStackTrace();

			return null;

		}
		return md5;
	}
}
