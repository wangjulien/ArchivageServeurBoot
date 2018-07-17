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


    protected class T {
        public Integer restitutionid;
        public String restitutionmotif;
        public String restitutionstatus;
        public String userid;
        public String domnnom;
        public Date horodatage;
        public String destinataire;
        public final String F = "S005A100A001A012A025T015A200";
    }
    protected T restitutions_T = new T();

    protected class CLE {
        public Integer restitutionid;
        public final String F = "S005";
    }
    protected CLE restitutions_CLE = new CLE();

    protected class PKEY {
        public Integer restitutionid;
        public final String F = "S005";
    }
    protected PKEY restitutions_PKEY = new PKEY();

    public  Integer getRestitutionid() {
        return (Integer) CurrentObjectValues.get("restitutionid");
    }

    public void setRestitutionid(Integer val) {
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
        T = restitutions_T;
        CLE = restitutions_CLE;
        PKEY = restitutions_PKEY;
        DUPLICATE = true;
    }
}
