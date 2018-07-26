package com.telino.avp.exception;

import com.telino.avp.protocol.AvpProtocol.AvpExceptionDetail;

public class AvpServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9144692887402603245L;

	private String archiveId;
	private String journalId;

	private String action;
	private String processus;
	private AvpExceptionDetail detail;

	public AvpServiceException() {
		super();
	}

	public AvpServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AvpServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AvpServiceException(String message) {
		super(message);
	}

	public AvpServiceException(Throwable cause) {
		super(cause);
	}

	public String getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}

	public String getJournalId() {
		return journalId;
	}

	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getProcessus() {
		return processus;
	}

	public void setProcessus(String processus) {
		this.processus = processus;
	}

	public AvpExceptionDetail getDetail() {
		return detail;
	}

	public void setDetail(AvpExceptionDetail detail) {
		this.detail = detail;
	}
}
