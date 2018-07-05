package com.telino.avp.entity.auxil;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.telino.avp.entity.context.User;

@Entity
@Table(name = "exp_comments")
public class ExpComment {

	@Id
	private UUID comId;

	private ZonedDateTime comDate;

	@ManyToOne
	@JoinColumn(name = "task_id")
	private ExpTask task;

	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;

	private String comment;

	public ExpComment() {
		super();
	}

	public UUID getComId() {
		return comId;
	}

	public void setComId(UUID comId) {
		this.comId = comId;
	}

	public ZonedDateTime getComDate() {
		return comDate;
	}

	public void setComDate(ZonedDateTime comDate) {
		this.comDate = comDate;
	}

	public ExpTask getTask() {
		return task;
	}

	public void setTask(ExpTask task) {
		this.task = task;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User login) {
		this.user = login;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "ExpComments [comId=" + comId + ", comDate=" + comDate + ", userId=" + user.getUserId() + ", comment=" + comment
				+ "]";
	}
}
