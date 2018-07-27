package com.telino.avp.servlets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.telino.avp.config.ScheduleConfig;
import com.telino.avp.service.schedule.ScheduledArchivageAnalysis;
import com.telino.avp.tools.CdmsApiServletRequestIO;

@Controller
@RequestMapping("/startCdmsAnalysis")
public class StartCdmsAnalysisController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartCdmsAnalysisController.class);

	@Value("${app.threadanalysis.cycletime}")
	private int cycleRate;

	@Autowired
	private TaskScheduler threadPoolTaskScheduler;

	@Autowired
	private ScheduledArchivageAnalysis scheduledArchivageAnalysis;

	@RequestMapping(params = { "nomBase" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void doGetAndPost(@RequestParam("nomBase") String nomBase, HttpServletRequest request,
			HttpServletResponse response) {

		LOGGER.info("Entering startCdmsAnalysis V0 " + nomBase);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> trame = (HashMap<String, Object>) CdmsApiServletRequestIO.lecture(request); // lecture de la
																											// trame
			// parse trame
			String param = trame.get("ordre").toString();
						
			// Start the back ground service by database name
			if (param.contains("start")) {
				if (ScheduleConfig.CTE.get(nomBase) == null) {
					// if the scheduled background service is not started
					ScheduledFuture<?> future = threadPoolTaskScheduler.scheduleWithFixedDelay(
							() -> scheduledArchivageAnalysis.launchBackgroudServices(nomBase), cycleRate * 60 * 1000);
					// ajouter
					ScheduleConfig.CTE.putIfAbsent(nomBase, future);

					CdmsApiServletRequestIO.ecriture(response, "OK");
					return;
				} else {
					LOGGER.info("Allready running");
					CdmsApiServletRequestIO.ecriture(response, "OK");
				}
			// Stop the back ground service by database name
			} else if (param.equals("stop")) {
				if (ScheduleConfig.CTE.get(nomBase) != null)
					ScheduleConfig.CTE.get(nomBase).cancel(true);
				ScheduleConfig.CTE.remove(nomBase);
				CdmsApiServletRequestIO.ecriture(response, "OK");
			} else {
				CdmsApiServletRequestIO.ecriture(response, "Service inconnu : " + param);
				return;
			}
		} catch (Exception e) {
			LOGGER.error("erreur servlet startCdmsAnalysis");
			try {
				CdmsApiServletRequestIO.ecriture(response, "Erreur d'execution " + e.getMessage());
			} catch (Exception ee) {
				LOGGER.error("erreur d'ecrire une reponse pour startCdmsAnalysis" + ee.getMessage());
			}

		}
	}
}
