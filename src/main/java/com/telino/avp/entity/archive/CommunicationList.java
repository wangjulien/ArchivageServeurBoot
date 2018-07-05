package com.telino.avp.entity.archive;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "communicationlist")
public class CommunicationList {

	@EmbeddedId
	private CommunicationListId id;

	@MapsId("communicationId")
	@ManyToOne
	@JoinColumn(name = "communicationid")
	private Communication communication;

	@MapsId("docId")
	@ManyToOne
	@JoinColumn(name = "docid")
	private Document document;

	private boolean communique;

	private String title;

	public CommunicationList() {
		super();
		this.id = new CommunicationListId();
	}

	public CommunicationListId getId() {
		return id;
	}

	public void setId(CommunicationListId id) {
		this.id = id;
	}

	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public boolean isCommunique() {
		return communique;
	}

	public void setCommunique(boolean communique) {
		this.communique = communique;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
