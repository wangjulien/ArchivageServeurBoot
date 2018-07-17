package CdmsProg.nfz42013;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.journal.JournalEventService;

import CdmsApi.CdmsLogger;
import CdmsApi.MainObject;

@Configurable(dependencyCheck = true)
public class checkparam extends MainObject {
	
	@Autowired
	private JournalEventService journalEventService;
	
	final static String foncname = "checkutil";
	private Connection conn;
	private Connection connMirror;
	private boolean mirror;
	static final Logger logger = Logger.getLogger(CdmsProg.nfz42013.checkparam.class);
	
	
	public checkparam(HashMap<String, Object> pH) {
		CurrentObjectValues = pH;
		conn = (Connection) CurrentObjectValues.get("Connection");
		connMirror = (Connection) CurrentObjectValues.get("connMirror");
		mirror = (boolean) CurrentObjectValues.get("mirror");
		if (this.getObjectValue("TRACE")!=null) {
			if ((Boolean) this.getObjectValue("TRACE")) this.setTraceOn();
		}
	}
	
	
	public boolean Exec() throws CdmsException {

		CdmsLogger.log(logger, CurrentObjectValues, (String) CurrentObjectValues.get("$APPLI"));
//		if (CurrentObjectValues.get("mode").toString().equals("delete")) return true;
//		if (CurrentObjectValues.get("mode").toString().equals("create")) return true;
		if (CurrentObjectValues.get("mode").toString().equals("update")) return true;
		
		//get cryptage
		/*
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select cryptage from param where paramid=1");
		if (rs.next()) {
			if (rs.getObject("cryptage").equals(CurrentObjectValues.get("cryptage"))) {
				rs.close();
				st.close();
				return true;
			}
		}
		rs.close();
		rs = st.executeQuery("select count(*) from document");
		if (rs.next()) {
			if (rs.getInt(1)>0) {
				rs.close();
				st.close();
				throwCdmsException("Modification du cryptage impossible, car il y a déjà des archives");
				return false;
			}
		}
		
		rs.close();
		st.close();
		 */
		try {
			Map<String,Object> inputToLog = new HashMap<>();
			inputToLog.put("origin", "ADELIS");
			inputToLog.put("operateur", "tomcat");
			inputToLog.put("version", "1");
			inputToLog.put("processus", "Paramétrage de l'application");
			inputToLog.put("action", "Mise à jour paramètres");
			inputToLog.put("logtype", "E");
			inputToLog.put("detail","");
			journalEventService.log(inputToLog);
			return true;
		} catch (AvpExploitException e) {
			e.printStackTrace();
			return false;
		}

	}
	

}