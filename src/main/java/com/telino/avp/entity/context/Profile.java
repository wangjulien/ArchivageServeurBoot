package com.telino.avp.entity.context;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "profils")
public class Profile {
	
	@Id
	@Column(name = "par_id")
	private Integer parId;
	
	@Column(name = "ar_profile")
	private String arProfile;
	
	@Column(name = "par_conservation")
	private int parConversation;
	
	@OneToOne
	@JoinColumn(name = "destructioncriteriaid")
	private DestructionCriteria destructionCriteria;
	
	@OneToOne
	@JoinColumn(name = "sortfinalid")
	private SortFinal sortFinalId;
	
	// Remove of a profile will not remove the DocTypes
	@OneToMany(mappedBy = "profile", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<DocType> docTypes = new HashSet<>();
	
	// Remove of a profile implies remove of the Right
	@OneToMany(mappedBy = "profile", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private Set<ParRight> parRights = new HashSet<>();
	
	public Profile() {
		super();
	}

	public Integer getParId() {
		return parId;
	}

	public void setParId(Integer parId) {
		this.parId = parId;
	}

	public String getArProfile() {
		return arProfile;
	}

	public void setArProfile(String arProfile) {
		this.arProfile = arProfile;
	}

	public int getParConversation() {
		return parConversation;
	}

	public void setParConversation(int parConversation) {
		this.parConversation = parConversation;
	}

	public DestructionCriteria getDestructionCriteria() {
		return destructionCriteria;
	}

	public void setDestructionCriteria(DestructionCriteria destructionCriteria) {
		this.destructionCriteria = destructionCriteria;
	}

	public SortFinal getSortFinalId() {
		return sortFinalId;
	}

	public void setSortFinalId(SortFinal sortFinalId) {
		this.sortFinalId = sortFinalId;
	}

	public Set<DocType> getDocTypes() {
		return docTypes;
	}

	public void setDocTypes(Set<DocType> docTypes) {
		this.docTypes = docTypes;
	}
	
	public void addDocType(DocType docType) {
		this.docTypes.add(docType);
		docType.setProfile(this);
	}

	public Set<ParRight> getParRights() {
		return parRights;
	}

	public void setParRights(Set<ParRight> parRights) {
		this.parRights = parRights;
	}
	
	public void addParRight(ParRight parRight) {
		this.parRights.add(parRight);
		parRight.setProfile(this);
	}
}
