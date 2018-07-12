package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class mime_typeclass extends TypeJAV {

    public mime_typeclass () {
        super ();
        initialize ();
    }

    public mime_typeclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer mime_type_id;
        public String content_type;
        public String mime_description;
        public final String F = "S005A100A030";
    }
    protected T mime_type_T = new T();

    protected class CLE {
        public Integer mime_type_id;
        public final String F = "S005";
    }
    protected CLE mime_type_CLE = new CLE();

    protected class PKEY {
        public Integer mime_type_id;
        public final String F = "S005";
    }
    protected PKEY mime_type_PKEY = new PKEY();

    public  Integer getMime_type_id() {
        return (Integer) CurrentObjectValues.get("mime_type_id");
    }

    public void setMime_type_id(Integer val) {
        setObjectValue (val, "mime_type_id");
    }

    public  String getContent_type() {
        return (String) CurrentObjectValues.get("content_type");
    }

    public void setContent_type(String val) {
        setObjectValue (val, "content_type");
    }

    public  String getMime_description() {
        return (String) CurrentObjectValues.get("mime_description");
    }

    public void setMime_description(String val) {
        setObjectValue (val, "mime_description");
    }

    private void initialize () {
        CODOBJ = "mime_type";
        setObjectValue("mime_type","codobj");
        T = mime_type_T;
        CLE = mime_type_CLE;
        PKEY = mime_type_PKEY;
        DUPLICATE = true;
    }
}
