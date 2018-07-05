package com.telino.avp.entity.archive;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "chiffrement")
public class Chiffrement {
	
	@Id
	private UUID cryptId;
	
	private String algorythm; 
	
	@OneToOne
	@JoinColumn(name = "idcrypkey")
	private EncryptionKey encryptionKey;
	
	
	public Chiffrement() {
		super();
	}

	public UUID getCryptId() {
		return cryptId;
	}

	public void setCryptId(UUID cryptId) {
		this.cryptId = cryptId;
	}

	public String getAlgorythm() {
		return algorythm;
	}

	public void setAlgorythm(String algorythm) {
		this.algorythm = algorythm;
	}

	public EncryptionKey getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(EncryptionKey encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
}
