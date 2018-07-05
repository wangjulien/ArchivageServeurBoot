package com.telino.avp.entity.context;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "doctypes", uniqueConstraints = @UniqueConstraint(columnNames = { "doctype_archivage", "categorie" }))
public class DocType {

	@Id
	private Integer docTypeId;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "doctype_archivage")
	private Type docTypeArchivage;

	private String categorie;

	private String keywordsList;

	@ManyToOne
	@JoinColumn(name = "par_id")
	private Profile profile;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "mime_doctypes", joinColumns = @JoinColumn(name = "doctypeid"), inverseJoinColumns = @JoinColumn(name = "mime_type_id"))
	private Set<MimeType> mimeTypes = new HashSet<>();

	public DocType() {
		super();
	}

	public Integer getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Integer docTypeId) {
		this.docTypeId = docTypeId;
	}

	public Type getDocTypeArchivage() {
		return docTypeArchivage;
	}

	public void setDocTypeArchivage(Type docTypeArchivage) {
		this.docTypeArchivage = docTypeArchivage;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public String getKeywordsList() {
		return keywordsList;
	}

	public void setKeywordsList(String keywordsList) {
		this.keywordsList = keywordsList;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Set<MimeType> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Set<MimeType> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public void addMimeType(MimeType mimeType) {
		this.mimeTypes.add(mimeType);
		mimeType.addDocType(this);
	}
}
