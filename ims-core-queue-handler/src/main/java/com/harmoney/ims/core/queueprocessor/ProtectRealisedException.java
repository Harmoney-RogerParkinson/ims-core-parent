package com.harmoney.ims.core.queueprocessor;

public class ProtectRealisedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProtectRealisedException() {
	}

	public ProtectRealisedException(String message) {
		super(message);
	}

	public ProtectRealisedException(Throwable cause) {
		super(cause);
	}

	public ProtectRealisedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtectRealisedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
