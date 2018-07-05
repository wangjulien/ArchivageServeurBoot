package com.telino.avp.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.entity.archive.Communication;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.service.archivage.ComAndRestService;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.service.archivage.DraftService;
import com.telino.avp.service.archivage.UserProfileRightService;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.journal.JournalEventService;
import com.telino.avp.tools.ServerProc;

@Service
public class ArchivageApiService {

	@Autowired
	private UserProfileRightService userProfileRightService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DraftService draftService;

	@Autowired
	private ComAndRestService comAndRestService;

	@Autowired
	private JournalArchiveService journalArchiveService;

	@Autowired
	private JournalEventService journalEventService;

	@Autowired
	private ExpTaskService expTaskExecutor;

	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> execApi(final Map<String, Object> input) throws AvpExploitException {

		// Return message in a Map
		Map<String, Object> resultat = new HashMap<>();

		// Get the command in Enum form
		if (Objects.isNull(input.get("command"))) {
			resultat.put("codeRetour", "9");
			resultat.put("message", "commande non précisée");
			return resultat;
		}
		Commande commande = Commande.getEnum(input.get("command").toString());

		// Check passwords stored
		if (Commande.STORE_PASSWORD != commande) {
			if (Objects.isNull(ServerProc.password1) && Objects.isNull(ServerProc.password2)) {
				resultat.put("codeRetour", "80");
				resultat.put("message", "");
			} else if (Objects.isNull(ServerProc.password1)) {
				resultat.put("codeRetour", "81");
				resultat.put("message", "");
			} else if (Objects.isNull(ServerProc.password2)) {
				resultat.put("codeRetour", "82");
				resultat.put("message", "");
			}
			return resultat;
		}

		// Each request contains login information for the user
		// Check in the DB for login information
		boolean connexionOK = false;
		if (Objects.nonNull(input.get("user"))) {
			if (Objects.nonNull(input.get("password")) || Objects.nonNull(input.get("encryptedPassword"))) {
				if (Objects.nonNull(userProfileRightService.getToken(input.get("user").toString(),
						(String) input.get("password"), (String) input.get("encryptedPassword")))) {
					connexionOK = true;
				}
			}
		}

		boolean isBgTask = false;
		// Background tasks are created by system, no authentication needed
		if (Objects.nonNull(input.get("bgTask")) && input.get("bgTask").equals(true)
				&& (Arrays.asList(Commande.DELETE, Commande.LOGICAL_DELETE, Commande.CREATE_LOG_CHECK,
						Commande.CREATE_LOG_EVENT, Commande.CHECK_FILES, Commande.CONTROL,
						Commande.EXP_TASK_NEED_HUMAN_INTERVENTION, Commande.EXP_TASK_CHECK_RESTORE_MASTER_HASH,
						Commande.EXP_TASK_CHECK_RESTORE_MIRROR_HASH, Commande.EXP_TASK_CHECK_RESTORE_MASTER_METADATA,
						Commande.EXP_TASK_CHECK_RESTORE_MIRROR_METADATA, Commande.EXP_TASK_RELAUNCH_FILE_ENTIRETY_CHECK,
						Commande.EXP_TASK_RESTORE_MASTER_FILE, Commande.EXP_TASK_RESTORE_MIRROR_FILE)
						.contains(commande))) {
			isBgTask = true;
			connexionOK = true;
		}

		// LogEvent?
		if (Commande.LOG_EVENT == commande)
			connexionOK = true;

		// Check if command is authorized
		if (!connexionOK) {
			resultat.put("codeRetour", "9");
			resultat.put("message", "informations de connexion erronées");
			return resultat;
		}

		// Check if the application and processus is validated
		if (!userProfileRightService.checkApplication(input.get("application"), commande, input.get("processus"),
				resultat)) {
			return resultat;
		}

		// No need to add OK for each method individually if all goes well
		// Otherwise, return code will be overwritten
		resultat.put("codeRetour", ReturnCode.OK.toString());
		resultat.put("message", "");

		//
		// Dispatch the commands
		//

		switch (commande) {

		case CHECK_FILES:
			documentService.checkfiles(input, resultat);
			break;

		case CHECK_LOG_CHECK:
			journalArchiveService.checklogcheck(UUID.fromString((String) input.get("logid")));
			break;

		case CHECK_LOG_EVENT:
			journalEventService.checklogevent(UUID.fromString((String) input.get("logid")));
			break;

		case CONTROL:
			documentService.control(input);
			break;

		case COMMUNICATION:
			comAndRestService.communication(input);
			break;

		case COMMUNICATION_VALIDATED:
			try {
				// First create the communication
				Communication communication = comAndRestService.communication(input);
				// add than validate it
				comAndRestService.validationCommunication(communication);
			} catch (PersistenceException e) {
				throw new AvpExploitException("602", e, "Validation de communication d'archives", null, null, null);
			}
			break;

		case CREATE_LOG_CHECK:
			journalArchiveService.scellerJournal();
			break;

		case CREATE_LOG_EVENT:
			journalEventService.scellerJournal();
			break;

		case DELAY:
			try {
				documentService.delay(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("617", e, "Modification de la date d'expiration du document", null, null,
						null);
			}
			break;

		case DELETE:
			documentService.delete(input, resultat, isBgTask);
			break;

		case DELETE_DRAFT:
			try {
				draftService.deleteDraft(input);

			} catch (PersistenceException e) {
				throw new AvpExploitException("616", e, "Suppression d'un draft", null, null, null);
			}
			break;

		case EXP_TASK_CHECK_RESTORE_MASTER_HASH:
		case EXP_TASK_CHECK_RESTORE_MASTER_METADATA:
		case EXP_TASK_CHECK_RESTORE_MIRROR_HASH:
		case EXP_TASK_CHECK_RESTORE_MIRROR_METADATA:
		case EXP_TASK_NEED_HUMAN_INTERVENTION:
		case EXP_TASK_RELAUNCH_FILE_ENTIRETY_CHECK:
		case EXP_TASK_RESTORE_MASTER_FILE:
		case EXP_TASK_RESTORE_MIRROR_FILE:
			// Si la commande est une tache d'exploitation, lancer la par un Executeur
			expTaskExecutor.dispatchExpTask(commande, input);
			// return the taskid
			resultat.put("taskid", input.get("taskid"));

			break;

		case GET_ATTESTATION:
			try {
				// Get the Attestation file for a log
				journalArchiveService.getAttestation(UUID.fromString((String) input.get("logid")), resultat);
			} catch (Exception e) {
				throw new AvpExploitException("612", e, "Récupération d'une attestation", null, null,
						input.get("logid").toString());
			}
			break;

		case GET_COMMUNICATION:
			try {
				comAndRestService.getCommunication(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("601", e, "Communication d'archives", null, null, null);
			}
			break;

		case GET_DRAFT_INFO:
			try {
				draftService.getDraftInfo(UUID.fromString((String) input.get("docid")), resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("614", e, "Récupération des informations d'un draft", null, null, null);
			}
			break;

		case GET_DOC:
			try {
				documentService.get(input, resultat);
			} catch (Exception e) {
				throw new AvpExploitException("613", e, "Récupération des données d'une archive", null,
						(String) input.get("docid"), null);
			}
			break;

		case GET_INFO:
			try {
				documentService.getInfo(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("618", e, "Récupération des informations d'une archive", null, null,
						null);
			}
			break;

		case GET_LOG_FILE:
			try {
				// Get attestation file from LogEvent
				journalEventService.getLogFile(UUID.fromString((String) input.get("logid")), resultat);
			} catch (Exception e) {
				throw new AvpExploitException("612", e, "Récupération d'une attestation", null, null,
						(String) input.get("logid"));
			}
			break;

		case GET_RESTITUTION:
			try {
				comAndRestService.getRestitution(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("601", e, "Restitution d'archives", null, null, null);
			}
			break;

		case GET_RIGHTS:
			try {
				userProfileRightService.getRights((String) input.get("user"), resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("619", e, "Récupération des droits de l'utilisateur", null, null, null);
			}
			break;

		case GET_USER_PROFILES:
			try {
				userProfileRightService.getUserReadProfiles((String) input.get("user"), resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("619", e, "Récupération des profils d'archivages de l'utilisateur", null,
						null, null);
			}
			break;

		case LIST:
			try {
				// recupere une liste de keywords with document meta donnee, limit a 2?
				documentService.getList(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("619", e, "Récupération de la liste des archives", null, null, null);
			}
			break;

		case LOG_EVENT:
			Map<String, Object> inputToLogEvent = new HashMap<>();
			inputToLogEvent.put("origin", input.get("origin"));
			inputToLogEvent.put("operateur", input.get("operateur"));
			inputToLogEvent.put("version", input.get("versionprocessus"));
			inputToLogEvent.put("processus", input.get("processus"));
			inputToLogEvent.put("action", input.get("action"));
			inputToLogEvent.put("logtype", "E");
			inputToLogEvent.put("detail", input.get("detail"));
			journalEventService.log(inputToLogEvent);
			break;

		case LOG_ARCHIVE:
			Map<String, Object> inputToLogArchive = new HashMap<>();
			inputToLogArchive.put("operation", input.get("operation"));
			inputToLogArchive.put("docid", input.get("docid"));
			inputToLogArchive.put("userid", input.get("user"));
			inputToLogArchive.put("mailid", input.get("mailid"));
			inputToLogArchive.put("docsname", input.get("docsname"));
			inputToLogArchive.put("logtype", input.get("logtype"));
			journalArchiveService.log(inputToLogArchive);
			break;

		case LOGICAL_DELETE:

			documentService.logicalDelete(input);

			break;

		case READ_DRAFT:
			try {
				draftService.readDraft(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("614", e, "Récupération de la visualisation d'un draft", null, null,
						null);
			}
			break;

		case REFUS_COMMUNICATION:
			try {
				comAndRestService.refusCommunication(UUID.fromString((String) input.get("communicationid")));
			} catch (PersistenceException e) {
				throw new AvpExploitException("602", e, "Validation d'une communication d'archives", null, null, null);
			}
			break;

		case REFUS_DRAFT:
			try {
				draftService.refusDraft(input);
			} catch (PersistenceException e) {
				throw new AvpExploitException("602", e, "Refuse draft", null, null, null);
			}
			break;

		case RESTITUTION:
			try {
				comAndRestService.restitute(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("601", e, "Restitution d'archives", null, null, null);
			}
			break;

		case STORE:
			if (Objects.isNull(input.get("serviceverseur"))) {
				resultat.put("codeRetour", "9");
				resultat.put("message", "Le service verseur est obligatoire");
				return resultat;
			}
			documentService.store(input, resultat);
			break;

		case STORE_DRAFT:
			try {
				draftService.draftStore(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("615", e, "Archivage d'un draft", null, null, null);
			}
			break;

		case STORE_PASSWORD:
			userProfileRightService.storePassword(input, resultat);
			break;

		case UPDATE_DRAFT:
			try {
				draftService.updateDraft(input);
			} catch (Exception e) {
				resultat.put("codeRetour", "99");
				resultat.put("message", e.getMessage());
			}
			break;

		case VALIDE_DRAFT:
			try {
				draftService.valideDraft(input);

			} catch (Exception e) {
				resultat.put("codeRetour", "99");
				resultat.put("message", e.getMessage());
			}

			break;

		case VALIDATION_COMMUNICATION:
			try {
				Communication communication = comAndRestService.findByComId(UUID.fromString(""));
				comAndRestService.validationCommunication(communication);

			} catch (PersistenceException e) {
				throw new AvpExploitException("602", e, "Validation d'une communication d'archives", null, null, null);
			}

			break;

		case VALIDATION_RESTITUTION:
			try {
				comAndRestService.validationRestitution(input, resultat);
			} catch (PersistenceException e) {
				throw new AvpExploitException("601", e, "Validation d'une restitution d'archives", null, null, null);
			}
			break;

		default:
			resultat.put("codeRetour", "90");
			resultat.put("message", "commande inconnue: " + commande);
			break;

		}

		return resultat;
	}

}
