package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class drooclass extends TypeJAV {

    public drooclass () {
        super ();
        initialize ();
    }

    public drooclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String profid;
        public String objecod;
        public Boolean drooread;
        public Boolean droowrite;
        public Boolean droorewrite;
        public Boolean droodelete;
        public Boolean drooprint;
        public Boolean drooexport;
        public Boolean droodownload;
        public final String F = "A015A020B005B005B005B005B005B005B005";
    }
    protected T DROO_T = new T();

    protected class CLE {
        public String profid;
        public String objecod;
        public final String F = "A015A020";
    }
    protected CLE DROO_CLE = new CLE();

    public  String getProfid() {
        return (String) CurrentObjectValues.get("profid");
    }

    public void setProfid(String val) {
        setObjectValue (val, "profid");
    }

    public  String getObjecod() {
        return (String) CurrentObjectValues.get("objecod");
    }

    public void setObjecod(String val) {
        setObjectValue (val, "objecod");
    }

    public  Boolean getDrooread() {
        return (Boolean) CurrentObjectValues.get("drooread");
    }

    public void setDrooread(Boolean val) {
        setObjectValue (val, "drooread");
    }

    public  Boolean getDroowrite() {
        return (Boolean) CurrentObjectValues.get("droowrite");
    }

    public void setDroowrite(Boolean val) {
        setObjectValue (val, "droowrite");
    }

    public  Boolean getDroorewrite() {
        return (Boolean) CurrentObjectValues.get("droorewrite");
    }

    public void setDroorewrite(Boolean val) {
        setObjectValue (val, "droorewrite");
    }

    public  Boolean getDroodelete() {
        return (Boolean) CurrentObjectValues.get("droodelete");
    }

    public void setDroodelete(Boolean val) {
        setObjectValue (val, "droodelete");
    }

    public  Boolean getDrooprint() {
        return (Boolean) CurrentObjectValues.get("drooprint");
    }

    public void setDrooprint(Boolean val) {
        setObjectValue (val, "drooprint");
    }

    public  Boolean getDrooexport() {
        return (Boolean) CurrentObjectValues.get("drooexport");
    }

    public void setDrooexport(Boolean val) {
        setObjectValue (val, "drooexport");
    }

    public  Boolean getDroodownload() {
        return (Boolean) CurrentObjectValues.get("droodownload");
    }

    public void setDroodownload(Boolean val) {
        setObjectValue (val, "droodownload");
    }

    private void initialize () {
        CODOBJ = "DROO";
        setObjectValue("DROO","codobj");
        T = DROO_T;
        CLE = DROO_CLE;
        DUPLICATE = true;
    }
}
