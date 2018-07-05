package com.telino.avp.entity.param;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "paramstorage")
public class StorageParam {

	@Id
	private Integer paramId;

	@Column(name = "type_storage")
	private String typeStorage;

	private String remoteOrLocal;

	private String hostName;

	private String port;

	private String servlet;

	private String directory;

	@Column(name = "storageid")
	private String idStorage;

	public StorageParam() {
		super();
	}

	public Integer getParamId() {
		return paramId;
	}

	public void setParamId(Integer paramId) {
		this.paramId = paramId;
	}

	public String getTypeStorage() {
		return typeStorage;
	}

	public void setTypeStorage(String typeStorage) {
		this.typeStorage = typeStorage;
	}

	public String getRemoteOrLocal() {
		return remoteOrLocal;
	}

	public void setRemoteOrLocal(String remoteOrLocal) {
		this.remoteOrLocal = remoteOrLocal;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getServlet() {
		return servlet;
	}

	public void setServlet(String servlet) {
		this.servlet = servlet;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getIdStorage() {
		return idStorage;
	}

	public void setIdStorage(String idStorage) {
		this.idStorage = idStorage;
	}
}
