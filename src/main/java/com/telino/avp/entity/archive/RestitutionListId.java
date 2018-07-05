package com.telino.avp.entity.archive;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RestitutionListId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5268711807041376059L;

	@Column(name = "restitutionid")
	private UUID restitutionId;

	@Column(name = "docid")
	private UUID docId;

	public RestitutionListId() {
		super();
	}

	public UUID getRestitutionId() {
		return restitutionId;
	}

	public void setRestitutionId(UUID restitutionId) {
		this.restitutionId = restitutionId;
	}

	public UUID getDocId() {
		return docId;
	}

	public void setDocId(UUID docId) {
		this.docId = docId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(restitutionId, docId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (Objects.isNull(obj))
			return false;

		// Convert the object
		if (!RestitutionListId.class.isAssignableFrom(obj.getClass()))
			return false;
		RestitutionListId other = (RestitutionListId) obj;

		// Compare the attributs
		if (Objects.isNull(this.restitutionId) ? !Objects.isNull(other.restitutionId) : !this.restitutionId.equals(other.restitutionId))
			return false;

		if (Objects.isNull(this.docId) ? !Objects.isNull(other.docId) : !this.docId.equals(other.docId))
			return false;

		return true;
	}

}
