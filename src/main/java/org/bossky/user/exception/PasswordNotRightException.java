package org.bossky.user.exception;

/**
 * 密码错误
 * 
 * @author daibo
 *
 */
public class PasswordNotRightException extends LoginException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordNotRightException() {
		super();
	}

	public PasswordNotRightException(String message) {
		super(message);
	}

	public PasswordNotRightException(String message, Throwable e) {
		super(message, e);
	}
}
