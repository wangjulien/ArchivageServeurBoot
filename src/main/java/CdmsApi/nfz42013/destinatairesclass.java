package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class destinatairesclass extends TypeJAV {

    public destinatairesclass () {
        super ();
        initialize ();
    }

    public destinatairesclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String destinataire;
        public String infosdestinataires;
        public final String F = "A200A200";
    }
    protected T destinataires_T = new T();

    protected class CLE {
        public String destinataire;
        public final String F = "A200";
    }
    protected CLE destinataires_CLE = new CLE();

    public  String getDestinataire() {
        return (String) CurrentObjectValues.get("destinataire");
    }

    public void setDestinataire(String val) {
        setObjectValue (val, "destinataire");
    }

    public  String getInfosdestinataires() {
        return (String) CurrentObjectValues.get("infosdestinataires");
    }

    public void setInfosdestinataires(String val) {
        setObjectValue (val, "infosdestinataires");
    }

    private void initialize () {
        CODOBJ = "destinataires";
        setObjectValue("destinataires","codobj");
        T = destinataires_T;
        CLE = destinataires_CLE;
        DUPLICATE = true;
    }
}
