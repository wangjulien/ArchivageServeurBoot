package com.telino.avp.entity.context;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mime_type")
public class MimeType {
	
	@Id
	@Column(name = "mime_type_id")
	private Integer mimeTypeId;
	
	@Column(name = "content_type")
	private String contentType;
	
	@Column(name = "mime_description")
	private String mimeDescription;
	
	@ManyToMany(mappedBy = "mimeTypes")
	private Set<DocType> docTypes = new HashSet<>();

	public MimeType() {
		super();
	}

	public Integer getMimeTypeId() {
		return mimeTypeId;
	}

	public void setMimeTypeId(Integer mimeTypeId) {
		this.mimeTypeId = mimeTypeId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMimeDescription() {
		return mimeDescription;
	}

	public void setMimeDescription(String mimeDescription) {
		this.mimeDescription = mimeDescription;
	}

	public Set<DocType> getDocTypes() {
		return docTypes;
	}

	public void setDocTypes(Set<DocType> docTypes) {
		this.docTypes = docTypes;
	}

	public void addDocType(DocType docType) {
		this.docTypes.add(docType);		
	}	
}
