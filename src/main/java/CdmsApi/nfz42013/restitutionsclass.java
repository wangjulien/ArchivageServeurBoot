package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class restitutionsclass extends TypeJAV {

    public restitutionsclass () {
        super ();
        initialize ();
    }

    public restitutionsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class CLE {
        public String restitutionid;
        public final String F = "A005";
    }
    protected CLE restitutions_CLE = new CLE();

    protected class PKEY {
        public String restitutionid;
        public final String F = "A005";
    }
    protected PKEY restitutions_PKEY = new PKEY();

    protected class T {
        public String restitutionid;
        public String restitutionmotif;
        public String restitutionstatus;
        public String userid;
        public String domnnom;
        public Date horodatage;
        public String destinataire;
        public final String F = "A005A100A001A012A020T015A200";
    }
    protected T restitutions_T = new T();

    public  String getRestitutionid() {
        return (String) CurrentObjectValues.get("restitutionid");
    }

    public void setRestitutionid(String val) {
        setObjectValue (val, "restitutionid");
    }

    public  String getRestitutionmotif() {
        return (String) CurrentObjectValues.get("restitutionmotif");
    }

    public void setRestitutionmotif(String val) {
        setObjectValue (val, "restitutionmotif");
    }

    public  String getRestitutionstatus() {
        return (String) CurrentObjectValues.get("restitutionstatus");
    }

    public void setRestitutionstatus(String val) {
        setObjectValue (val, "restitutionstatus");
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
        CODOBJ = "restitutions";
        setObjectValue("restitutions","codobj");
        CLE = restitutions_CLE;
        PKEY = restitutions_PKEY;
        T = restitutions_T;
        DUPLICATE = true;
    }
}
