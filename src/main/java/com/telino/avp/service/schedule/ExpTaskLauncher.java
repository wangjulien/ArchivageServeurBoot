package com.telino.avp.service.schedule;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.telino.avp.dao.ExpTaskDao;
import com.telino.avp.exception.ExpTaskException;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskState;

/**
 * Lanceur des tache d'exploitation dans un thread apart
 * 
 * @author Jiliang.WANG
 *
 */
@Component
public class ExpTaskLauncher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpTaskLauncher.class);
	
	private final static int CHECK_FILES_TASK_TIMEOUT = 10;
	
	@Autowired
	private ExpTaskDao expTaskDao;
	
	@Autowired
	private ExecutorService executorService;

	public ExpTaskLauncher() {
		super();
	}

	/**
	 * Methode asynchrone pour post traitement des taches d'exploitation
	 * 
	 * @param tasksQueue
	 * @throws ExpTaskException 
	 */
	@Async
	public void launchExpTask(List<Callable<Map<String, Object>>> tasksQueue) throws ExpTaskException {
	
		try {
			List<Future<Map<String, Object>>> results = executorService.invokeAll(tasksQueue);
			
			// Afin que l'etat (E) de log_archive est mis a jour, et la reponse ne sois pas trop vite   
			Thread.sleep(1000);
			
			for (Future<Map<String, Object>> result : results) {
				
				// Recuperer les resultats des appel http lances
				Map<String, Object> resultMap = result.get(CHECK_FILES_TASK_TIMEOUT, TimeUnit.MINUTES);
				
				// Resultat retourne doit contenir la tache et son codeRetour
				if ( null != resultMap.get("taskid") && null != resultMap.get("codeRetour") ) {
					LOGGER.info("Resultat d'appel asynchrone : " + resultMap);
					
					UUID taskId = UUID.fromString((String) resultMap.get("taskid"));
					String codeRetour = (String) resultMap.get("codeRetour");		
					
					// Reussi
					if (ReturnCode.OK.toString().equals(codeRetour)) {
						// Si traitement reussi					
						expTaskDao.updateExpTaskStateInBothDb(taskId, ExpTaskState.T);
						
					} else {
						expTaskDao.updateExpTaskStateInBothDb(taskId, ExpTaskState.A);
						LOGGER.error("Erreur lors l'exécution de la tâche d'exploitation TaskId : " + taskId);
					}					
				} else {
					LOGGER.error("Code retour ou ID de la tâche manquant : " + resultMap);
				}				
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOGGER.error("Erreur lors de l'execution des threads - interrompue ou échouée " + e.getMessage());
			
			// Exception asynchrone, elle sera pas propage vers thread appelant (ExpTaskChecker)
			// AsyncUncaughtExceptionHandler est utilise pour traiter les exceptions asychrones
			throw new ExpTaskException(e);
		}
	}
}
