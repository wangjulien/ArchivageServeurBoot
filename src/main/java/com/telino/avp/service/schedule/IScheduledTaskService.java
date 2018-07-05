package com.telino.avp.service.schedule;

import com.telino.avp.exception.ExpTaskException;

/**
 * Interface definit les fonctions a excuter periodiquement
 * 
 * @author Jiliang.WANG
 *
 */
public interface IScheduledTaskService {

	/**
	 * tache programme de remplissation
	 * 
	 * @throws ExpTaskException
	 */
	public void scheduledFillingTask() throws ExpTaskException;

	/**
	 * tache programme de l'exploitation
	 * 
	 * @throws ExpTaskException
	 */
	public void scheduledExploitationTask() throws ExpTaskException;
}
