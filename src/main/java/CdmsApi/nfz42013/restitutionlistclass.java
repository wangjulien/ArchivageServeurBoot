package CdmsApi.nfz42013;

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


    protected class CLE {
        public String restitutionid;
        public String docid;
        public final String F = "A005A008";
    }
    protected CLE restitutionlist_CLE = new CLE();

    protected class T {
        public String restitutionid;
        public String docid;
        public String title;
        public final String F = "A005A008A060";
    }
    protected T restitutionlist_T = new T();

    public  String getRestitutionid() {
        return (String) CurrentObjectValues.get("restitutionid");
    }

    public void setRestitutionid(String val) {
        setObjectValue (val, "restitutionid");
    }

    public  String getDocid() {
        return (String) CurrentObjectValues.get("docid");
    }

    public void setDocid(String val) {
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
        CLE = restitutionlist_CLE;
        T = restitutionlist_T;
        DUPLICATE = true;
    }
}
