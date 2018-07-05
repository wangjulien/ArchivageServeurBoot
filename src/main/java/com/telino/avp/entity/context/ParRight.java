package com.telino.avp.entity.context;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "par_rights")
public class ParRight {

	@EmbeddedId
	private ParRightId id;

	@MapsId("parId")
	@ManyToOne
	@JoinColumn(name = "par_id")
	private Profile profile;

	@MapsId("userId")
	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;

	@Column(name = "par_candeposit")
	private boolean parCanDeposit;

	@Column(name = "par_candelay")
	private boolean parCanDelay;

	@Column(name = "par_candestroy")
	private boolean parCanDestroy;

	@Column(name = "par_canmodprof")
	private boolean parCanModProf;

	@Column(name = "par_canread")
	private boolean parCanRead;

	@Column(name = "can_communicate")
	private boolean canCommunicate;

	@Column(name = "can_restitute")
	private boolean canRestitute;

	@Column(name = "par_cancommunicate")
	private boolean parCanCommunicate;

	@Column(name = "par_canrestitute")
	private boolean parCanRestitute;

	public ParRight() {
		super();
		this.id = new ParRightId();
	}

	public ParRightId getId() {
		return id;
	}

	public void setId(ParRightId id) {
		this.id = id;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User login) {
		this.user = login;
	}

	public boolean isParCanDeposit() {
		return parCanDeposit;
	}

	public void setParCanDeposit(boolean parCanDeposit) {
		this.parCanDeposit = parCanDeposit;
	}

	public boolean isParCanDelay() {
		return parCanDelay;
	}

	public void setParCanDelay(boolean parCanDelay) {
		this.parCanDelay = parCanDelay;
	}

	public boolean isParCanDestroy() {
		return parCanDestroy;
	}

	public void setParCanDestroy(boolean parCanDestroy) {
		this.parCanDestroy = parCanDestroy;
	}

	public boolean isParCanModProf() {
		return parCanModProf;
	}

	public void setParCanModProf(boolean parCanModProf) {
		this.parCanModProf = parCanModProf;
	}

	public boolean isParCanRead() {
		return parCanRead;
	}

	public void setParCanRead(boolean parCanRead) {
		this.parCanRead = parCanRead;
	}

	public boolean isCanCommunicate() {
		return canCommunicate;
	}

	public void setCanCommunicate(boolean canCommunicate) {
		this.canCommunicate = canCommunicate;
	}

	public boolean isCanRestitute() {
		return canRestitute;
	}

	public void setCanRestitute(boolean canRestitute) {
		this.canRestitute = canRestitute;
	}

	public boolean isParCanCommunicate() {
		return parCanCommunicate;
	}

	public void setParCanCommunicate(boolean parCanCommunicate) {
		this.parCanCommunicate = parCanCommunicate;
	}

	public boolean isParCanRestitute() {
		return parCanRestitute;
	}

	public void setParCanRestitute(boolean parCanRestitute) {
		this.parCanRestitute = parCanRestitute;
	}
}
