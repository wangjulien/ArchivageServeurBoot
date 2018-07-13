package com.telino.avp.entity.context;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "destructioncriterias")
public class DestructionCriteria {

	@Id
	private Integer destructionCriteriaId;

	private String destructionCriteria;

	private int minDestructionDelay;

	public DestructionCriteria() {
		super();
	}

	public Integer getDestructionCriteriaId() {
		return destructionCriteriaId;
	}

	public void setDestructionCriteriaId(Integer destructionCriteriaId) {
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
