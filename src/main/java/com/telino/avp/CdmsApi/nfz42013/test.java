package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;


public class test extends CdmsApi.types.TypeJAV {

    public test () {
        super ();
        initialize ();
    }

    public test (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class CLE {
        public String userid;
        public final String F = "A030";
    }
    protected CLE util_CLE = new CLE();

    protected class T {
        public String userid;
        public String nom;
        public String prenom;
        public String password;
        public final String F = "A030A030A030A030";
    }
    protected T util_T = new T();

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
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

    public  String getPassword() {
        return (String) CurrentObjectValues.get("password");
    }

    public void setPassword(String val) {
        setObjectValue (val, "password");
    }

    private void initialize () {
        CODOBJ = "util";
        setObjectValue("util","codobj");
        CLE = util_CLE;
        T = util_T;
    }

	
}
