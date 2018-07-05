package com.telino.avp.entity.param;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bgservices")
public class BgService {

	@Id
	@Column(name = "bgs_cod")
	private String bgsCod;
	
	@Column(name = "bgs_descr")
	private String bgsDescr;
	
	@Column(name = "bgs_on")
	private boolean bgsOn;

	@Column(name = "bgs_param")
	private String bgsParam;
	
	@Column(name = "bgs_start")
	private ZonedDateTime bgsStart;
	
	@Column(name = "bgs_process")
	private String bgsProcess;

	@Column(name = "bgs_encours")
	private boolean bgsEncours;

	public BgService() {
		super();
	}

	public String getBgsCod() {
		return bgsCod;
	}

	public void setBgsCod(String bgsCod) {
		this.bgsCod = bgsCod;
	}

	public String getBgsDescr() {
		return bgsDescr;
	}

	public void setBgsDescr(String bgsDescr) {
		this.bgsDescr = bgsDescr;
	}

	public boolean isBgsOn() {
		return bgsOn;
	}

	public void setBgsOn(boolean bgsOn) {
		this.bgsOn = bgsOn;
	}

	public String getBgsParam() {
		return bgsParam;
	}

	public void setBgsParam(String bgsParam) {
		this.bgsParam = bgsParam;
	}

	public ZonedDateTime getBgsStart() {
		return bgsStart;
	}

	public void setBgsStart(ZonedDateTime bgsStart) {
		this.bgsStart = bgsStart;
	}

	public String getBgsProcess() {
		return bgsProcess;
	}

	public void setBgsProcess(String bgsProcess) {
		this.bgsProcess = bgsProcess;
	}

	public boolean isBgsEncours() {
		return bgsEncours;
	}

	public void setBgsEncours(boolean bgsEncours) {
		this.bgsEncours = bgsEncours;
	}
}
