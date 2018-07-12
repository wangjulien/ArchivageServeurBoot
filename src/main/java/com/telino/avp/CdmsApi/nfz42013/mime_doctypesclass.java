package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class mime_doctypesclass extends TypeJAV {

    public mime_doctypesclass () {
        super ();
        initialize ();
    }

    public mime_doctypesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer mime_doctype_id;
        public Integer mime_type_id;
        public Integer doctypeid;
        public final String F = "S005S005S005";
    }
    protected T mime_doctypes_T = new T();

    protected class CLE {
        public Integer mime_type_id;
        public Integer doctypeid;
        public final String F = "S005S005";
    }
    protected CLE mime_doctypes_CLE = new CLE();

    protected class PKEY {
        public Integer mime_doctype_id;
        public final String F = "S005";
    }
    protected PKEY mime_doctypes_PKEY = new PKEY();

    public  Integer getMime_doctype_id() {
        return (Integer) CurrentObjectValues.get("mime_doctype_id");
    }

    public void setMime_doctype_id(Integer val) {
        setObjectValue (val, "mime_doctype_id");
    }

    public  Integer getMime_type_id() {
        return (Integer) CurrentObjectValues.get("mime_type_id");
    }

    public void setMime_type_id(Integer val) {
        setObjectValue (val, "mime_type_id");
    }

    public  Integer getDoctypeid() {
        return (Integer) CurrentObjectValues.get("doctypeid");
    }

    public void setDoctypeid(Integer val) {
        setObjectValue (val, "doctypeid");
    }

    private void initialize () {
        CODOBJ = "mime_doctypes";
        setObjectValue("mime_doctypes","codobj");
        T = mime_doctypes_T;
        CLE = mime_doctypes_CLE;
        PKEY = mime_doctypes_PKEY;
        DUPLICATE = true;
    }
}
