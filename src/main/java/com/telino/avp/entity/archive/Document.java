package com.telino.avp.entity.archive;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.telino.avp.entity.context.Profile;

/**
 * Document Entity : a etre remplace par Document2
 * 
 * @author jwang
 *
 */
@Entity
@Table(name = "document")
@SqlResultSetMapping(name = "DocumentResult", classes = { @ConstructorResult(targetClass = Document.class, columns = {
		@ColumnResult(name = "docid", type = UUID.class), @ColumnResult(name = "title", type = String.class) }) })
public class Document {

	@Id
	@Column(name = "docid")
	private UUID docId;

	// Timestamp for chrono order purpose
	private ZonedDateTime timestamp;

	private String title;
	private ZonedDateTime date;

	@Column(name = "archiver_id")
	private String archiverId;

	@Column(name = "content_length")
	private Integer contentLength;

	@Column(name = "content_type")
	private String contentType;

	private String keywords;
	private String doctype;

	@Column(name = "archive_date")
	private ZonedDateTime archiveDate;

	private String application;
	private String idsource;
	private String categorie;

	@Column(name = "archive_end")
	private ZonedDateTime archiveEnd;

	private String author;
	private String mailowner;
	private String domaineowner;

	@Column(name = "archiver_mail")
	private String archiverMail;

	@ManyToOne
	@JoinColumn(name = "par_id")
	private Profile profile;

	private String elasticid;

	private byte[] content;

	@Column(name = "domnnom")
	private String domnNom;

	private String conteneur;
	private String lot;

	@ManyToOne
	@JoinColumn(name = "iddepot")
	private Depot depot;

	private String serviceverseur;
	private String description;
	private Boolean cryptage;

	@Column(name = "cryptage_algo")
	private String cryptageAlgo;

	private String organisationverseuse;
	private String organisationversante;

	private Boolean logicaldelete;
	private ZonedDateTime logicaldeletedate;

	private String md5;

	@ManyToOne
	@JoinColumn(name = "cryptage_algoid")
	private Chiffrement chiffrement;

	@Column(name = "cryptage_iv")
	private byte[] cryptageIv;

	private int statut;

	@Column(name = "pronom_type")
	private String pronomType;

	@Column(name = "pronom_id")
	private String pronomId;

	@OneToOne(mappedBy = "document", cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
	private Empreinte empreinte;

	public Document() {
		super();
	}

	public Document(UUID docId, String title) {
		super();
		this.docId = docId;
		this.title = title;
	}

	public UUID getDocId() {
		return docId;
	}

	public void setDocId(UUID docId) {
		this.docId = docId;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ZonedDateTime getDate() {
		return Objects.isNull(date) ? ZonedDateTime.now() : date;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	public String getArchiverId() {
		return archiverId;
	}

	public void setArchiverId(String archiverId) {
		this.archiverId = archiverId;
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

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public ZonedDateTime getArchiveDate() {
		return Objects.isNull(archiveDate) ? ZonedDateTime.now() : archiveDate;
	}

	public void setArchiveDate(ZonedDateTime archiveDate) {
		this.archiveDate = archiveDate;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getIdsource() {
		return idsource;
	}

	public void setIdsource(String idsource) {
		this.idsource = idsource;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public ZonedDateTime getArchiveEnd() {
		return Objects.isNull(archiveEnd) ? ZonedDateTime.now() : archiveEnd;
	}

	public void setArchiveEnd(ZonedDateTime archiveEnd) {
		this.archiveEnd = archiveEnd;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMailowner() {
		return mailowner;
	}

	public void setMailowner(String mailowner) {
		this.mailowner = mailowner;
	}

	public String getDomaineowner() {
		return domaineowner;
	}

	public void setDomaineowner(String domaineowner) {
		this.domaineowner = domaineowner;
	}

	public String getArchiverMail() {
		return archiverMail;
	}

	public void setArchiverMail(String archiverMail) {
		this.archiverMail = archiverMail;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getElasticid() {
		return elasticid;
	}

	public void setElasticid(String elasticid) {
		this.elasticid = elasticid;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getDomnNom() {
		return domnNom;
	}

	public void setDomnNom(String domnNom) {
		this.domnNom = domnNom;
	}

	public String getConteneur() {
		return conteneur;
	}

	public void setConteneur(String conteneur) {
		this.conteneur = conteneur;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public Depot getDepot() {
		return depot;
	}

	public void setDepot(Depot depot) {
		this.depot = depot;
	}

	public String getServiceverseur() {
		return serviceverseur;
	}

	public void setServiceverseur(String serviceverseur) {
		this.serviceverseur = serviceverseur;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getCryptage() {
		return cryptage;
	}

	public void setCryptage(Boolean cryptage) {
		this.cryptage = cryptage;
	}

	public String getCryptageAlgo() {
		return cryptageAlgo;
	}

	public void setCryptageAlgo(String cryptageAlgo) {
		this.cryptageAlgo = cryptageAlgo;
	}

	public String getOrganisationverseuse() {
		return organisationverseuse;
	}

	public void setOrganisationverseuse(String organisationverseuse) {
		this.organisationverseuse = organisationverseuse;
	}

	public String getOrganisationversante() {
		return organisationversante;
	}

	public void setOrganisationversante(String organisationversante) {
		this.organisationversante = organisationversante;
	}

	public Boolean getLogicaldelete() {
		return logicaldelete;
	}

	public void setLogicaldelete(Boolean logicaldelete) {
		this.logicaldelete = logicaldelete;
	}

	public ZonedDateTime getLogicaldeletedate() {
		return logicaldeletedate;
	}

	public void setLogicaldeletedate(ZonedDateTime logicaldeletedate) {
		this.logicaldeletedate = logicaldeletedate;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Chiffrement getChiffrement() {
		return chiffrement;
	}

	public void setChiffrement(Chiffrement chiffrement) {
		this.chiffrement = chiffrement;
	}

	public byte[] getCryptageIv() {
		return cryptageIv;
	}

	public void setCryptageIv(byte[] cryptageIv) {
		this.cryptageIv = cryptageIv;
	}

	public int getStatut() {
		return statut;
	}

	public void setStatut(int statut) {
		this.statut = statut;
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

	public Empreinte getEmpreinte() {
		return empreinte;
	}

	public void setEmpreinte(Empreinte empreinte) {
		this.empreinte = empreinte;
		empreinte.setDocument(this);
	}

}
