package com.telino.avp.entity.param;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "param_automate")
public class ParamAutomate {

	@Id
	@Column(name = "paramid")
	private Integer paramId;

	private String hostname;
	private String port;
	private String servlet;

	public ParamAutomate() {
		super();
	}

	public Integer getParamId() {
		return paramId;
	}

	public void setParamId(Integer paramId) {
		this.paramId = paramId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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
}
