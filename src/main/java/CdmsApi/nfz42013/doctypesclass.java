package CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class doctypesclass extends TypeJAV {

	public doctypesclass() {
		super();
		initialize();
	}

	public doctypesclass(Connection conn, boolean b) {
		super(conn, b);
		initialize();
	}

	protected class CLE {
		public Integer doctypeid;
		public final String F = "S005";
	}

	protected CLE doctypes_CLE = new CLE();

	protected class PKEY {
		public Integer doctypeid;
		public final String F = "S005";
	}

	protected PKEY doctypes_PKEY = new PKEY();

	protected class T {
		public Integer doctypeid;
		public String doctype_archivage;
		public String categorie;
		public String keywordslist;
		public Integer par_id;
		public final String F = "S005A030A030A900S005";
	}

	protected T doctypes_T = new T();

	public Integer getDoctypeid() {
		return (Integer) CurrentObjectValues.get("doctypeid");
	}

	public void setDoctypeid(Integer val) {
		setObjectValue(val, "doctypeid");
	}

	public String getDoctype_archivage() {
		return (String) CurrentObjectValues.get("doctype_archivage");
	}

	public void setDoctype_archivage(String val) {
		setObjectValue(val, "doctype_archivage");
	}

	public String getCategorie() {
		return (String) CurrentObjectValues.get("categorie");
	}

	public void setCategorie(String val) {
		setObjectValue(val, "categorie");
	}

	public String getKeywordslist() {
		return (String) CurrentObjectValues.get("keywordslist");
	}

	public void setKeywordslist(String val) {
		setObjectValue(val, "keywordslist");
	}

	public Integer getPar_id() {
		return (Integer) CurrentObjectValues.get("par_id");
	}

	public void setPar_id(Integer val) {
		setObjectValue(val, "par_id");
	}

	private void initialize() {
		CODOBJ = "doctypes";
		setObjectValue("doctypes", "codobj");
		CLE = doctypes_CLE;
		PKEY = doctypes_PKEY;
		T = doctypes_T;
		DUPLICATE = true;
	}
}