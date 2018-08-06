package org.bossky.user.exception;

/**
 * 用户不存在
 * 
 * @author daibo
 *
 */
public class UserNotExistException extends LoginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotExistException() {
		super();
	}

	public UserNotExistException(String message) {
		super(message);
	}

	public UserNotExistException(String message, Throwable e) {
		super(message, e);
	}
}
