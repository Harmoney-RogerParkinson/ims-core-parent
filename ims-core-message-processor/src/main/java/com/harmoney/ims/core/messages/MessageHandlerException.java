/**
 * 
 */
package com.harmoney.ims.core.messages;

/**
 * @author Roger Parkinson
 *
 */
public class MessageHandlerException extends RuntimeException {

	/**
	 * 
	 */
	public MessageHandlerException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MessageHandlerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MessageHandlerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageHandlerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated constructor stub
	}

}
