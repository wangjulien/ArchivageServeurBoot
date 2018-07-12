package com.telino.avp.service.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.ExpTaskDao;
import com.telino.avp.dao.LogEventDao;
import com.telino.avp.entity.auxil.ExpTask;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.exception.ExpTaskException;

/**
 * 
 * Une service qui lance des taches programmes - taches d'exploitation -
 * implementation par Spring "scheduled task" - les periodes de lancement sont
 * parametres dans application.propeties
 * 
 * @author Jiliang.WANG
 *
 */
@Service
public class ScheduledTaskService implements IScheduledTaskService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskService.class);

	@Autowired
	private LogEventDao logEventDao;
	
	@Autowired
	private ExpTaskDao expTaskDao;
	
	@Autowired
	private ExpTaskBuilder expTaskBuilder;
	
	@Autowired
	private ExpTaskChecker expTaskChecker;

	/*
	 * tache programme de remplissation
	 * 
	 * @throws ExpTaskException
	 */
	@Override
	@Scheduled(cron = "${app.fillingTaskRate.cronexp}")
	@Transactional(rollbackFor = Exception.class)	// Default timeout is 5 mins (jta.properties)
	public void scheduledFillingTask() throws ExpTaskException {

		LOGGER.info("Lancement de remplissage d'exploitation ");
		try {
			// Recuperer depuis log_event les problemes :
			final List<LogEvent> logEvents = logEventDao.findAllArchiveIdFailedCheckEntirety();

			if (logEvents.isEmpty()) {
				LOGGER.info("Pas de nouveau journal d'évenement ");
				return;
			}

			// Analyser les log_event pour chaque archive pour creer different taches
			// d'exploitation
			List<ExpTask> expTasks = exploitLogEventForExpTask(logEvents);

			// Persister les taches d'exploitation dans exp_task
			expTaskDao.saveExpTasks(expTasks);
			

			// Marque fait dans log_event
			logEventDao.terminateExploitedEvent(logEvents);


			LOGGER.info("Fin de remplissage d'exploitation " + expTasks.size());
			
			
		} catch (DataAccessException e) {
			LOGGER.error(e.getMessage());

			throw new ExpTaskException(e);
		}

	}

	/**
	 * tache programme de l'exploitation
	 * 
	 * @throws ExpTaskException
	 */
	@Override
	@Scheduled(cron = "${app.exploitationTaskRate.cronexp}")
	public void scheduledExploitationTask() throws ExpTaskException {
		try {
			
			LOGGER.info("Abandonner les tâches d'exploitation ");
			expTaskChecker.abortExpiredExpTask();
			
			LOGGER.info("Relancer les tâches d'exploitation ");
			expTaskChecker.findAndRelaunchExpTask();
			
			LOGGER.info("Lancer les tâches d'exploitation initialisées");
			expTaskChecker.findAndLaunchInitExpTask();

		} catch (DataAccessException e) {
			LOGGER.error(e.getMessage());

			throw new ExpTaskException(e);
		}
	}

	/**
	 * exploite une liste de log_event et creer les taches d'exploitation
	 * correspondante
	 * 
	 * @param logEvents
	 *            : liste de log_event a exploiter
	 * @return liste de taches d'exploitation crees
	 */
	private List<ExpTask> exploitLogEventForExpTask(final List<LogEvent> logEvents) {

		Map<UUID, List<LogEvent>> logEventsByArchive = logEvents.stream()
				.collect(Collectors.groupingBy(e -> e.getArchive().getDocId()));

		List<ExpTask> expTasks = new ArrayList<>();

		for (Entry<UUID, List<LogEvent>> el : logEventsByArchive.entrySet()) {

			if (el.getValue().size() > 1) {
				// Cas 1 : une archive qui ont deux error dans Master et Mirror respectivement
				expTasks.add(expTaskBuilder.checkAndBuildTask(el.getValue()));
			} else {
				// Cas 2 : une erreur par archive
				expTasks.add(expTaskBuilder.buildTask(el.getValue().get(0)));
			}
		}

		return expTasks;
	}
}