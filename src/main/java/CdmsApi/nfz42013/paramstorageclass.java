package CdmsApi.nfz42013;

import java.sql.Connection;

import CdmsApi.types.TypeJAV;

public class paramstorageclass extends TypeJAV {

    public paramstorageclass () {
        super ();
        initialize ();
    }

    public paramstorageclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer paramid;
        public String type_storage;
        public String remoteorlocal;
        public String hostname;
        public String port;
        public String servlet;
        public String directory;
        public String storageid;
        public final String F = "S010A200A200A200A004A030A200A200";
    }
    protected T paramstorage_T = new T();

    protected class CLE {
        public Integer paramid;
        public final String F = "S010";
    }
    protected CLE paramstorage_CLE = new CLE();

    protected class PKEY {
        public Integer paramid;
        public final String F = "S010";
    }
    protected PKEY paramstorage_PKEY = new PKEY();

    public  Integer getParamid() {
        return (Integer) CurrentObjectValues.get("paramid");
    }

    public void setParamid(Integer val) {
        setObjectValue (val, "paramid");
    }

    public  String getType_storage() {
        return (String) CurrentObjectValues.get("type_storage");
    }

    public void setType_storage(String val) {
        setObjectValue (val, "type_storage");
    }

    public  String getRemoteorlocal() {
        return (String) CurrentObjectValues.get("remoteorlocal");
    }

    public void setRemoteorlocal(String val) {
        setObjectValue (val, "remoteorlocal");
    }

    public  String getHostname() {
        return (String) CurrentObjectValues.get("hostname");
    }

    public void setHostname(String val) {
        setObjectValue (val, "hostname");
    }

    public  String getPort() {
        return (String) CurrentObjectValues.get("port");
    }

    public void setPort(String val) {
        setObjectValue (val, "port");
    }

    public  String getServlet() {
        return (String) CurrentObjectValues.get("servlet");
    }

    public void setServlet(String val) {
        setObjectValue (val, "servlet");
    }

    public  String getDirectory() {
        return (String) CurrentObjectValues.get("directory");
    }

    public void setDirectory(String val) {
        setObjectValue (val, "directory");
    }

    public  String getStorageid() {
        return (String) CurrentObjectValues.get("storageid");
    }

    public void setStorageid(String val) {
        setObjectValue (val, "storageid");
    }

    private void initialize () {
        CODOBJ = "paramstorage";
        setObjectValue("paramstorage","codobj");
        T = paramstorage_T;
        CLE = paramstorage_CLE;
        PKEY = paramstorage_PKEY;
        DUPLICATE = true;
    }
}
