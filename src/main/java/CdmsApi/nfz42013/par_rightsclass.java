package CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class par_rightsclass extends TypeJAV {

    public par_rightsclass () {
        super ();
        initialize ();
    }

    public par_rightsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer par_id;
        public String userid;
        public Boolean par_candeposit;
        public Boolean par_candelay;
        public Boolean par_candestroy;
        public Boolean par_canmodprof;
        public Boolean par_canread;
        public Boolean par_cancommunicate;
        public Boolean par_canrestitute;
        public final String F = "S005A012B005B005B005B005B005B005B005";
    }
    protected T par_rights_T = new T();

    protected class CLE {
        public Integer par_id;
        public String userid;
        public final String F = "S005A012";
    }
    protected CLE par_rights_CLE = new CLE();

    public  Integer getPar_id() {
        return (Integer) CurrentObjectValues.get("par_id");
    }

    public void setPar_id(Integer val) {
        setObjectValue (val, "par_id");
    }

    public  String getUserid() {
        return (String) CurrentObjectValues.get("userid");
    }

    public void setUserid(String val) {
        setObjectValue (val, "userid");
    }

    public  Boolean getPar_candeposit() {
        return (Boolean) CurrentObjectValues.get("par_candeposit");
    }

    public void setPar_candeposit(Boolean val) {
        setObjectValue (val, "par_candeposit");
    }

    public  Boolean getPar_candelay() {
        return (Boolean) CurrentObjectValues.get("par_candelay");
    }

    public void setPar_candelay(Boolean val) {
        setObjectValue (val, "par_candelay");
    }

    public  Boolean getPar_candestroy() {
        return (Boolean) CurrentObjectValues.get("par_candestroy");
    }

    public void setPar_candestroy(Boolean val) {
        setObjectValue (val, "par_candestroy");
    }

    public  Boolean getPar_canmodprof() {
        return (Boolean) CurrentObjectValues.get("par_canmodprof");
    }

    public void setPar_canmodprof(Boolean val) {
        setObjectValue (val, "par_canmodprof");
    }

    public  Boolean getPar_canread() {
        return (Boolean) CurrentObjectValues.get("par_canread");
    }

    public void setPar_canread(Boolean val) {
        setObjectValue (val, "par_canread");
    }

    public  Boolean getPar_cancommunicate() {
        return (Boolean) CurrentObjectValues.get("par_cancommunicate");
    }

    public void setPar_cancommunicate(Boolean val) {
        setObjectValue (val, "par_cancommunicate");
    }

    public  Boolean getPar_canrestitute() {
        return (Boolean) CurrentObjectValues.get("par_canrestitute");
    }

    public void setPar_canrestitute(Boolean val) {
        setObjectValue (val, "par_canrestitute");
    }

    private void initialize () {
        CODOBJ = "par_rights";
        setObjectValue("par_rights","codobj");
        T = par_rights_T;
        CLE = par_rights_CLE;
        DUPLICATE = true;
    }
}
