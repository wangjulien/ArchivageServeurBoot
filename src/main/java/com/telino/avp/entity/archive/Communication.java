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

import com.telino.avp.protocol.DbEntityProtocol.CommunicationState;

@Entity
@Table(name = "communications")
public class Communication {
	
	@Id
	private UUID communicationId;
	
	private String communicationMotif;
	
	@Enumerated(EnumType.STRING)
	private CommunicationState communicationStatus;
	
	private String userId;
		
	private String domnNom;
	
	private ZonedDateTime horodatage;
	
	private String desinataire;
	
	@Column(name = "communication_end")
	private ZonedDateTime communicationEnd;
	
	@OneToMany(mappedBy = "communication", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private Set<CommunicationList> communicationList = new HashSet<>();

	public Communication() {
		super();
	}

	public UUID getCommunicationId() {
		return communicationId;
	}

	public void setCommunicationId(UUID communicationId) {
		this.communicationId = communicationId;
	}

	public String getCommunicationMotif() {
		return communicationMotif;
	}

	public void setCommunicationMotif(String communicationMotif) {
		this.communicationMotif = communicationMotif;
	}

	public CommunicationState getCommunicationStatus() {
		return communicationStatus;
	}

	public void setCommunicationStatus(CommunicationState communicationStatus) {
		this.communicationStatus = communicationStatus;
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

	public ZonedDateTime getCommunicationEnd() {
		return communicationEnd;
	}

	public void setCommunicationEnd(ZonedDateTime communicationEnd) {
		this.communicationEnd = communicationEnd;
	}

	public Set<CommunicationList> getCommunicationList() {
		return communicationList;
	}

	public void setCommunicationList(Set<CommunicationList> communicationList) {
		this.communicationList = communicationList;
	}
	
	public void addCommunicationList(CommunicationList cl) {
		this.communicationList.add(cl);
		cl.setCommunication(this);
	}
}
