package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class log_moclass extends TypeJAV {

    public log_moclass () {
        super ();
        initialize ();
    }

    public log_moclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class CLE {
        public String logid;
        public final String F = "A008";
    }
    protected CLE LOG_MO_CLE = new CLE();

    protected class PKEY {
        public String logid;
        public final String F = "A008";
    }
    protected PKEY LOG_MO_PKEY = new PKEY();

    protected class T {
        public String logid;
        public String userid;
        public String mailid;
        public Integer profdroits;
        public String logip;
        public String loghost;
        public String logreason;
        public Date logtime;
        public Boolean logbrisdeglace;
        public final String F = "A008A012@050E001A020A020A200T015B005";
    }
    protected T LOG_MO_T = new T();

    public  String getLogid() {
        return (String) CurrentObjectValues.get("logid");
    }

    public void setLogid(String val) {
        setObjectValue (val, "logid");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  String getMailid() {
        return (String) CurrentObjectValues.get("mailid");
    }

    public void setMailid(String val) {
        setObjectValue (val, "mailid");
    }

    public  Integer getProfdroits() {
        return (Integer) CurrentObjectValues.get("profdroits");
    }

    public void setProfdroits(Integer val) {
        setObjectValue (val, "profdroits");
    }

    public  String getLogip() {
        return (String) CurrentObjectValues.get("logip");
    }

    public void setLogip(String val) {
        setObjectValue (val, "logip");
    }

    public  String getLoghost() {
        return (String) CurrentObjectValues.get("loghost");
    }

    public void setLoghost(String val) {
        setObjectValue (val, "loghost");
    }

    public  String getLogreason() {
        return (String) CurrentObjectValues.get("logreason");
    }

    public void setLogreason(String val) {
        setObjectValue (val, "logreason");
    }

    public  Date getLogtime() {
        return (Date) CurrentObjectValues.get("logtime");
    }

    public void setLogtime(Date val) {
        setObjectValue (val, "logtime");
    }

    public  Boolean getLogbrisdeglace() {
        return (Boolean) CurrentObjectValues.get("logbrisdeglace");
    }

    public void setLogbrisdeglace(Boolean val) {
        setObjectValue (val, "logbrisdeglace");
    }

    private void initialize () {
        CODOBJ = "LOG_MO";
        setObjectValue("LOG_MO","codobj");
        CLE = LOG_MO_CLE;
        PKEY = LOG_MO_PKEY;
        T = LOG_MO_T;
    }
}
