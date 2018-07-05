package com.telino.avp.entity.param;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "applications")
public class Application {

	@Id
	private String applicationCode;

	private String applicationName;
	private boolean applicationValidation;

	public Application() {
		super();
	}

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public boolean isApplicationValidation() {
		return applicationValidation;
	}

	public void setApplicationValidation(boolean applicationValidation) {
		this.applicationValidation = applicationValidation;
	}
}
