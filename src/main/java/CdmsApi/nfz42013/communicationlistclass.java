package CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class communicationlistclass extends TypeJAV {

    public communicationlistclass () {
        super ();
        initialize ();
    }

    public communicationlistclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class CLE {
        public String communicationid;
        public String docid;
        public final String F = "A005A008";
    }
    protected CLE communicationlist_CLE = new CLE();

    protected class T {
        public String communicationid;
        public String docid;
        public Boolean communique;
        public String title;
        public final String F = "A005A008B005A060";
    }
    protected T communicationlist_T = new T();

    public  String getCommunicationid() {
        return (String) CurrentObjectValues.get("communicationid");
    }

    public void setCommunicationid(String val) {
        setObjectValue (val, "communicationid");
    }

    public  String getDocid() {
        return (String) CurrentObjectValues.get("docid");
    }

    public void setDocid(String val) {
        setObjectValue (val, "docid");
    }

    public  Boolean getCommunique() {
        return (Boolean) CurrentObjectValues.get("communique");
    }

    public void setCommunique(Boolean val) {
        setObjectValue (val, "communique");
    }

    public  String getTitle() {
        return (String) CurrentObjectValues.get("title");
    }

    public void setTitle(String val) {
        setObjectValue (val, "title");
    }

    private void initialize () {
        CODOBJ = "communicationlist";
        setObjectValue("communicationlist","codobj");
        CLE = communicationlist_CLE;
        T = communicationlist_T;
        DUPLICATE = true;
    }
}
