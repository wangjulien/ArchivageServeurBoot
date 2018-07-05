package com.telino.avp.entity.archive;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * Empreinte d'un document
 * 
 * @author jwang
 */
@Entity
@Table(name = "empreintes")
public class Empreinte {

	@Id
	private UUID id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "docid")
	private Document document;

	@Column(name = "empreinte")
	private String empreinte;

	@Column(name = "empreinte_algo")
	private String algorythme;

	@Column(name = "empreinte_unique")
	private String empreinteUnique;

	@Column(name = "empreinte_telino")
	private String empreinteInterne;

	public Empreinte() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getEmpreinte() {
		return empreinte;
	}

	public void setEmpreinte(String empreinte) {
		this.empreinte = empreinte;
	}

	public String getAlgorythme() {
		return algorythme;
	}

	public void setAlgorythme(String algorythme) {
		this.algorythme = algorythme;
	}

	public String getEmpreinteUnique() {
		return empreinteUnique;
	}

	public void setEmpreinteUnique(String empreinteUnique) {
		this.empreinteUnique = empreinteUnique;
	}

	public String getEmpreinteInterne() {
		return empreinteInterne;
	}

	public void setEmpreinteInterne(String empreinteInterne) {
		this.empreinteInterne = empreinteInterne;
	}
}
