package com.telino.avp.service.schedule;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.auxil.ExpComment;
import com.telino.avp.entity.auxil.ExpTask;
import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.entity.context.User;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskState;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskType;
import com.telino.avp.protocol.DbEntityProtocol.LogEventDetail;

/**
 * Builder de l'objet de tache d'exploitation
 * 
 * @author Jiliang.WANG
 *
 */
@Component
public class ExpTaskBuilder {

	@Autowired
	private UserDao userDao;

	private static final String DEFAULT_USER = "system";

	/**
	 * Creation d'un tache d'exploitation pour une archive qui ont plusieur
	 * log_event
	 * 
	 * @param logEvents
	 * @return
	 */
	public ExpTask checkAndBuildTask(List<LogEvent> logEvents) {

		// Pour une meme archive, regrouper les log_event par leur causes
		Map<String, List<LogEvent>> logEventsByCause = logEvents.stream()
				.collect(Collectors.groupingBy(LogEvent::getDetail));

		User system = userDao.findByUserId(DEFAULT_USER);

		if (logEventsByCause.size() > 1) {

			// Une archive a plusieur type d'erreur
			// Lever une tache d'exploitation par humain
			ExpComment com = new ExpComment();
			com.setComDate(ZonedDateTime.now());
			com.setUser(system);
			com.setComment(logEvents.stream().map(LogEvent::getDetail).collect(Collectors.joining("; ")));

			ExpTask task = new ExpTask();
			task.setHorodatage(ZonedDateTime.now());
			task.setTaskType(ExpTaskType.NEED_HUMAN_INTERVENTION);
			task.setDocument(logEvents.get(0).getArchive());
			task.setState(ExpTaskState.I);
			task.setUser(system);
			task.addComment(com);
			task.setJournal(logEvents.get(0));

			return task;
		} else {
			// Si ils sont la meme casue
			return buildTask(logEvents.get(0));
		}
	}

	/**
	 * Creation d'une tache d'exploit pour une archive qui on une erreur dans
	 * log_event
	 * 
	 * @param logEvent
	 * @return
	 */
	public ExpTask buildTask(final LogEvent logEvent) {

		User system = userDao.findByUserId(DEFAULT_USER);

		ExpComment com = new ExpComment();
		com.setComDate(ZonedDateTime.now());
		com.setUser(system);
		com.setComment(logEvent.getDetail());

		ExpTask task = new ExpTask();
		task.setHorodatage(ZonedDateTime.now());
		task.setDocument(logEvent.getArchive());
		task.setState(ExpTaskState.I);
		task.setUser(system);
		task.addComment(com);
		task.setJournal(logEvent);

		// Deduire le type de tache d'exploitation
		try {
			switch (LogEventDetail.valueOf(logEvent.getDetail())) {
			case HASH_NOT_MATCH_ERROR_IN_MASTER:
				task.setTaskType(ExpTaskType.CHECK_RESTORE_MASTER_HASH);
				break;
			case HASH_NOT_MATCH_ERROR_IN_MIRROR:
				task.setTaskType(ExpTaskType.CHECK_RESTORE_MIRROR_HASH);
				break;
			case NOT_FOUND_ERROR_IN_MASTER:
				task.setTaskType(ExpTaskType.RESTORE_MASTER_FILE);
				break;
			case NOT_FOUND_ERROR_IN_MIRROR:
				task.setTaskType(ExpTaskType.RESTORE_MIRROR_FILE);
				break;
			case DECRYPT_ERROR_IN_MASTER:
			case DECRYPT_ERROR_IN_MIRROR:
			case SHA_HASH_ERROR_IN_MASTER:
			case SHA_HASH_ERROR_IN_MIRROR:
				task.setTaskType(ExpTaskType.RELAUNCH_FILE_ENTIRETY_CHECK);
				break;
			case ENTIRETY_ERROR_IN_MASTER:
				task.setTaskType(ExpTaskType.CHECK_RESTORE_MASTER_METADATA);
				break;
			case ENTIRETY_ERROR_IN_MIRROR:
				task.setTaskType(ExpTaskType.CHECK_RESTORE_MIRROR_METADATA);
				break;
			default:
				task.setTaskType(ExpTaskType.NEED_HUMAN_INTERVENTION);
				break;
			}
		} catch (IllegalArgumentException e) {
			// Type error not known
			task.setTaskType(ExpTaskType.NEED_HUMAN_INTERVENTION);
		}
		
		return task;
	}
}
