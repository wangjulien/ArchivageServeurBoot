package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class conversionsclass extends TypeJAV {

    public conversionsclass () {
        super ();
        initialize ();
    }

    public conversionsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer conversion_id;
        public String conversion_name;
        public String conversion_program;
        public String conversion_source;
        public String conversion_target;
        public final String F = "S005A030A030A030A030";
    }
    protected T conversions_T = new T();

    protected class CLE {
        public Integer conversion_id;
        public final String F = "S005";
    }
    protected CLE conversions_CLE = new CLE();

    protected class PKEY {
        public Integer conversion_id;
        public final String F = "S005";
    }
    protected PKEY conversions_PKEY = new PKEY();

    public  Integer getConversion_id() {
        return (Integer) CurrentObjectValues.get("conversion_id");
    }

    public void setConversion_id(Integer val) {
        setObjectValue (val, "conversion_id");
    }

    public  String getConversion_name() {
        return (String) CurrentObjectValues.get("conversion_name");
    }

    public void setConversion_name(String val) {
        setObjectValue (val, "conversion_name");
    }

    public  String getConversion_program() {
        return (String) CurrentObjectValues.get("conversion_program");
    }

    public void setConversion_program(String val) {
        setObjectValue (val, "conversion_program");
    }

    public  String getConversion_source() {
        return (String) CurrentObjectValues.get("conversion_source");
    }

    public void setConversion_source(String val) {
        setObjectValue (val, "conversion_source");
    }

    public  String getConversion_target() {
        return (String) CurrentObjectValues.get("conversion_target");
    }

    public void setConversion_target(String val) {
        setObjectValue (val, "conversion_target");
    }

    private void initialize () {
        CODOBJ = "conversions";
        setObjectValue("conversions","codobj");
        T = conversions_T;
        CLE = conversions_CLE;
        PKEY = conversions_PKEY;
        DUPLICATE = true;
    }
}
