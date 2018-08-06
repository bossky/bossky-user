package org.bossky.user.exception;

/**
 * 登陆异常
 * 
 * @author daibo
 *
 */
public class LoginException extends UserException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginException() {
		super();
	}

	public LoginException(String message) {
		super(message);
	}

	public LoginException(String message, Throwable e) {
		super(message, e);
	}
}
