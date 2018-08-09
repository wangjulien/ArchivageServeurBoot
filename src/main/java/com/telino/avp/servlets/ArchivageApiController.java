package com.telino.avp.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.protocol.DbEntityProtocol.LogEventType;
import com.telino.avp.service.ArchivageApiService;
import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.service.journal.JournalEventService;
import com.telino.avp.tools.CdmsApiServletRequestIO;

@Controller
@RequestMapping("/ArchivageService")
public class ArchivageApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArchivageApiController.class);

	@Value("${spring.paramds.id:AVPNAV}")
	private String systEnvDsId;

	@Autowired
	private JournalEventService journalEventService;

	@Autowired
	private SwitchDataSourceService switchDataSourceService;

	@Autowired
	private ArchivageApiService archivageApis;

	@SuppressWarnings("unchecked")
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public void doGetAndPost(@RequestParam("nomBase") String mybase,
			@RequestParam(value = "init", required = false) String init, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		if (Objects.nonNull(mybase)) {
			if (Objects.nonNull(init)) {
				// Since the server is stateless, the initialization of DS is inutil
				Map<String, Object> result = new HashMap<>();
//				try {
//					// Switch DataSource
//					switchDataSourceService.switchDataSourceFor(mybase);
//					result.put("codeRetour", ReturnCode.OK.toString());
//
//					LOGGER.info(" Réinit effectué - nfz42013");
//				} catch (AvpExploitException e) {
//					LOGGER.error(e.getMessage());
//					result.put("codeRetour", ReturnCode.KO.toString());
//					result.put("message", e.getMessage());
//				}
				
				result.put("codeRetour", ReturnCode.OK.toString());
				LOGGER.info(" Réinit effectué - nfz42013");
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

			// Switch DataSource
			switchDataSourceService
					.switchDataSourceFor(header.get("nomBase") == null ? "" : header.get("nomBase").toString());

			// Result Map to response
			Map<String, Object> result = new HashMap<>();
			
			// multi HasMap in a LinkedList
			if (multi) {
				List<HashMap<String, Object>> bigRequest = (LinkedList<HashMap<String, Object>>) input;

				for (int i = 0; i < bigRequest.size(); i++) {
					trame = bigRequest.get(i);
					
					// API actions
					result = archivageApis.execApi(trame);

					if (!ReturnCode.OK.toString().equals((String) result.get("codeRetour"))) {
						CdmsApiServletRequestIO.ecriture(response, result, isMap);
						return;
					}
				}

				CdmsApiServletRequestIO.ecriture(response, result, isMap);
			} else {
				
				// Logger command info of the HTTP request,
				// if there is content, 
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

				LOGGER.info("Command " + header.get("command") + " ends");
			}

		} catch (AvpExploitException e) {
			LOGGER.error("Erreur servlet d'archivage " + e.getMessage());

			Map<String, Object> inputToLog1 = new HashMap<>();
			inputToLog1.put("origin", "ADELIS");
			inputToLog1.put("operateur", "ADELIS");
			inputToLog1.put("version", "1");
			inputToLog1.put("processus", Commande.getEnum((String) header.get("command")).getProcess());
			inputToLog1.put("action", e.getAction());
			inputToLog1.put("logtype", LogEventType.E.toString());
			inputToLog1.put("detail", e.getCodeErreur().getInternalDetail());
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
			mapResultat.put("message", e.getCodeErreur().getExternalDetail());

			// TaskId pour TaskExploitation
			if (!Objects.isNull(trame.get("taskid")))
				mapResultat.put("taskid", trame.get("taskid"));

			CdmsApiServletRequestIO.ecriture(response, mapResultat, isMap);

		} catch (Exception e) {
			LOGGER.error("Erreur servlet d'archivage " + e.getMessage());
			
			HashMap<String, Object> inputToLog1 = new HashMap<String, Object>();
			inputToLog1.put("origin", "ADELIS");
			inputToLog1.put("operateur", "ADELIS");
			inputToLog1.put("version", "1");
			inputToLog1.put("processus", header.get("command"));
			inputToLog1.put("action", this.getClass().toString());
			inputToLog1.put("logtype", LogEventType.E.toString());
			inputToLog1.put("detail", e.getMessage());
			
			try {
				journalEventService.log(inputToLog1);
			} catch (AvpExploitException e1) {
				LOGGER.error("problème dans logevent : " + e1.getMessage());
			}

			Map<String, Object> mapResultat = new HashMap<>();
			mapResultat.put("codeRetour", ReturnCode.KO.toString());
			mapResultat.put("message", AvpExploitExceptionCode.SYSTEM_ERROR.getExternalDetail());
			
			// TaskId pour TaskExploitation
			if (null != trame.get("taskid"))
				mapResultat.put("taskid", trame.get("taskid"));
			
			CdmsApiServletRequestIO.ecriture(response, mapResultat, isMap);
			
		}
	}

}
