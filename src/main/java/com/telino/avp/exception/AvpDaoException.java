package com.telino.avp.exception;

/**
 * Exception wraps information of JPA repository exception, including entity
 * information
 * 
 * @author Jiliang.WANG
 *
 */
public class AvpDaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3559912932075988663L;

	private String entityId;

	private String entityName;

	public AvpDaoException() {
		super();
	}

	public AvpDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AvpDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public AvpDaoException(String message) {
		super(message);
	}

	public AvpDaoException(Throwable cause, final String entityId, final String entityName) {
		super(cause);
		this.entityId = entityId;
		this.entityName = entityName;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
