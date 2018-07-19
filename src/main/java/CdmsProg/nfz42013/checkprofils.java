package CdmsProg.nfz42013;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.telino.avp.ArchivageServeurBootApplication;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.journal.JournalArchiveService;
import com.telino.avp.service.storage.AbstractStorageService;
import com.telino.avp.service.storage.FsStorageService;
import com.telino.avp.tools.FillPdfForm;

import CdmsApi.CdmsLogger;
import CdmsApi.MainObject;

//@Configurable(dependencyCheck = true)
public class checkprofils extends MainObject {

	private JournalArchiveService journalArchiveService;

	private AbstractStorageService storageService;

	final static String foncname = "checkprofil";
	private Connection conn;
	private Connection connMirror;
	private boolean alerte = false;
	static final Logger logger = Logger.getLogger(CdmsProg.nfz42013.checkprofils.class);

	public checkprofils(HashMap<String, Object> pH) {
		// !!! Hack Code in order to get Spring controlled bean, because the
		// @Configurable and loadingTimeWeaving mechanism does not work
		storageService = (FsStorageService) ArchivageServeurBootApplication.SPRING_CONTEXT.getBean("fsStorageService");
		journalArchiveService = (JournalArchiveService) ArchivageServeurBootApplication.SPRING_CONTEXT
				.getBean("journalArchiveService");

		CurrentObjectValues = pH;
		conn = (Connection) CurrentObjectValues.get("Connection");
		connMirror = (Connection) CurrentObjectValues.get("connMirror");
		if (this.getObjectValue("TRACE") != null) {
			if ((Boolean) this.getObjectValue("TRACE"))
				this.setTraceOn();
		}
	}

	public boolean Exec() throws CdmsException {
		CdmsLogger.log(logger, CurrentObjectValues, (String) CurrentObjectValues.get("$APPLI"));
		// mise à jour de la log d'archivage
		try {
			PreparedStatement read = conn.prepareStatement(
					"select ar_profile from profils where par_id = " + CurrentObjectValues.get("par_id"));
			;
			ResultSet rs = read.executeQuery();
			String profil = "";
			if (rs.next()) {
				profil = rs.getString(1);
			}

			rs.close();

			String mode = "modification";
			if (CurrentObjectValues.get("mode").equals("create"))
				mode = "création";
			else if (CurrentObjectValues.get("mode").equals("delete"))
				mode = "Suppression";

			String user = CurrentObjectValues.get("$USERID") + "";
			String mailid = CurrentObjectValues.get("$MAILID") + "";

			String action = " valeurs:  conservation: " + CurrentObjectValues.get("par_conservation");

			String operation = "Attestation de " + mode + " du profil d'archivage - "
					+ CurrentObjectValues.get("ar_profile") + " -";
			byte[] content = FillPdfForm.buildAttestation(operation, action);
			Document attestation = new Document();
			attestation.setArchiveDate(ZonedDateTime.now());
			attestation.setContent(content);
			int contentLength = content.length;
			attestation.setContentLength(contentLength);
			attestation.setDate(ZonedDateTime.now());
			attestation.setTitle(action);
			attestation.setContentType("application/pdf");
			attestation.setStatut(2);
			attestation.setDepot(null);
			// A default profile "Document" with par_id = 1 should exist.
			// Or profileDao can be used to retrieve an object Profile
			Profile docProfile = new Profile();
			docProfile.setParId(1);
			attestation.setProfile(docProfile);
			if (!storageService.archive(attestation)) {
				try {
					throw new Exception(
							"Impossible d'archiver l'attestation de " + mode + " du profil d'archivage " + profil);
				} catch (Exception e) {
					throw new AvpExploitException("520", e,
							"Archivage des attestations de " + mode + " de profil d'archivage", null, null, null);
				}
			}

			Map<String, Object> inputToLog = new HashMap<>();
			inputToLog.put("operation", action);
			inputToLog.put("userid", user);
			inputToLog.put("mailid", mailid);
			inputToLog.put("logtype", "P");
			inputToLog.put("attestationid", "" + attestation.getDocId());
			journalArchiveService.log(inputToLog);

			return true;
		} catch (SQLException | AvpExploitException e) {
			e.printStackTrace();
			return false;
		}

	}
}