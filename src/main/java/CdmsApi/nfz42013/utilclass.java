package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class utilclass extends TypeJAV {

    public utilclass () {
        super ();
        initialize ();
    }

    public utilclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer iduser;
        public String userid;
        public String profid;
        public String domnnom;
        public String userlib;
        public String langue;
        public String mailid;
        public String service;
        public final String F = "S008A012A015A025A050A004@050A050";
    }
    protected T UTIL_T = new T();

    protected class CLE {
        public Integer iduser;
        public final String F = "S008";
    }
    protected CLE UTIL_CLE = new CLE();

    protected class PKEY {
        public Integer iduser;
        public final String F = "S008";
    }
    protected PKEY UTIL_PKEY = new PKEY();

    public  Integer getIduser() {
        return (Integer) CurrentObjectValues.get("iduser");
    }

    public void setIduser(Integer val) {
        setObjectValue (val, "iduser");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  String getProfid() {
        return (String) CurrentObjectValues.get("profid");
    }

    public void setProfid(String val) {
        setObjectValue (val, "profid");
    }

    public  String getDomnnom() {
        return (String) CurrentObjectValues.get("domnnom");
    }

    public void setDomnnom(String val) {
        setObjectValue (val, "domnnom");
    }

    public  String getUserlib() {
        return (String) CurrentObjectValues.get("userlib");
    }

    public void setUserlib(String val) {
        setObjectValue (val, "userlib");
    }

    public  String getLangue() {
        return (String) CurrentObjectValues.get("langue");
    }

    public void setLangue(String val) {
        setObjectValue (val, "langue");
    }

    public  String getMailid() {
        return (String) CurrentObjectValues.get("mailid");
    }

    public void setMailid(String val) {
        setObjectValue (val, "mailid");
    }

    public  String getService() {
        return (String) CurrentObjectValues.get("service");
    }

    public void setService(String val) {
        setObjectValue (val, "service");
    }

    private void initialize () {
        CODOBJ = "UTIL";
        setObjectValue("UTIL","codobj");
        T = UTIL_T;
        CLE = UTIL_CLE;
        PKEY = UTIL_PKEY;
        DUPLICATE = true;
    }
    
}
