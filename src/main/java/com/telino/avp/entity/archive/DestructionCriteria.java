package com.telino.avp.entity.archive;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "destructioncriterias")
public class DestructionCriteria {

	@Id
	private UUID destructionCriteriaId;

	private String destructionCriteria;

	private int minDestructionDelay;

	public DestructionCriteria() {
		super();
	}

	public UUID getDestructionCriteriaId() {
		return destructionCriteriaId;
	}

	public void setDestructionCriteriaId(UUID destructionCriteriaId) {
		this.destructionCriteriaId = destructionCriteriaId;
	}

	public String getDestructionCriteria() {
		return destructionCriteria;
	}

	public void setDestructionCriteria(String destructionCriteria) {
		this.destructionCriteria = destructionCriteria;
	}

	public int getMinDestructionDelay() {
		return minDestructionDelay;
	}

	public void setMinDestructionDelay(int minDestructionDelay) {
		this.minDestructionDelay = minDestructionDelay;
	}
}
