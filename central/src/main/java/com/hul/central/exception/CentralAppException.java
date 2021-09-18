package com.hul.central.exception;

public class CentralAppException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CentralAppException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CentralAppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CentralAppException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CentralAppException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CentralAppException(Throwable cause) {
		super(cause);
	}
	
}
