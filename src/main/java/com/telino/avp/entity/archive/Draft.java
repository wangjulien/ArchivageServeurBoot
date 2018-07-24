package com.telino.avp.entity.archive;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jwang
 *
 */
@Entity
@Table(name = "drafts")
public class Draft {

	@Id
	@Column(name = "docid")
	private UUID docId;

	private String doctype;
	private String categorie;
	private String keywords;
	private byte[] content;

	@Column(name = "content_length")
	private Integer contentLength;

	@Column(name = "content_type")
	private String contentType;

	private String domaineowner;
	private String organisationversante;

	private ZonedDateTime docsdate;
	private String description;
	private String title;

	@Column(name = "domnnom")
	private String domnNom;

	private String mailowner;
	private Boolean transmis;
	
	private String statut;
	
	private String motif;
	private String userid;
	private ZonedDateTime draftdate;

	@Column(name = "archiveid")
	private UUID archiveId;

	@Column(name = "pronom_type")
	private String pronomType;

	@Column(name = "pronom_id")
	private String pronomId;

	public Draft() {
		super();
	}

	public UUID getDocId() {
		return docId;
	}

	public void setDocId(UUID docId) {
		this.docId = docId;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public Integer getContentLength() {
		return contentLength;
	}

	public void setContentLength(Integer contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDomaineowner() {
		return domaineowner;
	}

	public void setDomaineowner(String domaineowner) {
		this.domaineowner = domaineowner;
	}

	public String getOrganisationversante() {
		return organisationversante;
	}

	public void setOrganisationversante(String organisationversante) {
		this.organisationversante = organisationversante;
	}

	public ZonedDateTime getDocsdate() {
		return Objects.isNull(docsdate) ? ZonedDateTime.now() : docsdate;
	}

	public void setDocsdate(ZonedDateTime docsdate) {
		this.docsdate = docsdate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDomnNom() {
		return domnNom;
	}

	public void setDomnNom(String domnNom) {
		this.domnNom = domnNom;
	}

	public String getMailowner() {
		return mailowner;
	}

	public void setMailowner(String mailowner) {
		this.mailowner = mailowner;
	}

	public Boolean getTransmis() {
		return transmis;
	}

	public void setTransmis(Boolean transmis) {
		this.transmis = transmis;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public ZonedDateTime getDraftdate() {
		return Objects.isNull(draftdate) ? ZonedDateTime.now() : draftdate;
	}

	public void setDraftdate(ZonedDateTime draftdate) {
		this.draftdate = draftdate;
	}
	
	public UUID getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(UUID archiveId) {
		this.archiveId = archiveId;
	}

	public String getPronomType() {
		return pronomType;
	}

	public void setPronomType(String pronomType) {
		this.pronomType = pronomType;
	}

	public String getPronomId() {
		return pronomId;
	}

	public void setPronomId(String pronomId) {
		this.pronomId = pronomId;
	}
}
