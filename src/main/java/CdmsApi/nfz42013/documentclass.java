package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class documentclass extends TypeJAV {

	public documentclass() {
		super();
		initialize();
	}

	public documentclass(Connection conn, boolean b) {
		super(conn, b);
		initialize();
	}

	protected class CLE {
		public String docid;
		public final String F = "A008";
	}

	protected CLE document_CLE = new CLE();

	protected class PKEY {
		public String docid;
		public final String F = "A008";
	}

	protected PKEY document_PKEY = new PKEY();

	protected class T {
		public String docid;
		public String title;
		public Date date;
		public String content;
		public String archiver_id;
		public String content_type;
		public Integer content_length;
		public String keywords;
		public String doctype;
		public Date archive_date;
		public String application;
		public Integer lot;
		public String idsource;
		public String categorie;
		public Date archive_end;
		public String author;
		public String mailowner;
		public String domaineowner;
		public String archiver_mail;
		public Integer par_id;
		public String elasticid;
		public String domnnom;
		public String conteneur;
		public Boolean cryptage;
		public String cryptage_algo;
		public String organisationversante;
		public String organisationverseuse;
		public String md5;
		public Boolean archive_statut;
		public String num_archive;
		public final String F = "A008A060T010A900A020A100E008A900A030T016A020E008A040A030T016A060A100A030A100S005A100A020A030B005A100A040A040A900B005A020";
	}

	protected T document_T = new T();

	public String getDocid() {
		return (String) CurrentObjectValues.get("docid");
	}

	public void setDocid(String val) {
		setObjectValue(val, "docid");
	}

	public String getTitle() {
		return (String) CurrentObjectValues.get("title");
	}

	public void setTitle(String val) {
		setObjectValue(val, "title");
	}

	public Date getDate() {
		return (Date) CurrentObjectValues.get("date");
	}

	public void setDate(Date val) {
		setObjectValue(val, "date");
	}

	public String getContent() {
		return (String) CurrentObjectValues.get("content");
	}

	public void setContent(String val) {
		setObjectValue(val, "content");
	}

	public String getArchiver_id() {
		return (String) CurrentObjectValues.get("archiver_id");
	}

	public void setArchiver_id(String val) {
		setObjectValue(val, "archiver_id");
	}

	public String getContent_type() {
		return (String) CurrentObjectValues.get("content_type");
	}

	public void setContent_type(String val) {
		setObjectValue(val, "content_type");
	}

	public Integer getContent_length() {
		return (Integer) CurrentObjectValues.get("content_length");
	}

	public void setContent_length(Integer val) {
		setObjectValue(val, "content_length");
	}

	public String getKeywords() {
		return (String) CurrentObjectValues.get("keywords");
	}

	public void setKeywords(String val) {
		setObjectValue(val, "keywords");
	}

	public String getDoctype() {
		return (String) CurrentObjectValues.get("doctype");
	}

	public void setDoctype(String val) {
		setObjectValue(val, "doctype");
	}

	public Date getArchive_date() {
		return (Date) CurrentObjectValues.get("archive_date");
	}

	public void setArchive_date(Date val) {
		setObjectValue(val, "archive_date");
	}

	public String getApplication() {
		return (String) CurrentObjectValues.get("application");
	}

	public void setApplication(String val) {
		setObjectValue(val, "application");
	}

	public Integer getLot() {
		return (Integer) CurrentObjectValues.get("lot");
	}

	public void setLot(Integer val) {
		setObjectValue(val, "lot");
	}

	public String getIdsource() {
		return (String) CurrentObjectValues.get("idsource");
	}

	public void setIdsource(String val) {
		setObjectValue(val, "idsource");
	}

	public String getCategorie() {
		return (String) CurrentObjectValues.get("categorie");
	}

	public void setCategorie(String val) {
		setObjectValue(val, "categorie");
	}

	public Date getArchive_end() {
		return (Date) CurrentObjectValues.get("archive_end");
	}

	public void setArchive_end(Date val) {
		setObjectValue(val, "archive_end");
	}

	public String getAuthor() {
		return (String) CurrentObjectValues.get("author");
	}

	public void setAuthor(String val) {
		setObjectValue(val, "author");
	}

	public String getMailowner() {
		return (String) CurrentObjectValues.get("mailowner");
	}

	public void setMailowner(String val) {
		setObjectValue(val, "mailowner");
	}

	public String getDomaineowner() {
		return (String) CurrentObjectValues.get("domaineowner");
	}

	public void setDomaineowner(String val) {
		setObjectValue(val, "domaineowner");
	}

	public String getArchiver_mail() {
		return (String) CurrentObjectValues.get("archiver_mail");
	}

	public void setArchiver_mail(String val) {
		setObjectValue(val, "archiver_mail");
	}

	public Integer getPar_id() {
		return (Integer) CurrentObjectValues.get("par_id");
	}

	public void setPar_id(Integer val) {
		setObjectValue(val, "par_id");
	}

	public String getElasticid() {
		return (String) CurrentObjectValues.get("elasticid");
	}

	public void setElasticid(String val) {
		setObjectValue(val, "elasticid");
	}

	public String getDomnnom() {
		return (String) CurrentObjectValues.get("domnnom");
	}

	public void setDomnnom(String val) {
		setObjectValue(val, "domnnom");
	}

	public String getConteneur() {
		return (String) CurrentObjectValues.get("conteneur");
	}

	public void setConteneur(String val) {
		setObjectValue(val, "conteneur");
	}

	public Boolean getCryptage() {
		return (Boolean) CurrentObjectValues.get("cryptage");
	}

	public void setCryptage(Boolean val) {
		setObjectValue(val, "cryptage");
	}

	public String getCryptage_algo() {
		return (String) CurrentObjectValues.get("cryptage_algo");
	}

	public void setCryptage_algo(String val) {
		setObjectValue(val, "cryptage_algo");
	}

	public String getOrganisationversante() {
		return (String) CurrentObjectValues.get("organisationversante");
	}

	public void setOrganisationversante(String val) {
		setObjectValue(val, "organisationversante");
	}

	public String getOrganisationverseuse() {
		return (String) CurrentObjectValues.get("organisationverseuse");
	}

	public void setOrganisationverseuse(String val) {
		setObjectValue(val, "organisationverseuse");
	}

	public String getMd5() {
		return (String) CurrentObjectValues.get("md5");
	}

	public void setMd5(String val) {
		setObjectValue(val, "md5");
	}

	public Boolean getArchive_statut() {
		return (Boolean) CurrentObjectValues.get("archive_statut");
	}

	public void setArchive_statut(Boolean val) {
		setObjectValue(val, "archive_statut");
	}

	public String getNum_archive() {
		return (String) CurrentObjectValues.get("num_archive");
	}

	public void setNum_archive(String val) {
		setObjectValue(val, "num_archive");
	}

	private void initialize() {
		CODOBJ = "document";
		setObjectValue("document", "codobj");
		CLE = document_CLE;
		PKEY = document_PKEY;
		T = document_T;
	}
}