package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class servicesclass extends TypeJAV {

    public servicesclass () {
        super ();
        initialize ();
    }

    public servicesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String service;
        public String libservice;
        public final String F = "A050A100";
    }
    protected T services_T = new T();

    protected class CLE {
        public String service;
        public final String F = "A050";
    }
    protected CLE services_CLE = new CLE();

    public  String getService() {
        return (String) CurrentObjectValues.get("service");
    }

    public void setService(String val) {
        setObjectValue (val, "service");
    }

    public  String getLibservice() {
        return (String) CurrentObjectValues.get("libservice");
    }

    public void setLibservice(String val) {
        setObjectValue (val, "libservice");
    }

    private void initialize () {
        CODOBJ = "services";
        setObjectValue("services","codobj");
        T = services_T;
        CLE = services_CLE;
        DUPLICATE = true;
    }
}
