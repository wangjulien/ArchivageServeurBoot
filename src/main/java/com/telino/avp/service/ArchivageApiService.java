package com.telino.avp.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.entity.archive.Communication;
import com.telino.avp.entity.archive.Restitution;
import com.telino.avp.entity.archive.RestitutionList;
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
				resultat.put("message", "Les deux mots de passe de l'application non initialises");
				return resultat;
			} else if (Objects.isNull(ServerProc.password1)) {
				resultat.put("codeRetour", "81");
				resultat.put("message", "Mot de passe 1 de l'application non initialise");
				return resultat;
			} else if (Objects.isNull(ServerProc.password2)) {
				resultat.put("codeRetour", "82");
				resultat.put("message", "Mot de passe 2 de l'application non initialise");
				return resultat;
			}
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
		// All action need Rollback of DB will raise an AvpExploitException
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
			journalArchiveService.checkLogArchive(UUID.fromString((String) input.get("logid")));
			break;

		case CHECK_LOG_EVENT:
			journalEventService.checkLogEvent(UUID.fromString((String) input.get("logid")));
			break;

		case CONTROL:
			documentService.control(input);
			break;

		case COMMUNICATION:
			comAndRestService.communication(input);
			break;

		case COMMUNICATION_VALIDATED:

			// First create the communication
			Communication communication = comAndRestService.communication(input);
			// add than validate it
			comAndRestService.validationCommunication(communication);
			break;

		case CREATE_LOG_CHECK:
			journalArchiveService.scellerJournal();
			break;

		case CREATE_LOG_EVENT:
			journalEventService.scellerJournal();
			break;

		case DELAY:
			documentService.delay(input, resultat);
			break;

		case DELETE:
			documentService.delete(input, resultat, isBgTask);
			break;

		case DELETE_DRAFT:
			draftService.deleteDraft(input);
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
			// Get the Attestation file for a log
			journalArchiveService.getAttestation(UUID.fromString((String) input.get("logid")), resultat);
			break;

		case GET_COMMUNICATION:
			comAndRestService.getCommunication(input, resultat);
			break;

		case GET_DRAFT_INFO:
			draftService.getDraftInfo(UUID.fromString((String) input.get("docid")), resultat);
			break;

		case GET_DOC:
			documentService.get(input, resultat);
			break;

		case GET_INFO:
			documentService.getInfo(input, resultat);
			break;

		case GET_LOG_FILE:
			// Get attestation file from LogEvent
			journalEventService.getLogFile(UUID.fromString((String) input.get("logid")), resultat);
			break;

		case GET_RESTITUTION:
			comAndRestService.getRestitution(input, resultat);
			break;

		case GET_RIGHTS:
			userProfileRightService.getRights((String) input.get("user"), resultat);
			break;

		case GET_USER_PROFILES:
			userProfileRightService.getUserReadProfiles((String) input.get("user"), resultat);
			break;

		case LIST:
			// recupere une liste de keywords with document meta donnee, limit a 2?
			documentService.getList(input, resultat);
			break;

		case LOG_EVENT:
			journalEventService.log(input);
			break;

		case LOG_ARCHIVE:
			journalArchiveService.log(input);
			break;

		case LOGICAL_DELETE:

			documentService.logicalDelete(input);

			break;

		case READ_DRAFT:
			draftService.readDraft(input, resultat);
			break;

		case REFUS_COMMUNICATION:
			comAndRestService.refusCommunication(UUID.fromString((String) input.get("communicationid")));
			break;

		case REFUS_DRAFT:
			draftService.refusDraft(input);
			break;

		case RESTITUTION:
			comAndRestService.restitute(input, resultat);
			break;

		case STORE:
			if (Objects.isNull(input.get("serviceverseur"))) {
				resultat.put("codeRetour", "9");
				resultat.put("message", "Le service verseur est obligatoire");
			} else {
				documentService.store(input, resultat);
			}
			break;

		case STORE_DRAFT:
			draftService.draftStore(input, resultat);
			break;

		case STORE_PASSWORD:
			userProfileRightService.storePassword(input, resultat);
			break;

		case UPDATE_DRAFT:
			draftService.updateDraft(input);
			break;

		case VALIDE_DRAFT:
			draftService.valideDraft(input);
			break;

		case VALIDATION_COMMUNICATION:
			Communication comToBeValided = comAndRestService.findByComId(UUID.fromString(""));
			comAndRestService.validationCommunication(comToBeValided);
			break;

		case VALIDATION_RESTITUTION:
			Restitution valideResitution = comAndRestService.validationRestitution(input, resultat);
			// Sinc the save above re-persist the documents restaured (delete), we need to
			// re-delete them from DB
			documentService.deleteMetaData(valideResitution.getRestitutionList().stream()
					.map(RestitutionList::getDocument).collect(Collectors.toList()));
			break;

		default:
			resultat.put("codeRetour", "90");
			resultat.put("message", "commande inconnue: " + commande);
			break;

		}
		return resultat;
	}

}
