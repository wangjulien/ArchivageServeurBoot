package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class paramuserclass extends TypeJAV {

    public paramuserclass () {
        super ();
        initialize ();
    }

    public paramuserclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String lstmuser;
        public String lstmmenu;
        public String lstmwidgets;
        public String lstmcolor;
        public String lstmimage;
        public String lstmparam;
        public String lstmcolorfond;
        public String lstmcolorform;
        public Integer iduser;
        public final String F = "A030A800A030C020A030A250C020C020S008";
    }
    protected T paramuser_T = new T();

    protected class CLE {
        public Integer iduser;
        public final String F = "S008";
    }
    protected CLE paramuser_CLE = new CLE();

    public  String getLstmuser() {
        return (String) CurrentObjectValues.get("lstmuser");
    }

    public void setLstmuser(String val) {
        setObjectValue (val, "lstmuser");
    }

    public  String getLstmmenu() {
        return (String) CurrentObjectValues.get("lstmmenu");
    }

    public void setLstmmenu(String val) {
        setObjectValue (val, "lstmmenu");
    }

    public  String getLstmwidgets() {
        return (String) CurrentObjectValues.get("lstmwidgets");
    }

    public void setLstmwidgets(String val) {
        setObjectValue (val, "lstmwidgets");
    }

    public  String getLstmcolor() {
        return (String) CurrentObjectValues.get("lstmcolor");
    }

    public void setLstmcolor(String val) {
        setObjectValue (val, "lstmcolor");
    }

    public  String getLstmimage() {
        return (String) CurrentObjectValues.get("lstmimage");
    }

    public void setLstmimage(String val) {
        setObjectValue (val, "lstmimage");
    }

    public  String getLstmparam() {
        return (String) CurrentObjectValues.get("lstmparam");
    }

    public void setLstmparam(String val) {
        setObjectValue (val, "lstmparam");
    }

    public  String getLstmcolorfond() {
        return (String) CurrentObjectValues.get("lstmcolorfond");
    }

    public void setLstmcolorfond(String val) {
        setObjectValue (val, "lstmcolorfond");
    }

    public  String getLstmcolorform() {
        return (String) CurrentObjectValues.get("lstmcolorform");
    }

    public void setLstmcolorform(String val) {
        setObjectValue (val, "lstmcolorform");
    }

    public  Integer getIduser() {
        return (Integer) CurrentObjectValues.get("iduser");
    }

    public void setIduser(Integer val) {
        setObjectValue (val, "iduser");
    }

    private void initialize () {
        CODOBJ = "paramuser";
        setObjectValue("paramuser","codobj");
        T = paramuser_T;
        CLE = paramuser_CLE;
        DUPLICATE = true;
    }
}
