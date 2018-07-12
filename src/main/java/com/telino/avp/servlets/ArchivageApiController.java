package com.telino.avp.servlets;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.telino.avp.dao.DepotDao;
import com.telino.avp.entity.archive.Depot;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.service.ArchivageApiService;
import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.service.journal.JournalEventService;
import com.telino.avp.tools.CdmsApiServletRequestIO;

@Controller
@RequestMapping("/archivageserveur/ArchivageService")
public class ArchivageApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArchivageApiController.class);

	@Value("${spring.paramds.id:AVPNAV}")
	private String systEnvDsId;

	@Autowired
	private DepotDao depotDao;

	@Autowired
	private JournalEventService journalEventService;

	@Autowired
	private SwitchDataSourceService switchDataSourceService;

	@Autowired
	private ArchivageApiService archivageApis;

	@SuppressWarnings("unchecked")
	@RequestMapping(params = { "nomBase", "environnement", "init" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void doGetAndPost(@RequestParam("nomBase") String mybase,
			@RequestParam("environnement") String environnement, @RequestParam("init") String init,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (Objects.nonNull(mybase)) {

			// Initialization of DS
			if (Objects.nonNull(init)) {

				Map<String, Object> result = new HashMap<>();
				try {
					//
					// TODO : Switch DataSource par AOP intercepter
					//
					switchDataSourceService.switchDataSourceFor(mybase);
					result.put("codeRetour", ReturnCode.OK);

					LOGGER.info(" Réinit effectué - nfz42013");
				} catch (PersistenceException e) {
					LOGGER.error(e.getMessage());
					result.put("codeRetour", ReturnCode.KO);
					result.put("message", e.getMessage());
				}

				CdmsApiServletRequestIO.ecriture(response, result, true);
				return;
			}
		}

		boolean isMap = true;
		boolean multi = false;
		Map<String, Object> trame = null;
		Map<String, Object> header = null;
		try {
			Object inputObject = CdmsApiServletRequestIO.lecture(request);
			Object input;
			if (inputObject instanceof String) {
				isMap = false;
				input = CdmsApiServletRequestIO.formatToMap(inputObject, request);
			} else {
				isMap = true;
				input = inputObject;
			}

			String demandeur = "";

			if (input instanceof LinkedList) {
				multi = true;
				header = ((LinkedList<HashMap<String, Object>>) input).get(0);
				if (!header.containsKey("demandeur") && !header.containsKey("mailowner")) {
					Map<String, Object> result = new HashMap<>();
					result.put("codeRetour", "99");
					result.put("message", "Le demandeur est obligatoire");
					CdmsApiServletRequestIO.ecriture(response, result, isMap);
					return;
				} else {
					demandeur = (String) header.get("demandeur");
				}
			} else {
				multi = false;
				trame = (HashMap<String, Object>) input;
				header = trame;
			}

			//
			// TODO : Switch DataSource par AOP intercepter
			//
			switchDataSourceService.switchDataSourceFor(mybase);

			UUID idDepot = null;
			if (Commande.STORE.toString().equals((String) header.get("command"))) {

				idDepot = UUID.randomUUID();
			}

			Map<String, Object> result = new HashMap<>();

			if (multi) {
				List<HashMap<String, Object>> bigRequest = (LinkedList<HashMap<String, Object>>) input;

				for (int i = 0; i < bigRequest.size(); i++) {
					trame = bigRequest.get(i);
					if (Commande.STORE.toString().equals((String) header.get("command")))
						trame.put("iddepot", idDepot.toString());

					// API actions
					result = archivageApis.execApi(trame);

					if (!ReturnCode.OK.toString().equals((String) result.get("codeRetour"))
							&& Commande.STORE.toString().equals((String) header.get("command"))) {

						// TODO : rollBack(conn, connMirror, false);

						// Persist a Depot object
						Depot depot = new Depot();
						depot.setIdDepot(idDepot);
						depot.setDemandeur(demandeur);
						depot.setStatus((String) result.get("codeRetour"));
						depot.setMessage((String) result.get("message"));
						depot.setHorodatage(ZonedDateTime.now());

						depotDao.saveDepot(depot);

						CdmsApiServletRequestIO.ecriture(response, result, isMap);
						return;
					}
				}

				if (Commande.STORE.toString().equals((String) header.get("command"))) {
					result.put("iddepot", idDepot.toString());

					// Persist a Depot object
					Depot depot = new Depot();
					depot.setIdDepot(idDepot);
					depot.setDemandeur(demandeur);
					depot.setStatus(ReturnCode.OK.toString());
					depot.setMessage(ReturnCode.OK.getDepotMessage());
					depot.setHorodatage(ZonedDateTime.now());

					depotDao.saveDepot(depot);
				}

				CdmsApiServletRequestIO.ecriture(response, result, isMap);

			} else {
				if (Commande.STORE.toString().equals((String) header.get("command")))
					trame.put("iddepot", idDepot.toString());

				Map<String, Object> printObject = new HashMap<>();
				if (trame.containsKey("content")) {
					Iterator<String> IT = trame.keySet().iterator();
					while (IT.hasNext()) {
						String key = IT.next();
						if (key.equals("content"))
							printObject.put(key, "....");
						else
							printObject.put(key, trame.get(key));
					}
					LOGGER.info(printObject + " - nfz42013");

				} else {
					LOGGER.info(input + " - nfz42013");
				}

				result = archivageApis.execApi(trame);

				CdmsApiServletRequestIO.ecriture(response, result, isMap);

				// TODO : gestion de transaction
				// if (ReturnCode.OK.toString().equals(result.get("codeRetour")) ) {
				// commit(conn, connMirror);
				// } else {
				// rollBack(conn, connMirror, true);
				//
				// }

				LOGGER.info("Command " + header.get("command") + " ends");
			}

		} catch (AvpExploitException e) {
			LOGGER.error("Erreur servlet d'archivage " + e.getMessage());

			// TODO : gestion de transaction
			// rollBack(conn, connMirror, false);

			Map<String, Object> inputToLog1 = new HashMap<>();
			inputToLog1.put("origin", "ADELIS");
			inputToLog1.put("operateur", "ADELIS");
			inputToLog1.put("version", "1");
			inputToLog1.put("processus", Objects.nonNull(e.getProcessus()) ? e.getProcessus()
					: Commande.getEnum((String) header.get("command")).getProcess());
			inputToLog1.put("action", e.getAction());
			inputToLog1.put("logtype", "E");
			inputToLog1.put("detail", AvpExploitException.getTableLibelleErreur().get(e.getMessage())[0]);
			inputToLog1.put("archiveid", e.getArchiveId());
			inputToLog1.put("journalid", e.getJournalId());
			inputToLog1.put("methode", e.getMethodName());
			inputToLog1.put("trace", e.getMessage() + "." + Arrays.toString(e.getStackTrace()));

			try {
				journalEventService.log(inputToLog1);
			} catch (AvpExploitException e1) {
				LOGGER.error("problème dans logevent : " + e1.getMessage());
			}

			Map<String, Object> mapResultat = new HashMap<>();
			mapResultat.put("codeRetour", ReturnCode.KO.toString());
			mapResultat.put("message", AvpExploitException.getTableLibelleErreur().get(e.getMessage())[1]);

			// TaskId pour TaskExploitation
			if (!Objects.isNull(trame.get("taskid")))
				mapResultat.put("taskid", trame.get("taskid"));

			CdmsApiServletRequestIO.ecriture(response, mapResultat, isMap);

		}
	}

}
