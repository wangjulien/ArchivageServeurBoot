package CdmsApi.nfz42013;

import java.sql.Connection;
import CdmsApi.types.TypeJAV;

public class paramclass extends TypeJAV {

    public paramclass () {
        super ();
        initialize ();
    }

    public paramclass (Connection conn, boolean b) {
        super (conn, b);
        initialize ();
    }


    protected class T {
        public Integer paramid;
        public String paramsmtpserver;
        public String paramsmtpuser;
        public String paramsmtppassword;
        public String paramsmtpport;
        public String paramadminuser;
        public String paramrepmail;
        public String paramreppj;
        public String paramaccueil;
        public String paramconfid;
        public Integer paramverrou;
        public Boolean paramlogdetail;
        public String elasticnode;
        public String topmenu;
        public String servletneoged;
        public String portneoged;
        public String baseneoged;
        public String nodeneoged;
        public String portavp;
        public String databasename;
        public String indexavp;
        public Boolean logread;
        public String elasticcluster;
        public Boolean cryptage;
        public Boolean mirror;
        public String mirroringurl;
        public Boolean elasticlogarchivage;
        public Boolean elasticlogevent;
        public Boolean pdfacheck;
        public Integer pdfalevel;
        public String stamptype;
        public Boolean externaltimestamp;
        public String archivageserver;
        public String urlneoged;
        public Integer passwdlevel;
        public String neogedserver;
        public Boolean updateged;
        public String openofficepath;
        public Integer maxconvertsize;
        public Boolean archivage_doublon;
        public final String F = "S010A100A100A100A006A100A100A100A050A200E002B005A100A900A030A004A020A030A004A100A030B005A100B005B005A200B005B005B005E003A010B005A040A200E003A040B005A300E005B005";
    }
    protected T PARAM_T = new T();

    protected class CLE {
        public Integer paramid;
        public final String F = "S010";
    }
    protected CLE PARAM_CLE = new CLE();

    protected class PKEY {
        public Integer paramid;
        public final String F = "S010";
    }
    protected PKEY PARAM_PKEY = new PKEY();

    public  Integer getParamid() {
        return (Integer) CurrentObjectValues.get("paramid");
    }

    public void setParamid(Integer val) {
        setObjectValue (val, "paramid");
    }

    public  String getParamsmtpserver() {
        return (String) CurrentObjectValues.get("paramsmtpserver");
    }

    public void setParamsmtpserver(String val) {
        setObjectValue (val, "paramsmtpserver");
    }

    public  String getParamsmtpuser() {
        return (String) CurrentObjectValues.get("paramsmtpuser");
    }

    public void setParamsmtpuser(String val) {
        setObjectValue (val, "paramsmtpuser");
    }

    public  String getParamsmtppassword() {
        return (String) CurrentObjectValues.get("paramsmtppassword");
    }

    public void setParamsmtppassword(String val) {
        setObjectValue (val, "paramsmtppassword");
    }

    public  String getParamsmtpport() {
        return (String) CurrentObjectValues.get("paramsmtpport");
    }

    public void setParamsmtpport(String val) {
        setObjectValue (val, "paramsmtpport");
    }

    public  String getParamadminuser() {
        return (String) CurrentObjectValues.get("paramadminuser");
    }

    public void setParamadminuser(String val) {
        setObjectValue (val, "paramadminuser");
    }

    public  String getParamrepmail() {
        return (String) CurrentObjectValues.get("paramrepmail");
    }

    public void setParamrepmail(String val) {
        setObjectValue (val, "paramrepmail");
    }

    public  String getParamreppj() {
        return (String) CurrentObjectValues.get("paramreppj");
    }

    public void setParamreppj(String val) {
        setObjectValue (val, "paramreppj");
    }

    public  String getParamaccueil() {
        return (String) CurrentObjectValues.get("paramaccueil");
    }

    public void setParamaccueil(String val) {
        setObjectValue (val, "paramaccueil");
    }

    public  String getParamconfid() {
        return (String) CurrentObjectValues.get("paramconfid");
    }

    public void setParamconfid(String val) {
        setObjectValue (val, "paramconfid");
    }

    public  Integer getParamverrou() {
        return (Integer) CurrentObjectValues.get("paramverrou");
    }

    public void setParamverrou(Integer val) {
        setObjectValue (val, "paramverrou");
    }

    public  Boolean getParamlogdetail() {
        return (Boolean) CurrentObjectValues.get("paramlogdetail");
    }

    public void setParamlogdetail(Boolean val) {
        setObjectValue (val, "paramlogdetail");
    }

    public  String getElasticnode() {
        return (String) CurrentObjectValues.get("elasticnode");
    }

    public void setElasticnode(String val) {
        setObjectValue (val, "elasticnode");
    }

    public  String getTopmenu() {
        return (String) CurrentObjectValues.get("topmenu");
    }

    public void setTopmenu(String val) {
        setObjectValue (val, "topmenu");
    }

    public  String getServletneoged() {
        return (String) CurrentObjectValues.get("servletneoged");
    }

    public void setServletneoged(String val) {
        setObjectValue (val, "servletneoged");
    }

    public  String getPortneoged() {
        return (String) CurrentObjectValues.get("portneoged");
    }

