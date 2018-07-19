package CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class empreintesclass extends TypeJAV {

	public empreintesclass() {
		super();
		initialize();
	}

	public empreintesclass(Connection conn, boolean b) {
		super(conn, b);
		initialize();
	}

	protected class CLE {
		public String docid;
		public final String F = "A008";
	}

	protected CLE empreintes_CLE = new CLE();

	protected class T {
		public String docid;
		public String empreinte;
		public String empreinte_algo;
		public final String F = "A008A900A100";
	}

	protected T empreintes_T = new T();

	public String getDocid() {
		return (String) CurrentObjectValues.get("docid");
	}

	public void setDocid(String val) {
		setObjectValue(val, "docid");
	}

	public String getEmpreinte() {
		return (String) CurrentObjectValues.get("empreinte");
	}

	public void setEmpreinte(String val) {
		setObjectValue(val, "empreinte");
	}

	public String getEmpreinte_algo() {
		return (String) CurrentObjectValues.get("empreinte_algo");
	}

	public void setEmpreinte_algo(String val) {
		setObjectValue(val, "empreinte_algo");
	}

	private void initialize() {
		CODOBJ = "empreintes";
		setObjectValue("empreintes", "codobj");
		CLE = empreintes_CLE;
		T = empreintes_T;
	}
}