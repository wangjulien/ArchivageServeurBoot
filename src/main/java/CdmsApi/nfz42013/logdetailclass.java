package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;
import CdmsApi.types.TypeJAV;

public class logdetailclass extends TypeJAV {

    public logdetailclass () {
        super ();
        initialize ();
    }

    public logdetailclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer logiddetail;
        public Integer logid;
        public Date logtimedetail;
        public String logtransaction;
        public String logsql;
        public final String F = "S008S008T015A030A000";
    }
    protected T LOGDETAIL_T = new T();

    protected class CLE {
        public Integer logiddetail;
        public final String F = "S008";
    }
    protected CLE LOGDETAIL_CLE = new CLE();

    protected class PKEY {
        public Integer logiddetail;
        public final String F = "S008";
    }
    protected PKEY LOGDETAIL_PKEY = new PKEY();

    public  Integer getLogiddetail() {
        return (Integer) CurrentObjectValues.get("logiddetail");
    }

    public void setLogiddetail(Integer val) {
        setObjectValue (val, "logiddetail");
    }

    public  Integer getLogid() {
        return (Integer) CurrentObjectValues.get("logid");
    }

    public void setLogid(Integer val) {
        setObjectValue (val, "logid");
    }

    public  Date getLogtimedetail() {
        return (Date) CurrentObjectValues.get("logtimedetail");
    }

    public void setLogtimedetail(Date val) {
        setObjectValue (val, "logtimedetail");
    }

    public  String getLogtransaction() {
        return (String) CurrentObjectValues.get("logtransaction");
    }

    public void setLogtransaction(String val) {
        setObjectValue (val, "logtransaction");
    }

    public  String getLogsql() {
        return (String) CurrentObjectValues.get("logsql");
    }

    public void setLogsql(String val) {
        setObjectValue (val, "logsql");
    }

    private void initialize () {
        CODOBJ = "LOGDETAIL";
        setObjectValue("LOGDETAIL","codobj");
        T = LOGDETAIL_T;
        CLE = LOGDETAIL_CLE;
        PKEY = LOGDETAIL_PKEY;
    }
}
