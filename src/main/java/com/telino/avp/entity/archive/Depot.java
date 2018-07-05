package com.telino.avp.entity.archive;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "depots")
public class Depot {

	@Id
	private UUID idDepot;

	private String demandeur;
	private String status;
	private String message;
	private ZonedDateTime horodatage;

	public Depot() {
		super();
	}

	public UUID getIdDepot() {
		return idDepot;
	}

	public void setIdDepot(UUID idDepot) {
		this.idDepot = idDepot;
	}

	public String getDemandeur() {
		return demandeur;
	}

	public void setDemandeur(String demandeur) {
		this.demandeur = demandeur;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ZonedDateTime getHorodatage() {
		return horodatage;
	}

	public void setHorodatage(ZonedDateTime horodatage) {
		this.horodatage = horodatage;
	}
}
