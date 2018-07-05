package com.telino.avp.entity.archive;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "secret_key")
public class EncryptionKey {

	@Id
	@Column(name = "keyid")
	private UUID keyId;

	private String algorythm;
	private byte[] encodedkey;

	public EncryptionKey() {
		super();
	}

	public UUID getKeyId() {
		return keyId;
	}

	public void setKeyId(UUID keyId) {
		this.keyId = keyId;
	}

	public String getAlgorythm() {
		return algorythm;
	}

	public void setAlgorythm(String algorythm) {
		this.algorythm = algorythm;
	}

	public byte[] getEncodedkey() {
		return encodedkey;
	}

	public void setEncodedkey(byte[] encodedkey) {
		this.encodedkey = encodedkey;
	}

}
