package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class classifarchivageclass extends TypeJAV {

    public classifarchivageclass () {
        super ();
        initialize ();
    }

    public classifarchivageclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String doctype_archivage;
        public String categorie;
        public String keywordslist;
        public final String F = "A030A030A900";
    }
    protected T classifarchivage_T = new T();

    protected class CLE {
        public String doctype_archivage;
        public String categorie;
        public final String F = "A030A030";
    }
    protected CLE classifarchivage_CLE = new CLE();

    public  String getDoctype_archivage() {
        return (String) CurrentObjectValues.get("doctype_archivage");
    }

    public void setDoctype_archivage(String val) {
        setObjectValue (val, "doctype_archivage");
    }

    public  String getCategorie() {
        return (String) CurrentObjectValues.get("categorie");
    }

    public void setCategorie(String val) {
        setObjectValue (val, "categorie");
    }

    public  String getKeywordslist() {
        return (String) CurrentObjectValues.get("keywordslist");
    }

    public void setKeywordslist(String val) {
        setObjectValue (val, "keywordslist");
    }

    private void initialize () {
        CODOBJ = "classifarchivage";
        setObjectValue("classifarchivage","codobj");
        T = classifarchivage_T;
        CLE = classifarchivage_CLE;
        DUPLICATE = true;
    }
}
