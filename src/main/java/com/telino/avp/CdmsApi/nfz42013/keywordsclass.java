package com.telino.avp.CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class keywordsclass extends TypeJAV {

    public keywordsclass () {
        super ();
        initialize ();
    }

    public keywordsclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public String keyword;
        public String keywordvalues;
        public final String F = "A030A900";
    }
    protected T keywords_T = new T();

    protected class CLE {
        public String keyword;
        public final String F = "A030";
    }
    protected CLE keywords_CLE = new CLE();

    public  String getKeyword() {
        return (String) CurrentObjectValues.get("keyword");
    }

    public void setKeyword(String val) {
        setObjectValue (val, "keyword");
    }

    public  String getKeywordvalues() {
        return (String) CurrentObjectValues.get("keywordvalues");
    }

    public void setKeywordvalues(String val) {
        setObjectValue (val, "keywordvalues");
    }

    private void initialize () {
        CODOBJ = "keywords";
        setObjectValue("keywords","codobj");
        T = keywords_T;
        CLE = keywords_CLE;
        DUPLICATE = true;
    }
}
