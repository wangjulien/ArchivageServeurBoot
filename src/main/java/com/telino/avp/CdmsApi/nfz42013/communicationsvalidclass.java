package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class communicationsvalidclass extends TypeJAV {

    public communicationsvalidclass () {
        super ();
        initialize ();
    }

    public communicationsvalidclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer communicationid;
        public String communicationmotif;
        public String communicationstatus;
        public String userid;
        public String domnnom;
        public Date horodatage;
        public String destinataire;
        public final String F = "S005A100A001A012A020T015A200";
    }
    protected T communicationsvalid_T = new T();

    protected class CLE {
        public Integer communicationid;
        public final String F = "S005";
    }
    protected CLE communicationsvalid_CLE = new CLE();

    protected class PKEY {
        public Integer communicationid;
        public final String F = "S005";
    }
    protected PKEY communicationsvalid_PKEY = new PKEY();

    public  Integer getCommunicationid() {
        return (Integer) CurrentObjectValues.get("communicationid");
    }

    public void setCommunicationid(Integer val) {
        setObjectValue (val, "communicationid");
    }

    public  String getCommunicationmotif() {
        return (String) CurrentObjectValues.get("communicationmotif");
    }

    public void setCommunicationmotif(String val) {
        setObjectValue (val, "communicationmotif");
    }

    public  String getCommunicationstatus() {
        return (String) CurrentObjectValues.get("communicationstatus");
    }

    public void setCommunicationstatus(String val) {
        setObjectValue (val, "communicationstatus");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  String getDomnnom() {
        return (String) CurrentObjectValues.get("domnnom");
    }

    public void setDomnnom(String val) {
        setObjectValue (val, "domnnom");
    }

    public  Date getHorodatage() {
        return (Date) CurrentObjectValues.get("horodatage");
    }

    public void setHorodatage(Date val) {
        setObjectValue (val, "horodatage");
    }

    public  String getDestinataire() {
        return (String) CurrentObjectValues.get("destinataire");
    }

    public void setDestinataire(String val) {
        setObjectValue (val, "destinataire");
    }

    private void initialize () {
        CODOBJ = "communicationsvalid";
        setObjectValue("communicationsvalid","codobj");
        T = communicationsvalid_T;
        CLE = communicationsvalid_CLE;
        PKEY = communicationsvalid_PKEY;
    }
}

