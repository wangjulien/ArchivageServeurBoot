package com.telino.avp.entity.archive;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.telino.avp.protocol.DbEntityProtocol.RestitutionState;

@Entity
@Table(name = "restitutions")
public class Restitution {
	
	@Id
	private UUID restitutionId;
	
	private String restitutionMotif;
	
	@Enumerated(EnumType.STRING)
	private RestitutionState restitutionStatus;
	
	private String userId;
		
	private String domnNom;
	
	private ZonedDateTime horodatage;
	
	private String desinataire;
	
	@Column(name = "restitution_end")
	private ZonedDateTime restitutionEnd;
	
	@OneToMany(mappedBy = "restitution", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private Set<RestitutionList> restitutionList = new HashSet<>();

	public Restitution() {
		super();
	}

	public UUID getRestitutionId() {
		return restitutionId;
	}

	public void setRestitutionId(UUID restitutionId) {
		this.restitutionId = restitutionId;
	}

	public String getRestitutionMotif() {
		return restitutionMotif;
	}

	public void setRestitutionMotif(String restitutionMotif) {
		this.restitutionMotif = restitutionMotif;
	}

	public RestitutionState getRestitutionStatus() {
		return restitutionStatus;
	}

	public void setRestitutionStatus(RestitutionState restitutionStatus) {
		this.restitutionStatus = restitutionStatus;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDomnNom() {
		return domnNom;
	}

	public void setDomnNom(String domnNom) {
		this.domnNom = domnNom;
	}

	public ZonedDateTime getHorodatage() {
		return horodatage;
	}

	public void setHorodatage(ZonedDateTime horodatage) {
		this.horodatage = horodatage;
	}

	public String getDesinataire() {
		return desinataire;
	}

	public void setDesinataire(String desinataire) {
		this.desinataire = desinataire;
	}

	public ZonedDateTime getRestitutionEnd() {
		return restitutionEnd;
	}

	public void setRestitutionEnd(ZonedDateTime restitutionEnd) {
		this.restitutionEnd = restitutionEnd;
	}

	public Set<RestitutionList> getRestitutionList() {
		return restitutionList;
	}

	public void setRestitutionList(Set<RestitutionList> restitutionList) {
		this.restitutionList = restitutionList;
	}

	public void addRestitutionList(RestitutionList rl) {
		this.restitutionList.add(rl);
		rl.setRestitution(this);
	}	
}
