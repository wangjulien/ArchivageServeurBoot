package com.telino.avp.entity.auxil;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.context.User;

/**
 * log_event qui enregistre tous les operation sur une archive
 * 
 * @author jwang
 *
 */

@Entity
@Table(name = "log_archive")
@NamedNativeQueries({
		@NamedNativeQuery(name = "LogArchive.findAllLogArchiveByTimestampForContent", query = "select a.* from log_archive a where a.timestamp >= "
				+ "(select b.timestamp from log_archive b where b.logtype = :arcTypeS and b.timestamp < :timestamp order by a.timestamp desc limit 1)"
				+ " and a.timestamp < :timestamp", resultClass = LogArchive.class),
		@NamedNativeQuery(name = "LogArchive.findLogArchiveForDocId", query = "select b.* from log_archive b where b.timestamp > "
				+ "(select a.timestamp from log_archive a where a.docid = :docid and a.logtype = :arcTypeA order by a.timestamp asc limit 1) "
				+ " and b.logtype = :arcTypeS order by b.timestamp asc limit 1", resultClass = LogArchive.class),
		@NamedNativeQuery(name = "LogArchive.findHashForDocId", query = "select a.hash from log_archive a "
				+ "where a.docid = :docid and a.logtype = :arcTypeA and a.hash is not null order by a.timestamp asc limit 1") 
		})
public class LogArchive extends Journal {

	@Id
	@Column(name = "logid")
	private UUID logId;
	
	@ManyToOne
	@JoinColumn(name = "docid")
	private Document document;

	@Column(name = "docsname")
	private String docsName;
	
	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;

	@Column(name = "mailid")
	private String mailId;

	@OneToOne
	@JoinColumn(name = "attestation")
	private Document attestation;

	private String operation;

	public LogArchive() {
		super();
	}

	public UUID getLogId() {
		return logId;
	}

	public void setLogId(UUID logId) {
		this.logId = logId;
	}

	public Document getDocument() {
		return document;
	}
	
	public UUID getDocumentId() {
		return Objects.isNull(document) ? null : document.getDocId();
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}

	public String getDocsName() {
		return docsName;
	}

	public void setDocsName(String docsName) {
		this.docsName = docsName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public Document getAttestation() {
		return attestation;
	}
	
	public UUID getAttestationId() {
		return Objects.isNull(attestation) ? null : attestation.getDocId();
	}

	public void setAttestation(Document attestation) {
		this.attestation = attestation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String buildContent() {
		StringBuilder sb = new StringBuilder();
		sb.append(logId.toString());
		sb.append(horodatage.toString());
		sb.append(operation);
		sb.append(logType);
		sb.append(Objects.isNull(attestation) ? "" : attestation.getDocId().toString());
		sb.append(Objects.isNull(document) ? "" : document.getDocId().toString());
		sb.append(hash);
		sb.append(Objects.isNull(timestampTokenBytes) ? "" : timestampTokenBytes.toString());

		return sb.toString();
	}
}
