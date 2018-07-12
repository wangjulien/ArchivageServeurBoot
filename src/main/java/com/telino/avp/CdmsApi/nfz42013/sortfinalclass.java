package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class sortfinalclass extends TypeJAV {

    public sortfinalclass () {
        super ();
        initialize ();
    }

    public sortfinalclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer sortfinalid;
        public final String F = "S005";
    }
    protected T sortfinal_T = new T();

    protected class CLE {
        public final String F = "";
    }
    protected CLE sortfinal_CLE = new CLE();

    protected class PKEY {
        public final String F = "";
    }
    protected PKEY sortfinal_PKEY = new PKEY();

    public  Integer getSortfinalid() {
        return (Integer) CurrentObjectValues.get("sortfinalid");
    }

    public void setSortfinalid(Integer val) {
        setObjectValue (val, "sortfinalid");
    }

    private void initialize () {
        CODOBJ = "sortfinal";
        setObjectValue("sortfinal","codobj");
        T = sortfinal_T;
        CLE = sortfinal_CLE;
        PKEY = sortfinal_PKEY;
        DUPLICATE = true;
    }
}
