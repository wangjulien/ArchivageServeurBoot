package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class par_doctypesclass extends TypeJAV {

    public par_doctypesclass () {
        super ();
        initialize ();
    }

    public par_doctypesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer par_id;
        public Integer doctypeid;
        public Integer pa_docnum;
        public final String F = "S005S005S008";
    }
    protected T par_doctypes_T = new T();

    protected class CLE {
        public Integer pa_docnum;
        public final String F = "S008";
    }
    protected CLE par_doctypes_CLE = new CLE();

    protected class PKEY {
        public Integer pa_docnum;
        public final String F = "S008";
    }
    protected PKEY par_doctypes_PKEY = new PKEY();

    public  Integer getPar_id() {
        return (Integer) CurrentObjectValues.get("par_id");
    }

    public void setPar_id(Integer val) {
        setObjectValue (val, "par_id");
    }

    public  Integer getDoctypeid() {
        return (Integer) CurrentObjectValues.get("doctypeid");
    }

    public void setDoctypeid(Integer val) {
        setObjectValue (val, "doctypeid");
    }

    public  Integer getPa_docnum() {
        return (Integer) CurrentObjectValues.get("pa_docnum");
    }

    public void setPa_docnum(Integer val) {
        setObjectValue (val, "pa_docnum");
    }

    private void initialize () {
        CODOBJ = "par_doctypes";
        setObjectValue("par_doctypes","codobj");
        T = par_doctypes_T;
        CLE = par_doctypes_CLE;
        PKEY = par_doctypes_PKEY;
        DUPLICATE = true;
    }
}
