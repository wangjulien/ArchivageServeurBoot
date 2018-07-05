package com.telino.avp.entity.context;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "types")
public class Type {

	@Id
	@Column(name = "doctype_archivage")
	private String docTypeArchivage;

	private String docTypeLib;

	public Type() {
		super();
	}

	public String getDocTypeArchivage() {
		return docTypeArchivage;
	}

	public void setDocTypeArchivage(String docTypeArchivage) {
		this.docTypeArchivage = docTypeArchivage;
	}

	public String getDocTypeLib() {
		return docTypeLib;
	}

	public void setDocTypeLib(String docTypeLib) {
		this.docTypeLib = docTypeLib;
	}
}
