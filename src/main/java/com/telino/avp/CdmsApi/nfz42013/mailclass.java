package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class mailclass extends TypeJAV {

    public mailclass () {
        super ();
        initialize ();
    }

    public mailclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String mailid;
        public String maillib;
        public String mailorg;
        public Boolean mailinterne;
        public Integer idmail;
        public Boolean estobservable;
        public final String F = "@050A035A030B005S006B005";
    }
    protected T mail_T = new T();

    protected class CLE {
        public Integer idmail;
        public final String F = "S006";
    }
    protected CLE mail_CLE = new CLE();

    protected class PKEY {
        public Integer idmail;
        public final String F = "S006";
    }
    protected PKEY mail_PKEY = new PKEY();

    public  String getMailid() {
        return (String) CurrentObjectValues.get("mailid");
    }

    public void setMailid(String val) {
        setObjectValue (val, "mailid");
    }

    public  String getMaillib() {
        return (String) CurrentObjectValues.get("maillib");
    }

    public void setMaillib(String val) {
        setObjectValue (val, "maillib");
    }

    public  String getMailorg() {
        return (String) CurrentObjectValues.get("mailorg");
    }

    public void setMailorg(String val) {
        setObjectValue (val, "mailorg");
    }

    public  Boolean getMailinterne() {
        return (Boolean) CurrentObjectValues.get("mailinterne");
    }

    public void setMailinterne(Boolean val) {
        setObjectValue (val, "mailinterne");
    }

    public  Integer getIdmail() {
        return (Integer) CurrentObjectValues.get("idmail");
    }

    public void setIdmail(Integer val) {
        setObjectValue (val, "idmail");
    }

    public  Boolean getEstobservable() {
        return (Boolean) CurrentObjectValues.get("estobservable");
    }

    public void setEstobservable(Boolean val) {
        setObjectValue (val, "estobservable");
    }

    private void initialize () {
        CODOBJ = "mail";
        setObjectValue("mail","codobj");
        T = mail_T;
        CLE = mail_CLE;
        PKEY = mail_PKEY;
        DUPLICATE = true;
    }
}
