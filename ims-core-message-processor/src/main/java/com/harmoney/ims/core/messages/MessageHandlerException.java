/**
 * 
 */
package com.harmoney.ims.core.messages;

/**
 * @author Roger Parkinson
 *
 */
public class MessageHandlerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public MessageHandlerException() {
	}

	/**
	 * @param message
	 */
	public MessageHandlerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MessageHandlerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MessageHandlerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
