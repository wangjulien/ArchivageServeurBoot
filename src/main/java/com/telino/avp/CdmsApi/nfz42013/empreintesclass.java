package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class empreintesclass extends TypeJAV {

    public empreintesclass () {
        super ();
        initialize ();
    }

    public empreintesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer docid;
        public String empreinte;
        public String empreinte_algo;
        public final String F = "S008A900A100";
    }
    protected T empreintes_T = new T();

    protected class CLE {
        public Integer docid;
        public final String F = "S008";
    }
    protected CLE empreintes_CLE = new CLE();

    public  Integer getDocid() {
        return (Integer) CurrentObjectValues.get("docid");
    }

    public void setDocid(Integer val) {
        setObjectValue (val, "docid");
    }

    public  String getEmpreinte() {
        return (String) CurrentObjectValues.get("empreinte");
    }

    public void setEmpreinte(String val) {
        setObjectValue (val, "empreinte");
    }

    public  String getEmpreinte_algo() {
        return (String) CurrentObjectValues.get("empreinte_algo");
    }

    public void setEmpreinte_algo(String val) {
        setObjectValue (val, "empreinte_algo");
    }

    private void initialize () {
        CODOBJ = "empreintes";
        setObjectValue("empreintes","codobj");
        T = empreintes_T;
        CLE = empreintes_CLE;
    }
}
