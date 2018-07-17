package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class typesclass extends TypeJAV {

    public typesclass () {
        super ();
        initialize ();
    }

    public typesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String doctype_archivage;
        public String doctypelib;
        public final String F = "A030A030";
    }
    protected T types_T = new T();

    protected class CLE {
        public String doctype_archivage;
        public final String F = "A030";
    }
    protected CLE types_CLE = new CLE();

    public  String getDoctype_archivage() {
        return (String) CurrentObjectValues.get("doctype_archivage");
    }

    public void setDoctype_archivage(String val) {
        setObjectValue (val, "doctype_archivage");
    }

    public  String getDoctypelib() {
        return (String) CurrentObjectValues.get("doctypelib");
    }

    public void setDoctypelib(String val) {
        setObjectValue (val, "doctypelib");
    }

    private void initialize () {
        CODOBJ = "types";
        setObjectValue("types","codobj");
        T = types_T;
        CLE = types_CLE;
        DUPLICATE = true;
    }
}
