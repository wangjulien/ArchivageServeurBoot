package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class communicationsvalidclass extends TypeJAV {

    public communicationsvalidclass () {
        super ();
        initialize ();
    }

    public communicationsvalidclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class CLE {
        public String communicationid;
        public final String F = "A005";
    }
    protected CLE communicationsvalid_CLE = new CLE();

    protected class PKEY {
        public String communicationid;
        public final String F = "A005";
    }
    protected PKEY communicationsvalid_PKEY = new PKEY();

    protected class T {
        public String communicationid;
        public String communicationmotif;
        public String communicationstatus;
        public String userid;
        public String domnnom;
        public Date horodatage;
        public String destinataire;
        public final String F = "A005A100A001A012A020T015A200";
    }
    protected T communicationsvalid_T = new T();

    public  String getCommunicationid() {
        return (String) CurrentObjectValues.get("communicationid");
    }

    public void setCommunicationid(String val) {
        setObjectValue (val, "communicationid");
    }

    public  String getCommunicationmotif() {
        return (String) CurrentObjectValues.get("communicationmotif");
    }

    public void setCommunicationmotif(String val) {
        setObjectValue (val, "communicationmotif");
    }

    public  String getCommunicationstatus() {
        return (String) CurrentObjectValues.get("communicationstatus");
    }

    public void setCommunicationstatus(String val) {
        setObjectValue (val, "communicationstatus");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  String getDomnnom() {
        return (String) CurrentObjectValues.get("domnnom");
    }

    public void setDomnnom(String val) {
        setObjectValue (val, "domnnom");
    }

    public  Date getHorodatage() {
        return (Date) CurrentObjectValues.get("horodatage");
    }

    public void setHorodatage(Date val) {
        setObjectValue (val, "horodatage");
    }

    public  String getDestinataire() {
        return (String) CurrentObjectValues.get("destinataire");
    }

    public void setDestinataire(String val) {
        setObjectValue (val, "destinataire");
    }

    private void initialize () {
        CODOBJ = "communicationsvalid";
        setObjectValue("communicationsvalid","codobj");
        CLE = communicationsvalid_CLE;
        PKEY = communicationsvalid_PKEY;
        T = communicationsvalid_T;
    }
}
