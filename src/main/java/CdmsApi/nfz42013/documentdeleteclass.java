package CdmsApi.nfz42013;

import java.sql.Connection;
import java.util.Date;

import CdmsApi.types.TypeJAV;

public class documentdeleteclass extends TypeJAV {

    public documentdeleteclass () {
        super ();
        initialize ();
    }

    public documentdeleteclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer docid;
        public String title;
        public Date date;
        public String content;
        public String archiver_id;
        public String content_type;
        public Integer content_length;
        public final String F = "S008A060T010A900A020A100E008";
    }
    protected T documentdelete_T = new T();

    protected class CLE {
        public Integer docid;
        public final String F = "S008";
    }
    protected CLE documentdelete_CLE = new CLE();

    protected class PKEY {
        public Integer docid;
        public final String F = "S008";
    }
    protected PKEY documentdelete_PKEY = new PKEY();

    public  Integer getDocid() {
        return (Integer) CurrentObjectValues.get("docid");
    }

    public void setDocid(Integer val) {
        setObjectValue (val, "docid");
    }

    public  String getTitle() {
        return (String) CurrentObjectValues.get("title");
    }

    public void setTitle(String val) {
        setObjectValue (val, "title");
    }

    public  Date getDate() {
        return (Date) CurrentObjectValues.get("date");
    }

    public void setDate(Date val) {
        setObjectValue (val, "date");
    }

    public  String getContent() {
        return (String) CurrentObjectValues.get("content");
    }

    public void setContent(String val) {
        setObjectValue (val, "content");
    }

    public  String getArchiver_id() {
        return (String) CurrentObjectValues.get("archiver_id");
    }

    public void setArchiver_id(String val) {
        setObjectValue (val, "archiver_id");
    }

    public  String getContent_type() {
        return (String) CurrentObjectValues.get("content_type");
    }

    public void setContent_type(String val) {
        setObjectValue (val, "content_type");
    }

    public  Integer getContent_length() {
        return (Integer) CurrentObjectValues.get("content_length");
    }

    public void setContent_length(Integer val) {
        setObjectValue (val, "content_length");
    }

    private void initialize () {
        CODOBJ = "documentdelete";
        setObjectValue("documentdelete","codobj");
        T = documentdelete_T;
        CLE = documentdelete_CLE;
        PKEY = documentdelete_PKEY;
    }
}
