package com.telino.avp.entitysyst;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "syst_initpassword")
public class SystInitPassword {

	@Id
	private Integer passwordId;

	private String hash;

	public SystInitPassword() {
		super();
	}

	public Integer getPasswordId() {
		return passwordId;
	}

	public void setPasswordId(Integer passwordId) {
		this.passwordId = passwordId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
