package CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class types_manuclass extends TypeJAV {

    public types_manuclass () {
        super ();
        initialize ();
    }

    public types_manuclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer typemanuid;
        public String source;
        public String doctype;
        public String keywordslist;
        public String typagename;
        public String mailowner;
        public String domaineowner;
        public String shareddomains;
        public String fileprefix;
        public Integer workflowid;
        public final String F = "S008A010A015A200A100A300A030A900A060S008";
    }
    protected T TYPES_MANU_T = new T();

    protected class CLE {
        public Integer typemanuid;
        public final String F = "S008";
    }
    protected CLE TYPES_MANU_CLE = new CLE();

    protected class PKEY {
        public Integer typemanuid;
        public final String F = "S008";
    }
    protected PKEY TYPES_MANU_PKEY = new PKEY();

    public  Integer getTypemanuid() {
        return (Integer) CurrentObjectValues.get("typemanuid");
    }

    public void setTypemanuid(Integer val) {
        setObjectValue (val, "typemanuid");
    }

    public  String getSource() {
        return (String) CurrentObjectValues.get("source");
    }

    public void setSource(String val) {
        setObjectValue (val, "source");
    }

    public  String getDoctype() {
        return (String) CurrentObjectValues.get("doctype");
    }

    public void setDoctype(String val) {
        setObjectValue (val, "doctype");
    }

    public  String getKeywordslist() {
        return (String) CurrentObjectValues.get("keywordslist");
    }

    public void setKeywordslist(String val) {
        setObjectValue (val, "keywordslist");
    }

    public  String getTypagename() {
        return (String) CurrentObjectValues.get("typagename");
    }

    public void setTypagename(String val) {
        setObjectValue (val, "typagename");
    }

    public  String getMailowner() {
        return (String) CurrentObjectValues.get("mailowner");
    }

    public void setMailowner(String val) {
        setObjectValue (val, "mailowner");
    }

    public  String getDomaineowner() {
        return (String) CurrentObjectValues.get("domaineowner");
    }

    public void setDomaineowner(String val) {
        setObjectValue (val, "domaineowner");
    }

    public  String getShareddomains() {
        return (String) CurrentObjectValues.get("shareddomains");
    }

    public void setShareddomains(String val) {
        setObjectValue (val, "shareddomains");
    }

    public  String getFileprefix() {
        return (String) CurrentObjectValues.get("fileprefix");
    }

    public void setFileprefix(String val) {
        setObjectValue (val, "fileprefix");
    }

    public  Integer getWorkflowid() {
        return (Integer) CurrentObjectValues.get("workflowid");
    }

    public void setWorkflowid(Integer val) {
        setObjectValue (val, "workflowid");
    }

    private void initialize () {
        CODOBJ = "TYPES_MANU";
        setObjectValue("TYPES_MANU","codobj");
        T = TYPES_MANU_T;
        CLE = TYPES_MANU_CLE;
        PKEY = TYPES_MANU_PKEY;
    }
}