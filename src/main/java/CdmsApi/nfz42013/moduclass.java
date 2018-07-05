package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class moduclass extends TypeJAV {

    public moduclass () {
        super ();
        initialize ();
    }

    public moduclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String moducod;
        public String modulib;
        public String modubasins;
        public String moducolor;
        public final String F = "A004A030A001A010";
    }
    protected T MODU_T = new T();

    protected class CLE {
        public String moducod;
        public final String F = "A004";
    }
    protected CLE MODU_CLE = new CLE();

    public  String getModucod() {
        return (String) CurrentObjectValues.get("moducod");
    }

    public void setModucod(String val) {
        setObjectValue (val, "moducod");
    }

    public  String getModulib() {
        return (String) CurrentObjectValues.get("modulib");
    }

    public void setModulib(String val) {
        setObjectValue (val, "modulib");
    }

    public  String getModubasins() {
        return (String) CurrentObjectValues.get("modubasins");
    }

    public void setModubasins(String val) {
        setObjectValue (val, "modubasins");
    }

    public  String getModucolor() {
        return (String) CurrentObjectValues.get("moducolor");
    }

    public void setModucolor(String val) {
        setObjectValue (val, "moducolor");
    }

    private void initialize () {
        CODOBJ = "MODU";
        setObjectValue("MODU","codobj");
        T = MODU_T;
        CLE = MODU_CLE;
        DUPLICATE = true;
    }
}
