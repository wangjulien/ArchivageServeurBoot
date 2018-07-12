package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class silosclass extends TypeJAV {

    public silosclass () {
        super ();
        initialize ();
    }

    public silosclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String silo;
        public String silolib;
        public String siloparent;
        public final String F = "A020A030A020";
    }
    protected T SILOS_T = new T();

    protected class CLE {
        public String silo;
        public final String F = "A020";
    }
    protected CLE SILOS_CLE = new CLE();

    public  String getSilo() {
        return (String) CurrentObjectValues.get("silo");
    }

    public void setSilo(String val) {
        setObjectValue (val, "silo");
    }

    public  String getSilolib() {
        return (String) CurrentObjectValues.get("silolib");
    }

    public void setSilolib(String val) {
        setObjectValue (val, "silolib");
    }

    public  String getSiloparent() {
        return (String) CurrentObjectValues.get("siloparent");
    }

    public void setSiloparent(String val) {
        setObjectValue (val, "siloparent");
    }

    private void initialize () {
        CODOBJ = "SILOS";
        setObjectValue("SILOS","codobj");
        T = SILOS_T;
        CLE = SILOS_CLE;
    }
}
