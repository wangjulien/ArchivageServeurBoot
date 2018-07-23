package com.telino.avp.service.schedule;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telino.avp.dao.BgServiceDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.LogEventDao;
import com.telino.avp.dao.ParamAutomateDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.LogArchive;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.entity.param.BgService;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.protocol.DbEntityProtocol.BackgroundService;
import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.tools.RemoteCall;

@Service
public class ScheduledArchivageAnalysis {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledArchivageAnalysis.class);

	private static final String APP_NAME = "ADELIS";
	private static final String USER_ID = "system";
	private static final String APP_VERSION = "1";
	private static final int MAX_CHECK_FILES_THREAD = 5;

	private static final String DEFAULT_AUTORC_SERVLET_URL = "http://localhost:8087/automateRC/AutomateServiceServlet";
	private static final int HOURS_BETWEEN_INTERGITY_CHECK = 24;

	@Value("${app.archivageserveur.url}")
	private String avpServletUrl;

	private final String process;

	@Autowired
	private BgServiceDao bgServiceDao;

	@Autowired
	private ParamAutomateDao paramAutomateDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private LogArchiveDao logArchiveDao;

	@Autowired
	private LogEventDao logEventDao;

	@Autowired
	private RemoteCall remoteCall;

	@Autowired
	private SwitchDataSourceService switchDataSourceService;

	public ScheduledArchivageAnalysis() throws UnknownHostException {
		super();

		this.process = InetAddress.getLocalHost().getHostAddress();
	}

	@Transactional(rollbackFor = Exception.class)
	public void launchBackgroudServices(String nomBase) {

		// Switch datasource pour ce nomBase
		try {
			switchDataSourceService.switchDataSourceFor(nomBase);
		} catch (AvpExploitException e) {
			LOGGER.error(e.getMessage());
			// Log in LogEvent???
			throw new RuntimeException();
		}

		//
		// Parcour la table bgservices
		//

		List<BgService> bgServices = bgServiceDao.findAll();

		for (BgService bgs : bgServices) {

			BackgroundService bgsType = BackgroundService.valueOf(bgs.getBgsCod());

			// Si le traitement n'est pas demarre ou est deja demarre depuis ..
			if (!checkToDo(bgs))
				continue;

			// mettre a jour date de demarrage dans la DB
			bgs.setBgsProcess(process);
			bgs.setBgsStart(ZonedDateTime.now());
			bgServiceDao.save(bgs);

			switch (bgsType) {
			case CHECKFILES:
				// Service demarre depuis plus de 24h, on peut relancer
				if (isBgsStartMoreThan(bgs, HOURS_BETWEEN_INTERGITY_CHECK)) {

					if (checkFiles(nomBase)) {
						LOGGER.info(
								"Contrôle de l'intégralité des archives effectué et réussi pour toutes les archives");
					} else {
						LOGGER.info("Contrôle de l'intégralité des archives n'est pas passé pour toutes les archives");
					}
				}

				break;
			case CREATELOGARCHIVE:

				if (toSealLogArchive()) {
					LOGGER.info("Doit sceller le journal archive");

					createCtlLog(nomBase);
				} else {
					LOGGER.info("Pas de scellement du journal des archives");
				}

				break;
			case CREATELOGEVENT:

				if (toSealLogEvent()) {
					LOGGER.info("Doit sceller le journal event");

					createLogEvent(nomBase);
				} else {
					LOGGER.info("Pas de scellement du journal des évènements");
				}

				break;
			case DESTROY:

				if (destroy(nomBase)) {
					LOGGER.info("Destruction effectuée");
				} else {
					LOGGER.info("Pas de destruction d'archive");
				}

				break;
			case IMPORTFILES:

				if (importFiles(nomBase)) {
					LOGGER.info("Importe des documents par aumtomateRC module reussi");
				} else {
					LOGGER.info("Importe des documents par aumtomateRC module echoue");
				}

				break;
			default:
				LOGGER.error("BgService code inconnu : " + bgs.getBgsCod());
				break;
			}
		}
	}

	/**
	 * 
	 * @param nomBase
	 * @param values
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void createCtlLog(String nomBase) {
		Map<String, Object> request = new HashMap<>();
		request.put("command", "createlogcheck");
		request.put("bgTask", true);
		request.put("application", "NEOGED");
		request.put("user", "ADMIN");
		request.put("mailid", "");
		request.put("nomBase", nomBase);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (HashMap<String, Object>) remoteCall.callServlet(request, avpServletUrl,
					nomBase);
			LOGGER.info(result.toString());
		} catch (ClassNotFoundException | IOException e) {
			LOGGER.error("Erreur lors d'appeler Archivage Servlet : " + avpServletUrl);
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @param nomBase
	 * @param values
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void createLogEvent(String nomBase) {
		Map<String, Object> request = new HashMap<>();
		request.put("command", "createlogevent");
		request.put("bgTask", true);
		request.put("application", "NEOGED");
		request.put("user", "ADMIN");
		request.put("mailid", "");
		request.put("nomBase", nomBase);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (HashMap<String, Object>) remoteCall.callServlet(request, avpServletUrl,
					nomBase);
			LOGGER.info(result.toString());
		} catch (ClassNotFoundException | IOException e) {
			LOGGER.error("Erreur lors d'appeler Archivage Servlet : " + avpServletUrl);
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * Verifier si on peut lancer la tache background : - si bgs_on active
	 * 
	 * @param bgs
	 * @return
	 */
	private boolean checkToDo(final BgService bgs) {

		// Ce bgs est active
		if (bgs.isBgsOn() && (Objects.isNull(bgs.getBgsProcess()) // pas de Process IP
				|| this.process.equals(bgs.getBgsProcess()) // ou Process IP est soi meme
				|| isBgsStartMoreThan(bgs, 1)) // ou Demarrer depuis 1h par l'autre Process
		) {
			return true;
		}

		return false;
	}

	/**
	 * Si le Bgs est demarre plus de N hours
	 * 
	 * @param bgs
	 * @param hoursToWait
	 * @return
	 */
	private boolean isBgsStartMoreThan(final BgService bgs, final long hoursToWait) {
		if (Objects.isNull(bgs.getBgsStart()))
			return true;

		// StartTime plus n hours est avant NOW
		if (ZonedDateTime.now().isAfter(bgs.getBgsStart().plus(hoursToWait, ChronoUnit.HOURS)))
			return true;
		else
			return false;
	}

	/**
	 * si on est entre 23h-24h, ou une scellement est fait plus d'une heure
	 * 
	 * @return
	 */
	private boolean toSealLogArchive() {
		boolean sealedLogLessOneHour = false;

		Optional<LogArchive> logArchiveOpt = logArchiveDao.getLastSealedLog();
		if (logArchiveOpt.isPresent()) {

			// Is there a LogArchive done less than 1 hour?
			sealedLogLessOneHour = logArchiveOpt.get().getHorodatage()
					.isBefore(ZonedDateTime.now().minus(1, ChronoUnit.HOURS));
		} else {
			sealedLogLessOneHour = true;
		}

		LocalTime nowTime = LocalTime.now();
		if ((nowTime.isBefore(LocalTime.of(23, 59)) && nowTime.isAfter(LocalTime.of(23, 00))) || sealedLogLessOneHour) {
			return true;
		} else {
			if (logArchiveOpt.isPresent())
				LOGGER.info("Scellement du journal existe déjà : logid = " + logArchiveOpt.get().getLogId());
			return false;
		}
	}

	/**
	 * si on est entre 23h-24h, ou une scellement est fait plus d'une heure
	 * 
	 * @return
	 */
	private boolean toSealLogEvent() {
		boolean sealedLogLessOneHour = false;

		Optional<LogEvent> logEventOpt = logEventDao.getLastSealedLog();
		if (logEventOpt.isPresent()) {

			// Is there a LogArchive done less than 1 hour?
			sealedLogLessOneHour = logEventOpt.get().getHorodatage()
					.isBefore(ZonedDateTime.now().minus(1, ChronoUnit.HOURS));
		} else {
			sealedLogLessOneHour = true;
		}

		LocalTime nowTime = LocalTime.now();
		if ((nowTime.isBefore(LocalTime.of(23, 59)) && nowTime.isAfter(LocalTime.of(23, 00))) || sealedLogLessOneHour) {
			return true;
		} else {
			if (logEventOpt.isPresent())
				LOGGER.info("Scellement du journal existe déjà : logid = " + logEventOpt.get().getLogId());
			return false;
		}
	}

	/**
	 * A expiration d'archivage du document, suppression (logically)
	 * 
	 * @param values
	 * @param nomBase
	 * @return
	 */
	private boolean destroy(final String nomBase) {

		List<Document> docsToDelete = documentDao.getAllDocToDelete();

		for (Document doc : docsToDelete) {
			LOGGER.info("should destroy " + doc.getDocId());
			// TRUE signifie un logical delete
			logicalDestroy(doc, nomBase, true);
		}

		return docsToDelete.size() > 0;
	}

	private void logicalDestroy(final Document doc, final String nomBase, final boolean isLogicalDelete) {

		Map<String, Object> request = new HashMap<String, Object>();
		if (isLogicalDelete)
			request.put("command", Commande.LOGICAL_DELETE.toString());
		else
			request.put("command", Commande.DELETE.toString());

		request.put("bgTask", true);
		request.put("docid", doc.getDocId().toString());
		request.put("elasticid", doc.getElasticid());
		request.put("application", "ADELIS");
		request.put("user", doc.getArchiverId());
		request.put("mailid", doc.getArchiverMail());
		request.put("docsname", doc.getTitle());
		request.put("nomBase", nomBase);

		// setDestroyRights(values, conn, "DESTROY");
		// request.put("user", (String) values.get("user"));
		// request.put("password", (String) values.get("password"));

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (HashMap<String, Object>) remoteCall.callServlet(request, avpServletUrl,
					nomBase);
			LOGGER.info(result.toString());
		} catch (ClassNotFoundException | IOException e) {
			LOGGER.error("Erreur lors d'appeler Archivage Servlet : " + avpServletUrl);
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * Lancement de controle de l'integralite des documents
	 * 
	 * @param values
	 * @param nomBase
	 * @return
	 */
	private boolean checkFiles(String nomBase) {

		final int maxCheckFilesThread = MAX_CHECK_FILES_THREAD;

		LOGGER.info("Nombre maximal de threads : " + maxCheckFilesThread);

		final int nbDocs = documentDao.getTotalArchiveNum();

		final int limit = nbDocs / maxCheckFilesThread + 1;
		int page = 0;

		ExecutorService executor = Executors.newFixedThreadPool(maxCheckFilesThread);
		List<Callable<Map<String, Object>>> tasks = new ArrayList<>();

		while (page * limit < nbDocs) {

			List<UUID> docids = documentDao.getDocListToCheck(page, limit).stream().map(Document::getDocId)
					.collect(Collectors.toList());

			Map<String, Object> request = new HashMap<String, Object>();
			request.put("command", Commande.CHECK_FILES.toString());
			request.put("bgTask", true);
			try {
				request.put("docids", new ObjectMapper().writeValueAsString(docids));
			} catch (JsonProcessingException e) {
				LOGGER.error("Erreur lors d'ecrire docids en JSON " + docids);
				LOGGER.error(e.getMessage());
				return false;
			}
			request.put("application", APP_NAME);
			request.put("nomBase", nomBase);

			// log_archive
			request.put("operation", BackgroundService.CHECKFILES.getDetail());
			request.put("userid", USER_ID);
			request.put("mailid", APP_NAME);

			// log_event
			request.put("origin", APP_NAME);
			request.put("processus", BackgroundService.CHECKFILES.toString());
			request.put("action", BackgroundService.CHECKFILES.getDetail());
			request.put("operateur", APP_NAME);
			request.put("version", APP_VERSION);

			LOGGER.debug(avpServletUrl + " " + request);

			//
			// Creer un Thread par Call
			//

			@SuppressWarnings("unchecked")
			Callable<Map<String, Object>> call = () -> {
				return (HashMap<String, Object>) remoteCall.callServlet(request, avpServletUrl, nomBase);
			};

			// Liste de task a excuter
			tasks.add(call);

			// aller vers prochain parkage
			page++;
		}

		boolean allThreadOk = true;

		try {
			for (Future<Map<String, Object>> result : executor.invokeAll(tasks)) {
				Map<String, Object> resultMap = result.get();

				if (!ReturnCode.OK.toString().equals((String) resultMap.get("codeRetour"))) {
					// Si traitement reussi
					LOGGER.error("Erreur lors de contrôler de l'intégralité des archives par ce thread : " + resultMap);
					allThreadOk = false;
				} else {
					LOGGER.info("Contrôler de l'intégralité des archives réussit par ce thread : " + resultMap);
				}

			}
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Erreur lors de l'execution des threads - interrompue ou échouée" + e.getMessage());
			allThreadOk = false;
		}

		executor.shutdownNow();

		return allThreadOk;
	}

	/**
	 * Lancer importe des fichiers par Automate module
	 * 
	 * @param values
	 * @param nomBase
	 * @return
	 */
	private boolean importFiles(String nomBase) {

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("command", Commande.IMPORT_FILES.toString());
		request.put("bgTask", "true");
		request.put("application", APP_NAME);
		request.put("nomBase", nomBase);

		// Recupere URL
		String autoRcServletUrl = paramAutomateDao.getParaAutomate().orElse(DEFAULT_AUTORC_SERVLET_URL);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (HashMap<String, Object>) remoteCall.callServlet(request, autoRcServletUrl,
					nomBase);

			Objects.requireNonNull(resultMap, "Module Automate RC n'est pas accessible : " + autoRcServletUrl);

			if (!ReturnCode.OK.toString().equals((String) resultMap.get("codeRetour"))) {
				// Si traitement reussi
				LOGGER.error("Erreur lors d'importer des fichiers par AutomateRC module : " + resultMap);
				return false;
			} else {
				return true;
			}
		} catch (ClassNotFoundException | IOException e) {
			LOGGER.error("Erreur lors d'appeler AutomateRC module : " + autoRcServletUrl);
			LOGGER.error(e.getMessage());
			return false;
		}
	}
}
