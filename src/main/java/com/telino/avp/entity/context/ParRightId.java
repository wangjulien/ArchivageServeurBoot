package com.telino.avp.entity.context;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ParRightId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5760127901911810448L;

	@Column(name = "par_id")
	private Integer parId;

	@Column(name = "userid")
	private String userId;

	public ParRightId() {
		super();
	}

	public Integer getParId() {
		return parId;
	}

	public void setParId(Integer parId) {
		this.parId = parId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (Objects.isNull(obj))
			return false;

		// Convert the object
		if (!ParRightId.class.isAssignableFrom(obj.getClass()))
			return false;
		ParRightId other = (ParRightId) obj;

		// Compare the attributs
		if (Objects.isNull(this.parId) ? !Objects.isNull(other.parId) : !this.parId.equals(other.parId))
			return false;

		if (Objects.isNull(this.userId) ? !Objects.isNull(other.userId) : !this.userId.equals(other.userId))
			return false;

		return true;
	}

}
