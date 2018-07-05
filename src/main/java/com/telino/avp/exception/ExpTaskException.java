package com.telino.avp.exception;

public class ExpTaskException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9046120927434287978L;

	public ExpTaskException() {
		super();
	}

	public ExpTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExpTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpTaskException(String message) {
		super(message);
	}

	public ExpTaskException(Throwable cause) {
		super(cause);
	}	
}
