package com.telino.avp.entitysyst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "syst_env")
public class SystEnv {

	@Id
	@Column(name = "envid")
	private Integer envId;

	private String environnement;
	private String nombase;
	private String host;
	private String port;
	private String servlet;
	private String nombaseparam;
	private String application;
	private String applicationtitle;
	private String applicationlogo;
	private String externallogin;
	private String casadress;
	private String welcomepage;
	private String applicationprofile;

	@Column(name = "bgs_on")
	private boolean bgsOn;

	private int sessiontimeout;
	private String iconpath;
	private int buttonwidth;
	private boolean flatmode;
	private String parametres;

	public SystEnv() {
		super();
	}

	public Integer getEnvId() {
		return envId;
	}

	public void setEnvId(Integer envId) {
		this.envId = envId;
	}

	public String getEnvironnement() {
		return environnement;
	}

	public void setEnvironnement(String environnement) {
		this.environnement = environnement;
	}

	public String getNombase() {
		return nombase;
	}

	public void setNombase(String nombase) {
		this.nombase = nombase;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getNombaseparam() {
		return nombaseparam;
	}

	public void setNombaseparam(String nombaseparam) {
		this.nombaseparam = nombaseparam;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationtitle() {
		return applicationtitle;
	}

	public void setApplicationtitle(String applicationtitle) {
		this.applicationtitle = applicationtitle;
	}

	public String getApplicationlogo() {
		return applicationlogo;
	}

	public void setApplicationlogo(String applicationlogo) {
		this.applicationlogo = applicationlogo;
	}

	public String getExternallogin() {
		return externallogin;
	}

	public void setExternallogin(String externallogin) {
		this.externallogin = externallogin;
	}

	public String getCasadress() {
		return casadress;
	}

	public void setCasadress(String casadress) {
		this.casadress = casadress;
	}

	public String getWelcomepage() {
		return welcomepage;
	}

	public void setWelcomepage(String welcomepage) {
		this.welcomepage = welcomepage;
	}

	public String getApplicationprofile() {
		return applicationprofile;
	}

	public void setApplicationprofile(String applicationprofile) {
		this.applicationprofile = applicationprofile;
	}

	public boolean isBgsOn() {
		return bgsOn;
	}

	public void setBgsOn(boolean bgsOn) {
		this.bgsOn = bgsOn;
	}

	public int getSessiontimeout() {
		return sessiontimeout;
	}

	public void setSessiontimeout(int sessiontimeout) {
		this.sessiontimeout = sessiontimeout;
	}

	public String getIconpath() {
		return iconpath;
	}

	public void setIconpath(String iconpath) {
		this.iconpath = iconpath;
	}

	public int getButtonwidth() {
		return buttonwidth;
	}

	public void setButtonwidth(int buttonwidth) {
		this.buttonwidth = buttonwidth;
	}

	public boolean isFlatmode() {
		return flatmode;
	}

	public void setFlatmode(boolean flatmode) {
		this.flatmode = flatmode;
	}

	public String getParametres() {
		return parametres;
	}

	public void setParametres(String parametres) {
		this.parametres = parametres;
	}
}
