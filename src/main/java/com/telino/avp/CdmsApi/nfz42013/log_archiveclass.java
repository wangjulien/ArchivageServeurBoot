package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;
import CdmsApi.types.TypeJAV;

public class log_archiveclass extends TypeJAV {

    public log_archiveclass () {
        super ();
        initialize ();
    }

    public log_archiveclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer logid;
        public Date horodatage;
        public String operation;
        public String userid;
        public Integer docid;
        public String mailid;
        public String docsname;
        public final String F = "S008T015A030A012S008@050A200";
    }
    protected T log_archive_T = new T();

    protected class CLE {
        public Integer logid;
        public final String F = "S008";
    }
    protected CLE log_archive_CLE = new CLE();

    protected class PKEY {
        public Integer logid;
        public final String F = "S008";
    }
    protected PKEY log_archive_PKEY = new PKEY();

    public  Integer getLogid() {
        return (Integer) CurrentObjectValues.get("logid");
    }

    public void setLogid(Integer val) {
        setObjectValue (val, "logid");
    }

    public  Date getHorodatage() {
        return (Date) CurrentObjectValues.get("horodatage");
    }

    public void setHorodatage(Date val) {
        setObjectValue (val, "horodatage");
    }

    public  String getOperation() {
        return (String) CurrentObjectValues.get("operation");
    }

    public void setOperation(String val) {
        setObjectValue (val, "operation");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  Integer getDocid() {
        return (Integer) CurrentObjectValues.get("docid");
    }

    public void setDocid(Integer val) {
        setObjectValue (val, "docid");
    }

    public  String getMailid() {
        return (String) CurrentObjectValues.get("mailid");
    }

    public void setMailid(String val) {
        setObjectValue (val, "mailid");
    }

    public  String getDocsname() {
        return (String) CurrentObjectValues.get("docsname");
    }

    public void setDocsname(String val) {
        setObjectValue (val, "docsname");
    }

    private void initialize () {
        CODOBJ = "log_archive";
        setObjectValue("log_archive","codobj");
        T = log_archive_T;
        CLE = log_archive_CLE;
        PKEY = log_archive_PKEY;
    }
}
