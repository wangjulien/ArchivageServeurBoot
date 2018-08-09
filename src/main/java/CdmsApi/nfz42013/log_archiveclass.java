package CdmsApi.nfz42013;

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


    protected class CLE {
        public String logid;
        public final String F = "A008";
    }
    protected CLE log_archive_CLE = new CLE();

    protected class PKEY {
        public String logid;
        public final String F = "A008";
    }
    protected PKEY log_archive_PKEY = new PKEY();

    protected class T {
        public String logid;
        public Date horodatage;
        public String operation;
        public String userid;
        public String docid;
        public String mailid;
        public String docsname;
        public final String F = "A008T015A030A012A008@050A200";
    }
    protected T log_archive_T = new T();

    public  String getLogid() {
        return (String) CurrentObjectValues.get("logid");
    }

    public void setLogid(String val) {
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

    public  String getDocid() {
        return (String) CurrentObjectValues.get("docid");
    }

    public void setDocid(String val) {
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
        CLE = log_archive_CLE;
        PKEY = log_archive_PKEY;
        T = log_archive_T;
    }
}
