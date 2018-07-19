package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class depotsclass extends TypeJAV {

	public depotsclass() {
		super();
		initialize();
	}

	public depotsclass(Connection conn, boolean b) {
		super(conn, b);
		initialize();
	}

	protected class CLE {
		public String iddepot;
		public final String F = "A008";
	}

	protected CLE depots_CLE = new CLE();

	protected class PKEY {
		public String iddepot;
		public final String F = "A008";
	}

	protected PKEY depots_PKEY = new PKEY();

	protected class T {
		public String iddepot;
		public Date horodatage;
		public String demandeur;
		public String status;
		public String message;
		public final String F = "A008T015A040A100A200";
	}

	protected T depots_T = new T();

	public String getIddepot() {
		return (String) CurrentObjectValues.get("iddepot");
	}

	public void setIddepot(String val) {
		setObjectValue(val, "iddepot");
	}

	public Date getHorodatage() {
		return (Date) CurrentObjectValues.get("horodatage");
	}

	public void setHorodatage(Date val) {
		setObjectValue(val, "horodatage");
	}

	public String getDemandeur() {
		return (String) CurrentObjectValues.get("demandeur");
	}

	public void setDemandeur(String val) {
		setObjectValue(val, "demandeur");
	}

	public String getStatus() {
		return (String) CurrentObjectValues.get("status");
	}

	public void setStatus(String val) {
		setObjectValue(val, "status");
	}

	public String getMessage() {
		return (String) CurrentObjectValues.get("message");
	}

	public void setMessage(String val) {
		setObjectValue(val, "message");
	}

	private void initialize() {
		CODOBJ = "depots";
		setObjectValue("depots", "codobj");
		CLE = depots_CLE;
		PKEY = depots_PKEY;
		T = depots_T;
		DUPLICATE = true;
	}
}