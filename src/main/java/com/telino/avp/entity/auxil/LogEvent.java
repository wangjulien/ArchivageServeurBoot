package com.telino.avp.entity.auxil;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.telino.avp.entity.archive.Document;
import com.telino.avp.protocol.DbEntityProtocol.LogEventState;

/**
 * log_event qui enregistre tous les evements : operation, action ou exception
 * Les Queries utilises par Dao master et mirror sont declares ici avec
 * NameQueries
 * 
 * @author jwang
 *
 */
@Entity
@Table(name = "log_event")
@NamedNativeQueries({
		@NamedNativeQuery(name = "LogEvent.findAllLogEventByTimestampForContent", query = "select a.* from log_event a where a.timestamp >= "
				+ "(select b.timestamp from log_event b where b.logtype = :evtTypeS and b.timestamp < :timestamp order by b.timestamp desc limit 1)"
				+ " and a.timestamp < :timestamp order by a.timestamp desc", resultClass = LogEvent.class) })
public class LogEvent extends Journal {

	@Id
	@Column(name = "logid")
	private UUID logId;

	@ManyToOne
	@JoinColumn(name = "archiveid")
	private Document archive;

	@OneToOne
	@JoinColumn(name = "journalid")
	private LogArchive logArchive;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "versionprocessus")
	private String versionProcessus;

	private String origin;
	private String processus;
	private String action;
	private String detail;
	private String operateur;
	private String trace;
	private String methode;

	@OneToOne
	@JoinColumn(name = "journalxmlid")
	private Document journalXml;

	@Enumerated(EnumType.STRING)
	private LogEventState statExp = LogEventState.I;

	public LogEvent() {
		super();
	}

	public UUID getLogId() {
		return logId;
	}

	public void setLogId(UUID logId) {
		this.logId = logId;
	}

	public Document getArchive() {
		return archive;
	}
	
	public UUID getArchiveId() {
		return Objects.isNull(archive) ? null : archive.getDocId();
	}

	public void setArchive(Document archive) {
		this.archive = archive;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getVersionProcessus() {
		return versionProcessus;
	}

	public void setVersionProcessus(String versionProcessus) {
		this.versionProcessus = versionProcessus;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getProcessus() {
		return processus;
	}

	public void setProcessus(String processus) {
		this.processus = processus;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getOperateur() {
		return operateur;
	}

	public void setOperateur(String operateur) {
		this.operateur = operateur;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	public String getMethode() {
		return methode;
	}

	public void setMethode(String methode) {
		this.methode = methode;
	}

	public Document getJournalXml() {
		return journalXml;
	}

	public void setJournalXml(Document journalXml) {
		this.journalXml = journalXml;
	}

	public LogEventState getStatExp() {
		return statExp;
	}

	public void setStatExp(LogEventState statExp) {
		this.statExp = statExp;
	}

	public LogArchive getLogArchive() {
		return logArchive;
	}

	public UUID getJournalId() {
		return Objects.isNull(logArchive) ? null : logArchive.getLogId();
	}

	public void setLogArchive(LogArchive logArchive) {
		this.logArchive = logArchive;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String buildContent() {

		StringBuilder sb = new StringBuilder();
		sb.append(logId.toString());
		sb.append(origin);
		sb.append(processus);
		sb.append(action);
		sb.append(detail);
		sb.append(horodatage.toString());
		sb.append(operateur);
		sb.append(versionProcessus);
		sb.append(logType);
		sb.append(Objects.isNull(timestampTokenBytes) ? "" : timestampTokenBytes.toString());

		return sb.toString();
	}
}
