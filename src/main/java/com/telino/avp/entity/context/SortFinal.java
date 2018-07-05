package com.telino.avp.entity.context;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sortfinal")
public class SortFinal {
	
	@Id
	private Integer sortFinalId;
	
	private String sortFinal;

	public SortFinal() {
		super();
	}

	public Integer getSortFinalId() {
		return sortFinalId;
	}

	public void setSortFinalId(Integer sortFinalId) {
		this.sortFinalId = sortFinalId;
	}

	public String getSortFinal() {
		return sortFinal;
	}

	public void setSortFinal(String sortFinal) {
		this.sortFinal = sortFinal;
	}	
}
