package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class domnclass extends TypeJAV {

    public domnclass () {
        super ();
        initialize ();
    }

    public domnclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String domnnom;
        public String domnlib;
        public String orgaparent;
        public final String F = "A025A030A030";
    }
    protected T DOMN_T = new T();

    protected class CLE {
        public String domnnom;
        public final String F = "A025";
    }
    protected CLE DOMN_CLE = new CLE();

    public  String getDomnnom() {
        return (String) CurrentObjectValues.get("domnnom");
    }

    public void setDomnnom(String val) {
        setObjectValue (val, "domnnom");
    }

    public  String getDomnlib() {
        return (String) CurrentObjectValues.get("domnlib");
    }

    public void setDomnlib(String val) {
        setObjectValue (val, "domnlib");
    }

    public  String getOrgaparent() {
        return (String) CurrentObjectValues.get("orgaparent");
    }

    public void setOrgaparent(String val) {
        setObjectValue (val, "orgaparent");
    }

    private void initialize () {
        CODOBJ = "DOMN";
        setObjectValue("DOMN","codobj");
        T = DOMN_T;
        CLE = DOMN_CLE;
        DUPLICATE = true;
    }
}
