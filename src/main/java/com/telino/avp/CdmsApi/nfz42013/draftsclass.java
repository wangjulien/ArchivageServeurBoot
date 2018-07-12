package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class draftsclass extends TypeJAV {

    public draftsclass () {
        super ();
        initialize ();
    }

    public draftsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer docid;
        public String doctype;
        public String categorie;
        public String keywords;
        public String content;
        public Integer content_length;
        public String content_type;
        public String domaineowner;
        public String organisationversante;
        public Date docsdate;
        public String description;
        public String title;
        public String silo;
        public String mailowner;
        public Boolean transmis;
        public String statut;
        public String motif;
        public String userid;
        public final String F = "S008A030A030A900A900E008A100A030A040T015A200A060A020A100B005A020A200A012";
    }
    protected T DRAFTS_T = new T();

    protected class CLE {
        public Integer docid;
        public final String F = "S008";
    }
    protected CLE DRAFTS_CLE = new CLE();

    protected class PKEY {
        public Integer docid;
        public final String F = "S008";
    }
    protected PKEY DRAFTS_PKEY = new PKEY();

    public  Integer getDocid() {
        return (Integer) CurrentObjectValues.get("docid");
    }

    public void setDocid(Integer val) {
        setObjectValue (val, "docid");
    }

    public  String getDoctype() {
        return (String) CurrentObjectValues.get("doctype");
    }

    public void setDoctype(String val) {
        setObjectValue (val, "doctype");
    }

    public  String getCategorie() {
        return (String) CurrentObjectValues.get("categorie");
    }

    public void setCategorie(String val) {
        setObjectValue (val, "categorie");
    }

    public  String getKeywords() {
        return (String) CurrentObjectValues.get("keywords");
    }

    public void setKeywords(String val) {
        setObjectValue (val, "keywords");
    }

    public  String getContent() {
        return (String) CurrentObjectValues.get("content");
    }

    public void setContent(String val) {
        setObjectValue (val, "content");
    }

    public  Integer getContent_length() {
        return (Integer) CurrentObjectValues.get("content_length");
    }

    public void setContent_length(Integer val) {
        setObjectValue (val, "content_length");
    }

    public  String getContent_type() {
        return (String) CurrentObjectValues.get("content_type");
    }

    public void setContent_type(String val) {
        setObjectValue (val, "content_type");
    }

    public  String getDomaineowner() {
        return (String) CurrentObjectValues.get("domaineowner");
    }

    public void setDomaineowner(String val) {
        setObjectValue (val, "domaineowner");
    }

    public  String getOrganisationversante() {
        return (String) CurrentObjectValues.get("organisationversante");
    }

    public void setOrganisationversante(String val) {
        setObjectValue (val, "organisationversante");
    }

    public  Date getDocsdate() {
        return (Date) CurrentObjectValues.get("docsdate");
    }

    public void setDocsdate(Date val) {
        setObjectValue (val, "docsdate");
    }

    public  String getDescription() {
        return (String) CurrentObjectValues.get("description");
    }

    public void setDescription(String val) {
        setObjectValue (val, "description");
    }

    public  String getTitle() {
        return (String) CurrentObjectValues.get("title");
    }

    public void setTitle(String val) {
        setObjectValue (val, "title");
    }

    public  String getSilo() {
        return (String) CurrentObjectValues.get("silo");
    }

    public void setSilo(String val) {
        setObjectValue (val, "silo");
    }

    public  String getMailowner() {
        return (String) CurrentObjectValues.get("mailowner");
    }

    public void setMailowner(String val) {
        setObjectValue (val, "mailowner");
    }

    public  Boolean getTransmis() {
        return (Boolean) CurrentObjectValues.get("transmis");
    }

    public void setTransmis(Boolean val) {
        setObjectValue (val, "transmis");
    }

    public  String getStatut() {
        return (String) CurrentObjectValues.get("statut");
    }

    public void setStatut(String val) {
        setObjectValue (val, "statut");
    }

    public  String getMotif() {
        return (String) CurrentObjectValues.get("motif");
    }

    public void setMotif(String val) {
        setObjectValue (val, "motif");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    private void initialize () {
        CODOBJ = "DRAFTS";
        setObjectValue("DRAFTS","codobj");
        T = DRAFTS_T;
        CLE = DRAFTS_CLE;
        PKEY = DRAFTS_PKEY;
    }
}
