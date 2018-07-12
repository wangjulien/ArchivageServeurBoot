package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class profilsclass extends TypeJAV {

    public profilsclass () {
        super ();
        initialize ();
    }

    public profilsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer par_id;
        public String ar_profile;
        public Integer par_conservation;
        public Integer destructioncriteriaid;
        public Integer sortfinalid;
        public final String F = "S005A030E005S005S005";
    }
    protected T profils_T = new T();

    protected class CLE {
        public Integer par_id;
        public final String F = "S005";
    }
    protected CLE profils_CLE = new CLE();

    protected class PKEY {
        public Integer par_id;
        public final String F = "S005";
    }
    protected PKEY profils_PKEY = new PKEY();

    public  Integer getPar_id() {
        return (Integer) CurrentObjectValues.get("par_id");
    }

    public void setPar_id(Integer val) {
        setObjectValue (val, "par_id");
    }

    public  String getAr_profile() {
        return (String) CurrentObjectValues.get("ar_profile");
    }

    public void setAr_profile(String val) {
        setObjectValue (val, "ar_profile");
    }

    public  Integer getPar_conservation() {
        return (Integer) CurrentObjectValues.get("par_conservation");
    }

    public void setPar_conservation(Integer val) {
        setObjectValue (val, "par_conservation");
    }

    public  Integer getDestructioncriteriaid() {
        return (Integer) CurrentObjectValues.get("destructioncriteriaid");
    }

    public void setDestructioncriteriaid(Integer val) {
        setObjectValue (val, "destructioncriteriaid");
    }

    public  Integer getSortfinalid() {
        return (Integer) CurrentObjectValues.get("sortfinalid");
    }

    public void setSortfinalid(Integer val) {
        setObjectValue (val, "sortfinalid");
    }

    private void initialize () {
        CODOBJ = "profils";
        setObjectValue("profils","codobj");
        T = profils_T;
        CLE = profils_CLE;
        PKEY = profils_PKEY;
        DUPLICATE = true;
    }
}
