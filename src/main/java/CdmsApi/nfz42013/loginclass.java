package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class loginclass extends TypeJAV {

    public loginclass () {
        super ();
        initialize ();
    }

    public loginclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String userid;
        public String userpassword;
        public String nom;
        public String prenom;
        public final String F = "A012A030A040A040";
    }
    protected T login_T = new T();

    protected class CLE {
        public String userid;
        public final String F = "A012";
    }
    protected CLE login_CLE = new CLE();

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  String getUserpassword() {
        return (String) CurrentObjectValues.get("userpassword");
    }

    public void setUserpassword(String val) {
        setObjectValue (val, "userpassword");
    }

    public  String getNom() {
        return (String) CurrentObjectValues.get("nom");
    }

    public void setNom(String val) {
        setObjectValue (val, "nom");
    }

    public  String getPrenom() {
        return (String) CurrentObjectValues.get("prenom");
    }

    public void setPrenom(String val) {
        setObjectValue (val, "prenom");
    }

    private void initialize () {
        CODOBJ = "login";
        setObjectValue("login","codobj");
        T = login_T;
        CLE = login_CLE;
        DUPLICATE = true;
    }
}
