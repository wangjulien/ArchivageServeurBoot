package com.telino.avp.service.schedule;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.ExpTaskDao;
import com.telino.avp.dao.masterdao.MasterExpTaskRepository;
import com.telino.avp.entity.auxil.ExpTask;
import com.telino.avp.exception.ExpTaskException;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskState;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskType;
import com.telino.avp.tools.RemoteCall;

/**
 * Controleur d'etat des tache d'exploitation et Lanceur
 * 
 * @author Jiliang.WANG
 *
 */
@Component
public class ExpTaskChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpTaskChecker.class);
	
	private static final String APP_NAME = "ADELIS";
	
	@Value("${archivageserveur.url}")
	private String urlServeur;
	
	@Value("${spring.masterds.db}")
	private String dbName;
	
	@Autowired
	private MasterExpTaskRepository masterExpTaskDao;
	
	@Autowired
	private ExpTaskDao expTaskDao;

	@Autowired
	private ExpTaskLauncher expTaskLaucher;

	public ExpTaskChecker() {
		super();
	}

	/**
	 * Abondonner les taches expirees
	 */
	@Transactional(rollbackFor = Exception.class)
	public void abortExpiredExpTask() {
		List<ExpTask> expiredTasks = masterExpTaskDao.findExpiredExpTask(ExpTaskState.E.toString());

		if (expiredTasks.isEmpty()) {
			LOGGER.info("Aucune tâche abondonnée ");
			return;
		}

		expiredTasks.forEach(tk -> tk.setState(ExpTaskState.A));

		saveExpTaskInBothDb(expiredTasks);

		LOGGER.info("Nombre de tâche abondonnée : " + expiredTasks.size());
	}

	/**
	 * Relancer les taches pouvant etre relancer
	 * @throws ExpTaskException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void findAndRelaunchExpTask() throws ExpTaskException {
		List<ExpTask> expiredTasks = masterExpTaskDao.findToRelauchByStatusAndExpired(ExpTaskState.E.toString());

		if (expiredTasks.isEmpty()) {
			LOGGER.info("Aucune tâche à relancer ");
			return;
		}

		launchExpTask(expiredTasks);
		LOGGER.info("Nombre de tâche relancée : " + expiredTasks.size());
	}

	/**
	 * Lancer les taches d'exploitation
	 * @throws ExpTaskException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void findAndLaunchInitExpTask() throws ExpTaskException {
		List<ExpTask> initTasks = masterExpTaskDao.findByStateAndTaskTypeIdIsNot(ExpTaskState.I,
				ExpTaskType.NEED_HUMAN_INTERVENTION.getTypeId());

		if (initTasks.isEmpty()) {
			LOGGER.info("Aucune tâche initialisée à lancer ");
			return;
		}


		launchExpTask(initTasks);
		LOGGER.info("Nombre de tâche lancée : " + initTasks.size());
	}

	
	private void launchExpTask(List<ExpTask> expTasks) throws ExpTaskException {
	
		List<Callable<Map<String, Object>>> tasksQueue = new ArrayList<>();

		expTasks.forEach(tk -> enqueueExpTask(tk, tasksQueue));
		
		saveExpTaskInBothDb(expTasks);		

		// Methode Asynchrone, n'attend pas la reponse
		expTaskLaucher.launchExpTask(tasksQueue);
	}
	
	private void enqueueExpTask(ExpTask task, List<Callable<Map<String, Object>>> tasksQueue) {
		task.setState(ExpTaskState.E);
		task.setDateDeb(ZonedDateTime.now());
		task.setNbTries(task.getNbTries() + 1);
		
		// Valoriser les parametres de l'appel http
		Map<String, Object> request = new HashMap<>();
		request.put("application", APP_NAME);
		request.put("bgTask", true);
		request.put("command", task.getTaskType().toString());
		request.put("taskid", task.getTaskId().toString());
		request.put("docid", task.getDocument().getDocId().toString());
		request.put("nomBase", dbName.toUpperCase());
		
		LOGGER.debug(urlServeur + " " + request);

		// Creer un Thread par Call

		@SuppressWarnings("unchecked")
		Callable<Map<String, Object>> call = () -> {
			RemoteCall RC = new RemoteCall();
			return (HashMap<String, Object>) RC.callServlet(request, urlServeur, "");
		};

		// Liste de task a excuter
		tasksQueue.add(call);
	}

	
	
	/**
	 * Fonction utilitaire pour persister/mettre a jour des taches dans les deux DB
	 * 
	 * @param tasks
	 *            : liste de taches a persister dans les deux db
	 */
	public void saveExpTaskInBothDb(List<ExpTask> tasks) {
		expTaskDao.saveExpTasks(tasks);
	}
	
		
	@Transactional(rollbackFor = Exception.class)
	public void updateExpTaskStateInBothDb(UUID taskId, ExpTaskState state) {
		Optional<ExpTask> optTask = masterExpTaskDao.findById(taskId);
		if (optTask.isPresent()) {
			
			ExpTask expTask = optTask.get();
			expTask.setState(state);
			expTask.setDateFin(ZonedDateTime.now());
			expTaskDao.saveExpTask(expTask);
		}
	}
}
