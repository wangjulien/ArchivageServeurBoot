package CdmsProg.nfz42013;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.telino.avp.ArchivageServeurBootApplication;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.journal.JournalEventService;

import CdmsApi.CdmsLogger;
import CdmsApi.MainObject;

//@Configurable(dependencyCheck = true)
public class checkutil extends MainObject {

	private JournalEventService journalEventService;

	final static String foncname = "checkutil";
	private Connection conn;
	private Connection connMirror;
	static final Logger logger = Logger.getLogger(CdmsProg.nfz42013.checkutil.class);

	public checkutil(HashMap<String, Object> pH) {
		// !!! Hack Code in order to get Spring controlled bean, because the
		// @Configurable and loadingTimeWeaving mechanism does not work
		journalEventService = (JournalEventService) ArchivageServeurBootApplication.SPRING_CONTEXT
				.getBean("journalEventService");

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
		if (CurrentObjectValues.get("mode").toString().equals("delete")) {
			try {
				printLog();
			} catch (SQLException | AvpExploitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}
		// vérification que le mail n'est pas déjà utilisé par ailleurs
		// if (CurrentObjectValues.get("$APPLI")!=null &&
		// !CurrentObjectValues.get("$APPLI").equals("cdms")) return true;
		String userid = CurrentObjectValues.get("userid") == null ? "" : CurrentObjectValues.get("userid").toString();
		String mailid = CurrentObjectValues.get("mailid") == null ? "" : CurrentObjectValues.get("mailid").toString();

		try {
			Statement st0 = conn.createStatement();
			int maxUsers = 5;
			ResultSet rs0 = st0.executeQuery("select maxusers from param where paramid = 1;");
			if (rs0.next()) {
				maxUsers = rs0.getInt(1);
			}
			rs0.close();
			rs0 = st0.executeQuery("select count(*) from util");
			if (rs0.next()) {
				if (rs0.getInt(1) + 1 >= maxUsers) {
					throwCdmsException("Votre licence est limitée à " + maxUsers + " utilisateurs");
					return false;
				}
			}

			rs0 = st0.executeQuery(
					"select userid from util where userid != '" + userid + "' and mailid ='" + mailid + "'");
			if (rs0.next()) {
				String user = rs0.getString(1);
				st0.close();
				throwCdmsException("Cette adresse e-mail est déjà attribuée à l'utilisateur " + user);

				return false;
			}

			// create mail if not exists
			rs0 = st0.executeQuery("select mailid from mail where mailid = '" + mailid + "'");
			if (rs0.next()) {

			} else {
				String[] mailparts = mailid.split("@");
				String libMail = mailparts[0].replace(".", " ");
				String request = "insert into mail (mailid,maillib) values ('" + mailid + "','" + libMail + "')";
				st0.execute(request);
				st0.close();
				if (connMirror != null && CurrentObjectValues.get("$DUPLICATE") != null) {
					System.out.println("creating mail on mirror");
					Statement st1 = connMirror.createStatement();
					st1.execute(request);
					st1.close();
				} else {
					System.out.println("mirror " + connMirror);
					System.out.println("duplicate " + CurrentObjectValues.get("$DUPLICATE"));
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		if (CurrentObjectValues.get("mode").toString().equals("create")) {

			try {
				Statement st0 = conn.createStatement();
				ResultSet rs0 = st0.executeQuery("select userid from login where userid = '" + userid + "'");
				if (rs0.next()) {
					st0.close();
					printLog();
					return true;
				}
				String request = "insert into login (userid) values ('" + userid + "')";
				System.out.println(request);
				st0.execute(request);

				st0.close();
				if (connMirror != null && CurrentObjectValues.get("$DUPLICATE") != null) {
					Statement st1 = connMirror.createStatement();
					st1.execute(request);
					st1.close();
				}
			} catch (SQLException | AvpExploitException e) {
				e.printStackTrace();
				return false;
			}

		}

		try {
			printLog();
		} catch (SQLException | AvpExploitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;

	}

	private void printLog() throws SQLException, AvpExploitException {
		String message = "";
		if (CurrentObjectValues.get("mode").toString().equals("create")) {
			message = "Création ";
		}
		if (CurrentObjectValues.get("mode").toString().equals("update")) {
			message = "Modification ";
		}
		message += " utilisateur: " + (String) CurrentObjectValues.get("userid");
		message += ", e-mail: " + (String) CurrentObjectValues.get("mailid");
		message += ", profil: " + (String) CurrentObjectValues.get("profid");
		message += ", organisation: " + (String) CurrentObjectValues.get("domnnom");

		Map<String, Object> inputToLog = new HashMap<>();
		inputToLog.put("origin", "ADELIS");
		inputToLog.put("operateur", "tomcat");
		inputToLog.put("version", "1");
		inputToLog.put("processus", "checkutil");
		inputToLog.put("action", "Mise à jour utilisateurs");
		inputToLog.put("logtype", "E");
		inputToLog.put("detail", message);
		journalEventService.log(inputToLog);

	}

}