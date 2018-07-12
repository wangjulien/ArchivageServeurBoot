package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class restitutionlistclass extends TypeJAV {

    public restitutionlistclass () {
        super ();
        initialize ();
    }

    public restitutionlistclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer restitutionid;
        public Integer docid;
        public String title;
        public final String F = "S005S008A060";
    }
    protected T restitutionlist_T = new T();

    protected class CLE {
        public Integer restitutionid;
        public Integer docid;
        public final String F = "S005S008";
    }
    protected CLE restitutionlist_CLE = new CLE();

    public  Integer getRestitutionid() {
        return (Integer) CurrentObjectValues.get("restitutionid");
    }

    public void setRestitutionid(Integer val) {
        setObjectValue (val, "restitutionid");
    }

    public  Integer getDocid() {
        return (Integer) CurrentObjectValues.get("docid");
    }

    public void setDocid(Integer val) {
        setObjectValue (val, "docid");
    }

    public  String getTitle() {
        return (String) CurrentObjectValues.get("title");
    }

    public void setTitle(String val) {
        setObjectValue (val, "title");
    }

    private void initialize () {
        CODOBJ = "restitutionlist";
        setObjectValue("restitutionlist","codobj");
        T = restitutionlist_T;
        CLE = restitutionlist_CLE;
        DUPLICATE = true;
    }
}
