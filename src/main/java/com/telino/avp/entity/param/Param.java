package com.telino.avp.entity.param;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "param")
public class Param {

	@Id
	private Integer paramId;

	private String paramsmtpserver;
	private String paramsmtpuser;
	private String paramsmtppassword;
	private String paramsmtpport;
	private String paramadminuser;
	private String paramrepmail;
	private String paramreppj;
	private String paramaccueil;
	private String paramconfid;
	private int paramverrou;
	private boolean paramlogdetail;
	private String elasticnode;
	private String elasticpath;
	private String topmenu;
	private String servletneoged;
	private String portneoged;
	private String baseneoged;
	private String nodeneoged;
	private String portavp;
	private String databasename;
	private String indexavp;
	private int maxusers;
	private boolean logread;
	private String schemaneoged;
	private String elasticcluster;
	private boolean cryptage;
	private boolean mirror;
	private String mirroringurl;
	private boolean elasticlogarchivage;
	private boolean elasticlogevent;
	private boolean pdfacheck;
	private int pdfalevel;
	private String stamptype;
	private boolean externaltimestamp;
	private String archivageserver;
	private String urlneoged;
	private int passwdlevel;
	private String neogedserver;
	private boolean updateged;
	private String initnumber;
	private String openofficepath;
	private int maxconvertsize;
	private boolean archivage_doublon;
	private UUID cryptageid;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "storageid")
	private StorageParam masterStorageParam;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "storagemirrorid")
	private StorageParam mirrorStorageParam;

	public Param() {
		super();
	}

	public Integer getParamId() {
		return paramId;
	}

	public void setParamId(Integer paramId) {
		this.paramId = paramId;
	}

	public String getParamsmtpserver() {
		return paramsmtpserver;
	}

	public void setParamsmtpserver(String paramsmtpserver) {
		this.paramsmtpserver = paramsmtpserver;
	}

	public String getParamsmtpuser() {
		return paramsmtpuser;
	}

	public void setParamsmtpuser(String paramsmtpuser) {
		this.paramsmtpuser = paramsmtpuser;
	}

	public String getParamsmtppassword() {
		return paramsmtppassword;
	}

	public void setParamsmtppassword(String paramsmtppassword) {
		this.paramsmtppassword = paramsmtppassword;
	}

	public String getParamsmtpport() {
		return paramsmtpport;
	}

	public void setParamsmtpport(String paramsmtpport) {
		this.paramsmtpport = paramsmtpport;
	}

	public String getParamadminuser() {
		return paramadminuser;
	}

	public void setParamadminuser(String paramadminuser) {
		this.paramadminuser = paramadminuser;
	}

	public String getParamrepmail() {
		return paramrepmail;
	}

	public void setParamrepmail(String paramrepmail) {
		this.paramrepmail = paramrepmail;
	}

	public String getParamreppj() {
		return paramreppj;
	}

	public void setParamreppj(String paramreppj) {
		this.paramreppj = paramreppj;
	}

	public String getParamaccueil() {
		return paramaccueil;
	}

	public void setParamaccueil(String paramaccueil) {
		this.paramaccueil = paramaccueil;
	}

	public String getParamconfid() {
		return paramconfid;
	}

	public void setParamconfid(String paramconfid) {
		this.paramconfid = paramconfid;
	}

	public int getParamverrou() {
		return paramverrou;
	}

	public void setParamverrou(int paramverrou) {
		this.paramverrou = paramverrou;
	}

	public boolean isParamlogdetail() {
		return paramlogdetail;
	}

	public void setParamlogdetail(boolean paramlogdetail) {
		this.paramlogdetail = paramlogdetail;
	}

	public String getElasticnode() {
		return elasticnode;
	}

	public void setElasticnode(String elasticnode) {
		this.elasticnode = elasticnode;
	}

	public String getElasticpath() {
		return elasticpath;
	}

	public void setElasticpath(String elasticpath) {
		this.elasticpath = elasticpath;
	}

	public String getTopmenu() {
		return topmenu;
	}

	public void setTopmenu(String topmenu) {
		this.topmenu = topmenu;
	}

	public String getServletneoged() {
		return servletneoged;
	}

	public void setServletneoged(String servletneoged) {
		this.servletneoged = servletneoged;
	}

	public String getPortneoged() {
		return portneoged;
	}

	public void setPortneoged(String portneoged) {
		this.portneoged = portneoged;
	}

	public String getBaseneoged() {
		return baseneoged;
	}

	public void setBaseneoged(String baseneoged) {
		this.baseneoged = baseneoged;
	}

	public String getNodeneoged() {
		return nodeneoged;
	}

	public void setNodeneoged(String nodeneoged) {
		this.nodeneoged = nodeneoged;
	}

	public String getPortavp() {
		return portavp;
	}

	public void setPortavp(String portavp) {
		this.portavp = portavp;
	}

	public String getDatabasename() {
		return databasename;
	}

	public void setDatabasename(String databasename) {
		this.databasename = databasename;
	}

	public String getIndexavp() {
		return indexavp;
	}

	public void setIndexavp(String indexavp) {
		this.indexavp = indexavp;
	}

	public int getMaxusers() {
		return maxusers;
	}

	public void setMaxusers(int maxusers) {
		this.maxusers = maxusers;
	}

	public boolean isLogread() {
		return logread;
	}

	public void setLogread(boolean logread) {
		this.logread = logread;
	}

	public String getSchemaneoged() {
		return schemaneoged;
	}

	public void setSchemaneoged(String schemaneoged) {
		this.schemaneoged = schemaneoged;
	}

	public String getElasticcluster() {
		return elasticcluster;
	}

	public void setElasticcluster(String elasticcluster) {
		this.elasticcluster = elasticcluster;
	}

	public boolean isCryptage() {
		return cryptage;
	}

	public void setCryptage(boolean cryptage) {
		this.cryptage = cryptage;
	}

	public boolean isMirror() {
		return mirror;
	}

	public void setMirror(boolean mirror) {
		this.mirror = mirror;
	}

	public String getMirroringurl() {
		return mirroringurl;
	}

	public void setMirroringurl(String mirroringurl) {
		this.mirroringurl = mirroringurl;
	}

	public boolean isElasticlogarchivage() {
		return elasticlogarchivage;
	}

	public void setElasticlogarchivage(boolean elasticlogarchivage) {
		this.elasticlogarchivage = elasticlogarchivage;
	}

	public boolean isElasticlogevent() {
		return elasticlogevent;
	}

	public void setElasticlogevent(boolean elasticlogevent) {
		this.elasticlogevent = elasticlogevent;
	}

	public boolean isPdfacheck() {
		return pdfacheck;
	}

	public void setPdfacheck(boolean pdfacheck) {
		this.pdfacheck = pdfacheck;
	}

	public int getPdfalevel() {
		return pdfalevel;
	}

	public void setPdfalevel(int pdfalevel) {
		this.pdfalevel = pdfalevel;
	}

	public String getStamptype() {
		return stamptype;
	}

	public void setStamptype(String stamptype) {
		this.stamptype = stamptype;
	}

	public boolean isExternaltimestamp() {
		return externaltimestamp;
	}

	public void setExternaltimestamp(boolean externaltimestamp) {
		this.externaltimestamp = externaltimestamp;
	}

	public String getArchivageserver() {
		return archivageserver;
	}

	public void setArchivageserver(String archivageserver) {
		this.archivageserver = archivageserver;
	}

	public String getUrlneoged() {
		return urlneoged;
	}

	public void setUrlneoged(String urlneoged) {
		this.urlneoged = urlneoged;
	}

	public int getPasswdlevel() {
		return passwdlevel;
	}

	public void setPasswdlevel(int passwdlevel) {
		this.passwdlevel = passwdlevel;
	}

	public String getNeogedserver() {
		return neogedserver;
	}

	public void setNeogedserver(String neogedserver) {
		this.neogedserver = neogedserver;
	}

	public boolean isUpdateged() {
		return updateged;
	}

	public void setUpdateged(boolean updateged) {
		this.updateged = updateged;
	}

	public String getInitnumber() {
		return initnumber;
	}

	public void setInitnumber(String initnumber) {
		this.initnumber = initnumber;
	}

	public String getOpenofficepath() {
		return openofficepath;
	}

	public void setOpenofficepath(String openofficepath) {
		this.openofficepath = openofficepath;
	}

	public int getMaxconvertsize() {
		return maxconvertsize;
	}

	public void setMaxconvertsize(int maxconvertsize) {
		this.maxconvertsize = maxconvertsize;
	}

	public boolean isArchivage_doublon() {
		return archivage_doublon;
	}

	public void setArchivage_doublon(boolean archivage_doublon) {
		this.archivage_doublon = archivage_doublon;
	}

	public UUID getCryptageid() {
		return cryptageid;
	}

	public void setCryptageid(UUID cryptageid) {
		this.cryptageid = cryptageid;
	}

	public StorageParam getMasterStorageParam() {
		return masterStorageParam;
	}

	public void setMasterStorageParam(StorageParam masterStorageParam) {
		this.masterStorageParam = masterStorageParam;
	}

	public StorageParam getMirrorStorageParam() {
		return mirrorStorageParam;
	}

	public void setMirrorStorageParam(StorageParam mirrorStorageParam) {
		this.mirrorStorageParam = mirrorStorageParam;
	}
}
