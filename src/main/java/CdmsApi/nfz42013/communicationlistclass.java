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


    protected class T {
        public Integer communicationid;
        public Integer docid;
        public Boolean communique;
        public String title;
        public final String F = "S005S008B005A060";
    }
    protected T communicationlist_T = new T();

    protected class CLE {
        public Integer communicationid;
        public Integer docid;
        public final String F = "S005S008";
    }
    protected CLE communicationlist_CLE = new CLE();

    public  Integer getCommunicationid() {
        return (Integer) CurrentObjectValues.get("communicationid");
    }

    public void setCommunicationid(Integer val) {
        setObjectValue (val, "communicationid");
    }

    public  Integer getDocid() {
        return (Integer) CurrentObjectValues.get("docid");
    }

    public void setDocid(Integer val) {
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
        T = communicationlist_T;
        CLE = communicationlist_CLE;
        DUPLICATE = true;
    }
}
