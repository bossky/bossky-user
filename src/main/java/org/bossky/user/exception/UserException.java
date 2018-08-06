package org.bossky.user.exception;

/**
 * 用户异常
 * 
 * @author daibo
 *
 */
public class UserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserException() {
		super();
	}

	public UserException(String message) {
		super(message);
	}

	public UserException(String message, Throwable e) {
		super(message, e);
	}

}
