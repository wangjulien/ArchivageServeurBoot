package com.telino.avp.entity.archive;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommunicationListId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2023871557487628602L;

	@Column(name = "communicationid")
	private UUID communicationId;

	@Column(name = "docid")
	private UUID docId;

	public CommunicationListId() {
		super();
	}

	public UUID getCommunicationId() {
		return communicationId;
	}

	public void setCommunicationId(UUID communicationId) {
		this.communicationId = communicationId;
	}

	public UUID getDocId() {
		return docId;
	}

	public void setDocId(UUID docId) {
		this.docId = docId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(communicationId, docId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (Objects.isNull(obj))
			return false;

		// Convert the object
		if (!CommunicationListId.class.isAssignableFrom(obj.getClass()))
			return false;
		CommunicationListId other = (CommunicationListId) obj;

		// Compare the attributs
		if (Objects.isNull(this.communicationId) ? !Objects.isNull(other.communicationId) : !this.communicationId.equals(other.communicationId))
			return false;

		if (Objects.isNull(this.docId) ? !Objects.isNull(other.docId) : !this.docId.equals(other.docId))
			return false;

		return true;
	}

}