    public void setPortneoged(String val) {
        setObjectValue (val, "portneoged");
    }

    public  String getBaseneoged() {
        return (String) CurrentObjectValues.get("baseneoged");
    }

    public void setBaseneoged(String val) {
        setObjectValue (val, "baseneoged");
    }

    public  String getNodeneoged() {
        return (String) CurrentObjectValues.get("nodeneoged");
    }

    public void setNodeneoged(String val) {
        setObjectValue (val, "nodeneoged");
    }

    public  String getPortavp() {
        return (String) CurrentObjectValues.get("portavp");
    }

    public void setPortavp(String val) {
        setObjectValue (val, "portavp");
    }

    public  String getDatabasename() {
        return (String) CurrentObjectValues.get("databasename");
    }

    public void setDatabasename(String val) {
        setObjectValue (val, "databasename");
    }

    public  String getIndexavp() {
        return (String) CurrentObjectValues.get("indexavp");
    }

    public void setIndexavp(String val) {
        setObjectValue (val, "indexavp");
    }

    public  Boolean getLogread() {
        return (Boolean) CurrentObjectValues.get("logread");
    }

    public void setLogread(Boolean val) {
        setObjectValue (val, "logread");
    }

    public  String getElasticcluster() {
        return (String) CurrentObjectValues.get("elasticcluster");
    }

    public void setElasticcluster(String val) {
        setObjectValue (val, "elasticcluster");
    }

    public  Boolean getCryptage() {
        return (Boolean) CurrentObjectValues.get("cryptage");
    }

    public void setCryptage(Boolean val) {
        setObjectValue (val, "cryptage");
    }

    public  Boolean getMirror() {
        return (Boolean) CurrentObjectValues.get("mirror");
    }

    public void setMirror(Boolean val) {
        setObjectValue (val, "mirror");
    }

    public  String getMirroringurl() {
        return (String) CurrentObjectValues.get("mirroringurl");
    }

    public void setMirroringurl(String val) {
        setObjectValue (val, "mirroringurl");
    }

    public  Boolean getElasticlogarchivage() {
        return (Boolean) CurrentObjectValues.get("elasticlogarchivage");
    }

    public void setElasticlogarchivage(Boolean val) {
        setObjectValue (val, "elasticlogarchivage");
    }

    public  Boolean getElasticlogevent() {
        return (Boolean) CurrentObjectValues.get("elasticlogevent");
    }

    public void setElasticlogevent(Boolean val) {
        setObjectValue (val, "elasticlogevent");
    }

    public  Boolean getPdfacheck() {
        return (Boolean) CurrentObjectValues.get("pdfacheck");
    }

    public void setPdfacheck(Boolean val) {
        setObjectValue (val, "pdfacheck");
    }

    public  Integer getPdfalevel() {
        return (Integer) CurrentObjectValues.get("pdfalevel");
    }

    public void setPdfalevel(Integer val) {
        setObjectValue (val, "pdfalevel");
    }

    public  String getStamptype() {
        return (String) CurrentObjectValues.get("stamptype");
    }

    public void setStamptype(String val) {
        setObjectValue (val, "stamptype");
    }

    public  Boolean getExternaltimestamp() {
        return (Boolean) CurrentObjectValues.get("externaltimestamp");
    }

    public void setExternaltimestamp(Boolean val) {
        setObjectValue (val, "externaltimestamp");
    }

    public  String getArchivageserver() {
        return (String) CurrentObjectValues.get("archivageserver");
    }

    public void setArchivageserver(String val) {
        setObjectValue (val, "archivageserver");
    }

    public  String getUrlneoged() {
        return (String) CurrentObjectValues.get("urlneoged");
    }

    public void setUrlneoged(String val) {
        setObjectValue (val, "urlneoged");
    }

    public  Integer getPasswdlevel() {
        return (Integer) CurrentObjectValues.get("passwdlevel");
    }

    public void setPasswdlevel(Integer val) {
        setObjectValue (val, "passwdlevel");
    }

    public  String getNeogedserver() {
        return (String) CurrentObjectValues.get("neogedserver");
    }

    public void setNeogedserver(String val) {
        setObjectValue (val, "neogedserver");
    }

    public  Boolean getUpdateged() {
        return (Boolean) CurrentObjectValues.get("updateged");
    }

    public void setUpdateged(Boolean val) {
        setObjectValue (val, "updateged");
    }

    public  String getOpenofficepath() {
        return (String) CurrentObjectValues.get("openofficepath");
    }

    public void setOpenofficepath(String val) {
        setObjectValue (val, "openofficepath");
    }

    public  Integer getMaxconvertsize() {
        return (Integer) CurrentObjectValues.get("maxconvertsize");
    }

    public void setMaxconvertsize(Integer val) {
        setObjectValue (val, "maxconvertsize");
    }

    public  Boolean getArchivage_doublon() {
        return (Boolean) CurrentObjectValues.get("archivage_doublon");
    }

    public void setArchivage_doublon(Boolean val) {
        setObjectValue (val, "archivage_doublon");
    }

    private void initialize () {
        CODOBJ = "PARAM";
        setObjectValue("PARAM","codobj");
        T = PARAM_T;
        CLE = PARAM_CLE;
        PKEY = PARAM_PKEY;
        DUPLICATE = true;
    }
}
