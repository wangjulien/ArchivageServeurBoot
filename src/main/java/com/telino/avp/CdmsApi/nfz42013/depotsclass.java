package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;
import CdmsApi.types.TypeJAV;

public class depotsclass extends TypeJAV {

    public depotsclass () {
        super ();
        initialize ();
    }

    public depotsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer iddepot;
        public Date horodatage;
        public String demandeur;
        public String status;
        public String message;
        public final String F = "S008T015A040A100A200";
    }
    protected T depots_T = new T();

    protected class CLE {
        public Integer iddepot;
        public final String F = "S008";
    }
    protected CLE depots_CLE = new CLE();

    protected class PKEY {
        public Integer iddepot;
        public final String F = "S008";
    }
    protected PKEY depots_PKEY = new PKEY();

    public  Integer getIddepot() {
        return (Integer) CurrentObjectValues.get("iddepot");
    }

    public void setIddepot(Integer val) {
        setObjectValue (val, "iddepot");
    }

    public  Date getHorodatage() {
        return (Date) CurrentObjectValues.get("horodatage");
    }

    public void setHorodatage(Date val) {
        setObjectValue (val, "horodatage");
    }

    public  String getDemandeur() {
        return (String) CurrentObjectValues.get("demandeur");
    }

    public void setDemandeur(String val) {
        setObjectValue (val, "demandeur");
    }

    public  String getStatus() {
        return (String) CurrentObjectValues.get("status");
    }

    public void setStatus(String val) {
        setObjectValue (val, "status");
    }

    public  String getMessage() {
        return (String) CurrentObjectValues.get("message");
    }

    public void setMessage(String val) {
        setObjectValue (val, "message");
    }

    private void initialize () {
        CODOBJ = "depots";
        setObjectValue("depots","codobj");
        T = depots_T;
        CLE = depots_CLE;
        PKEY = depots_PKEY;
        DUPLICATE = true;
    }
}
