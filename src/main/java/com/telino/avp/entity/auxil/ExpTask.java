package com.telino.avp.entity.auxil;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.context.User;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskState;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskType;

@Entity
@Table(name = "exp_task")
public class ExpTask {

	@Id
	private UUID taskId;

	private ZonedDateTime horodatage;
	private ZonedDateTime dateDeb;
	private ZonedDateTime dateFin;

	private Long taskTypeId;

	@ManyToOne
	@JoinColumn(name = "docid")
	private Document document;

	@ManyToOne
	@JoinColumn(name = "logid")
	private LogArchive logArchive;

	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;
	
	private int nbTries;

	@Enumerated(EnumType.STRING)
	private ExpTaskState state;

	@OneToMany(mappedBy = "task", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private Set<ExpComment> comments = new HashSet<>();

	public ExpTask() {
		super();
		this.state = ExpTaskState.I;
	}

	public UUID getTaskId() {
		return taskId;
	}

	public void setTaskId(UUID taskId) {
		this.taskId = taskId;
	}

	public ZonedDateTime getHorodatage() {
		return horodatage;
	}

	public void setHorodatage(ZonedDateTime horodatage) {
		this.horodatage = horodatage;
	}

	public ZonedDateTime getDateDeb() {
		return dateDeb;
	}

	public void setDateDeb(ZonedDateTime dateDeb) {
		this.dateDeb = dateDeb;
	}

	public ZonedDateTime getDateFin() {
		return dateFin;
	}

	public void setDateFin(ZonedDateTime dateFin) {
		this.dateFin = dateFin;
	}

	public ExpTaskType getTaskType() {
		return ExpTaskType.getType(taskTypeId);
	}

	public void setTaskType(ExpTaskType taskType) {
		if (taskType == null) {
			this.taskTypeId = null;
		} else {
			this.taskTypeId = taskType.getTypeId();
		}
	}

	public Long getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(Long taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public LogArchive getJournal() {
		return logArchive;
	}

	public void setJournal(LogArchive logArchive) {
		this.logArchive = logArchive;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getNbTries() {
		return nbTries;
	}

	public void setNbTries(int nbTries) {
		this.nbTries = nbTries;
	}

	public ExpTaskState getState() {
		return state;
	}

	public void setState(ExpTaskState state) {
		this.state = state;
	}

	public Set<ExpComment> getComments() {
		return comments;
	}

	public void setComments(Set<ExpComment> comments) {
		this.comments = comments;
	}

	public void addComment(ExpComment c) {
		c.setTask(this);
		this.comments.add(c);
	}

	@Override
	public String toString() {
		return "ExpTask [taskId=" + taskId + ", horodatage=" + horodatage + ", dateDeb=" + dateDeb + ", dateFin="
				+ dateFin + ", taskTypeId=" + taskTypeId + ", docId=" + document.getDocId().toString() + ", logId="
				+ logArchive.getLogId() + ", userId=" + user.getUserId() + ", nbTries=" + nbTries + ", state=" + state
				+ ", comments=" + comments + "]";
	}
}
