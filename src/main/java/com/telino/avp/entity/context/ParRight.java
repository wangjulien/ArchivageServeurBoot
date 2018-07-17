package com.telino.avp.entity.context;

import java.util.Objects;

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
	private Boolean parCanDeposit;

	@Column(name = "par_candelay")
	private Boolean parCanDelay;

	@Column(name = "par_candestroy")
	private Boolean parCanDestroy;

	@Column(name = "par_canmodprof")
	private Boolean parCanModProf;

	@Column(name = "par_canread")
	private Boolean parCanRead;

	@Column(name = "can_communicate")
	private Boolean canCommunicate;

	@Column(name = "can_restitute")
	private Boolean canRestitute;

	@Column(name = "par_cancommunicate")
	private Boolean parCanCommunicate;

	@Column(name = "par_canrestitute")
	private Boolean parCanRestitute;

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

	public Boolean isParCanDeposit() {
		return Objects.isNull(parCanDeposit) ? false : parCanDeposit;
	}

	public void setParCanDeposit(Boolean parCanDeposit) {
		this.parCanDeposit = parCanDeposit;
	}

	public Boolean isParCanDelay() {
		return Objects.isNull(parCanDelay) ? false : parCanDelay;
	}

	public void setParCanDelay(Boolean parCanDelay) {
		this.parCanDelay = parCanDelay;
	}

	public Boolean isParCanDestroy() {
		return Objects.isNull(parCanDestroy) ? false : parCanDestroy;
	}

	public void setParCanDestroy(Boolean parCanDestroy) {
		this.parCanDestroy = parCanDestroy;
	}

	public Boolean isParCanModProf() {
		return Objects.isNull(parCanModProf) ? false : parCanModProf;
	}

	public void setParCanModProf(Boolean parCanModProf) {
		this.parCanModProf = parCanModProf;
	}

	public Boolean isParCanRead() {
		return Objects.isNull(parCanRead) ? false : parCanRead;
	}

	public void setParCanRead(Boolean parCanRead) {
		this.parCanRead = parCanRead;
	}

	public Boolean isCanCommunicate() {
		return Objects.isNull(canCommunicate) ? false : canCommunicate;
	}

	public void setCanCommunicate(Boolean canCommunicate) {
		this.canCommunicate = canCommunicate;
	}

	public Boolean isCanRestitute() {
		return Objects.isNull(canRestitute) ? false : canRestitute;
	}

	public void setCanRestitute(Boolean canRestitute) {
		this.canRestitute = canRestitute;
	}

	public Boolean isParCanCommunicate() {
		return Objects.isNull(parCanCommunicate) ? false : parCanCommunicate;
	}

	public void setParCanCommunicate(Boolean parCanCommunicate) {
		this.parCanCommunicate = parCanCommunicate;
	}

	public Boolean isParCanRestitute() {
		return Objects.isNull(parCanRestitute) ? false : parCanRestitute;
	}

	public void setParCanRestitute(Boolean parCanRestitute) {
		this.parCanRestitute = parCanRestitute;
	}
}
