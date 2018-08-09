package com.telino.avp.entity.archive;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "restitutionlist")
public class RestitutionList {

	@EmbeddedId
	private RestitutionListId id;

	@MapsId("restitutionId")
	@ManyToOne
	@JoinColumn(name = "restitutionid")
	private Restitution restitution;

	@MapsId("docId")
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "docid")
	private Document document;

	private boolean restitue;

	private String title;

	public RestitutionList() {
		super();
		this.id = new RestitutionListId();
	}

	public RestitutionListId getId() {
		return id;
	}

	public void setId(RestitutionListId id) {
		this.id = id;
	}

	public Restitution getRestitution() {
		return restitution;
	}

	public void setRestitution(Restitution restitution) {
		this.restitution = restitution;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public boolean isRestitue() {
		return restitue;
	}

	public void setRestitue(boolean communique) {
		this.restitue = communique;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
