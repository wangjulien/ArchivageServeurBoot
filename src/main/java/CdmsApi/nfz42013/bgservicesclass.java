package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;
import CdmsApi.types.TypeJAV;

public class bgservicesclass extends TypeJAV {

    public bgservicesclass () {
        super ();
        initialize ();
    }

    public bgservicesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String bgs_cod;
        public String bgs_descr;
        public Boolean bgs_on;
        public String bgs_param;
        public Date bgs_start;
        public String bgs_process;
        public final String F = "A020A040B005A200T015A100";
    }
    protected T bgservices_T = new T();

    protected class CLE {
        public String bgs_cod;
        public final String F = "A020";
    }
    protected CLE bgservices_CLE = new CLE();

    public  String getBgs_cod() {
        return (String) CurrentObjectValues.get("bgs_cod");
    }

    public void setBgs_cod(String val) {
        setObjectValue (val, "bgs_cod");
    }

    public  String getBgs_descr() {
        return (String) CurrentObjectValues.get("bgs_descr");
    }

    public void setBgs_descr(String val) {
        setObjectValue (val, "bgs_descr");
    }

    public  Boolean getBgs_on() {
        return (Boolean) CurrentObjectValues.get("bgs_on");
    }

    public void setBgs_on(Boolean val) {
        setObjectValue (val, "bgs_on");
    }

    public  String getBgs_param() {
        return (String) CurrentObjectValues.get("bgs_param");
    }

    public void setBgs_param(String val) {
        setObjectValue (val, "bgs_param");
    }

    public  Date getBgs_start() {
        return (Date) CurrentObjectValues.get("bgs_start");
    }

    public void setBgs_start(Date val) {
        setObjectValue (val, "bgs_start");
    }

    public  String getBgs_process() {
        return (String) CurrentObjectValues.get("bgs_process");
    }

    public void setBgs_process(String val) {
        setObjectValue (val, "bgs_process");
    }

    private void initialize () {
        CODOBJ = "bgservices";
        setObjectValue("bgservices","codobj");
        T = bgservices_T;
        CLE = bgservices_CLE;
        DUPLICATE = true;
    }
}
