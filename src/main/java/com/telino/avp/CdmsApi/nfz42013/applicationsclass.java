package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class applicationsclass extends TypeJAV {

    public applicationsclass () {
        super ();
        initialize ();
    }

    public applicationsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String applicationcode;
        public String applicationname;
        public Boolean applicationvalidation;
        public final String F = "A010A020B005";
    }
    protected T applications_T = new T();

    protected class CLE {
        public String applicationcode;
        public final String F = "A010";
    }
    protected CLE applications_CLE = new CLE();

    public  String getApplicationcode() {
        return (String) CurrentObjectValues.get("applicationcode");
    }

    public void setApplicationcode(String val) {
        setObjectValue (val, "applicationcode");
    }

    public  String getApplicationname() {
        return (String) CurrentObjectValues.get("applicationname");
    }

    public void setApplicationname(String val) {
        setObjectValue (val, "applicationname");
    }

    public  Boolean getApplicationvalidation() {
        return (Boolean) CurrentObjectValues.get("applicationvalidation");
    }

    public void setApplicationvalidation(Boolean val) {
        setObjectValue (val, "applicationvalidation");
    }

    private void initialize () {
        CODOBJ = "applications";
        setObjectValue("applications","codobj");
        T = applications_T;
        CLE = applications_CLE;
        DUPLICATE = true;
    }
}
