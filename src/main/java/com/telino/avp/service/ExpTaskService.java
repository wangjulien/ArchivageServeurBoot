package com.telino.avp.service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.storage.FsStorageService;

/**
 * Dispatcher et Executeur des tache d'exploitation en utilisant les modules de
 * API archivageserveur
 * 
 * @author Jiliang.WANG
 *
 */
@Service
public class ExpTaskService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpTaskService.class);
	
	@Autowired
	private DocumentService documentService;

	@Autowired
	private JournalArchiveService journalArchiveService;
	
	@Autowired
	private FsStorageService fsStorageService;	
	
	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private LogArchiveDao logArchiveDao;


	/**
	 * Dispatcher de tâches, une contrôle de l'intégralité est lancé après
	 * restauration de problème
	 * 
	 * @param expTaskCommand
	 * @param input
	 * @throws AvpExploitException
	 */
	public void dispatchExpTask(final Commande expTaskCommand, Map<String, Object> input) throws AvpExploitException {

		UUID docId = null;
		if (Objects.isNull(input.get("docid"))) {
			String msg = "Appel http ne contient pas docid " + input;
			LOGGER.error(msg);
			throw new AvpExploitException("1", new Throwable(msg));
		} else
			docId = UUID.fromString(input.get("docid").toString());

		switch (expTaskCommand) {

		case EXP_TASK_RELAUNCH_FILE_ENTIRETY_CHECK:
			break;

		case EXP_TASK_CHECK_RESTORE_MASTER_HASH:
			restoreHasFromLogArchive(docId, false);
			break;

		case EXP_TASK_CHECK_RESTORE_MIRROR_HASH:
			restoreHasFromLogArchive(docId, true);
			break;

		case EXP_TASK_CHECK_RESTORE_MASTER_METADATA:
			// isMirror = false, restore the metadonne depuis mirror
			documentDao.restoreTheMetaDataFrom(docId, false);
			break;

		case EXP_TASK_CHECK_RESTORE_MIRROR_METADATA:
			// isMirror = true, restore the metadonne depuis master
			documentDao.restoreTheMetaDataFrom(docId, true);
			break;

		case EXP_TASK_RESTORE_MASTER_FILE:

			fsStorageService.restoreMasterFileFromMirror(docId);
			break;

		case EXP_TASK_RESTORE_MIRROR_FILE:

			fsStorageService.restoreMirrorFileFromMaster(docId);
			break;

		case EXP_TASK_NEED_HUMAN_INTERVENTION:
		default:
			String msg = "Type de tâche d'exploitation erroné : " + expTaskCommand;
			LOGGER.error(msg);
			throw new AvpExploitException("1", new Throwable(msg));
		}

		LOGGER.info("Rappel Controle de l'integralite : " + input);
		documentService.control(input);
	}

	private void restoreHasFromLogArchive(final UUID docId, final boolean isMirror) throws AvpExploitException {
		// Controle l'integralite du journalJournalArchive logArchive;
		journalArchiveService.verifySellementLogForDocId(docId, isMirror);

		// Si oui, prend Hash de log_archive et remettre dans document
		documentDao.restoreTheHash(docId, logArchiveDao.findHashForDocId(docId, isMirror), isMirror);

		// Sinon, une erreur AVPExploitException est leve
	}
}
