package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class log_eventclass extends TypeJAV {

    public log_eventclass () {
        super ();
        initialize ();
    }

    public log_eventclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer logid;
        public String origin;
        public String processus;
        public String action;
        public Date horodatage;
        public String detail;
        public String customer_name;
        public String versionprocessus;
        public String operateur;
        public Integer archiveid;
        public Integer journalid;
        public String logtype;
        public final String F = "S008A060A040A060T015A900A100A006A040E005E005A001";
    }
    protected T log_event_T = new T();

    protected class CLE {
        public Integer logid;
        public final String F = "S008";
    }
    protected CLE log_event_CLE = new CLE();

    protected class PKEY {
        public Integer logid;
        public final String F = "S008";
    }
    protected PKEY log_event_PKEY = new PKEY();

    public  Integer getLogid() {
        return (Integer) CurrentObjectValues.get("logid");
    }

    public void setLogid(Integer val) {
        setObjectValue (val, "logid");
    }

    public  String getOrigin() {
        return (String) CurrentObjectValues.get("origin");
    }

    public void setOrigin(String val) {
        setObjectValue (val, "origin");
    }

    public  String getProcessus() {
        return (String) CurrentObjectValues.get("processus");
    }

    public void setProcessus(String val) {
        setObjectValue (val, "processus");
    }

    public  String getAction() {
        return (String) CurrentObjectValues.get("action");
    }

    public void setAction(String val) {
        setObjectValue (val, "action");
    }

    public  Date getHorodatage() {
        return (Date) CurrentObjectValues.get("horodatage");
    }

    public void setHorodatage(Date val) {
        setObjectValue (val, "horodatage");
    }

    public  String getDetail() {
        return (String) CurrentObjectValues.get("detail");
    }

    public void setDetail(String val) {
        setObjectValue (val, "detail");
    }

    public  String getCustomer_name() {
        return (String) CurrentObjectValues.get("customer_name");
    }

    public void setCustomer_name(String val) {
        setObjectValue (val, "customer_name");
    }

    public  String getVersionprocessus() {
        return (String) CurrentObjectValues.get("versionprocessus");
    }

    public void setVersionprocessus(String val) {
        setObjectValue (val, "versionprocessus");
    }

    public  String getOperateur() {
        return (String) CurrentObjectValues.get("operateur");
    }

    public void setOperateur(String val) {
        setObjectValue (val, "operateur");
    }

    public  Integer getArchiveid() {
        return (Integer) CurrentObjectValues.get("archiveid");
    }

    public void setArchiveid(Integer val) {
        setObjectValue (val, "archiveid");
    }

    public  Integer getJournalid() {
        return (Integer) CurrentObjectValues.get("journalid");
    }

    public void setJournalid(Integer val) {
        setObjectValue (val, "journalid");
    }

    public  String getLogtype() {
        return (String) CurrentObjectValues.get("logtype");
    }

    public void setLogtype(String val) {
        setObjectValue (val, "logtype");
    }

    private void initialize () {
        CODOBJ = "log_event";
        setObjectValue("log_event","codobj");
        T = log_event_T;
        CLE = log_event_CLE;
        PKEY = log_event_PKEY;
    }
}
