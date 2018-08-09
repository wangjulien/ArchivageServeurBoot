package CdmsApi.nfz42013;

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


    protected class CLE {
        public String logid;
        public final String F = "A008";
    }
    protected CLE log_event_CLE = new CLE();

    protected class PKEY {
        public String logid;
        public final String F = "A008";
    }
    protected PKEY log_event_PKEY = new PKEY();

    protected class T {
        public String logid;
        public String origin;
        public String processus;
        public String action;
        public Date horodatage;
        public String detail;
        public String customer_name;
        public String versionprocessus;
        public String operateur;
        public String archiveid;
        public String journalid;
        public String logtype;
        public final String F = "A008A060A040A060T015A900A100A006A040A005A005A001";
    }
    protected T log_event_T = new T();

    public  String getLogid() {
        return (String) CurrentObjectValues.get("logid");
    }

    public void setLogid(String val) {
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

    public  String getArchiveid() {
        return (String) CurrentObjectValues.get("archiveid");
    }

    public void setArchiveid(String val) {
        setObjectValue (val, "archiveid");
    }

    public  String getJournalid() {
        return (String) CurrentObjectValues.get("journalid");
    }

    public void setJournalid(String val) {
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
        CLE = log_event_CLE;
        PKEY = log_event_PKEY;
        T = log_event_T;
    }
}
