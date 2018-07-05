package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class profclass extends TypeJAV {

    public profclass () {
        super ();
        initialize ();
    }

    public profclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String profid;
        public String proflib;
        public Integer profdroits;
        public String welcomescreen;
        public final String F = "A015A030E001A050";
    }
    protected T PROF_T = new T();

    protected class CLE {
        public String profid;
        public final String F = "A015";
    }
    protected CLE PROF_CLE = new CLE();

    public  String getProfid() {
        return (String) CurrentObjectValues.get("profid");
    }

    public void setProfid(String val) {
        setObjectValue (val, "profid");
    }

    public  String getProflib() {
        return (String) CurrentObjectValues.get("proflib");
    }

    public void setProflib(String val) {
        setObjectValue (val, "proflib");
    }

    public  Integer getProfdroits() {
        return (Integer) CurrentObjectValues.get("profdroits");
    }

    public void setProfdroits(Integer val) {
        setObjectValue (val, "profdroits");
    }

    public  String getWelcomescreen() {
        return (String) CurrentObjectValues.get("welcomescreen");
    }

    public void setWelcomescreen(String val) {
        setObjectValue (val, "welcomescreen");
    }

    private void initialize () {
        CODOBJ = "PROF";
        setObjectValue("PROF","codobj");
        T = PROF_T;
        CLE = PROF_CLE;
        DUPLICATE = true;
    }
}
