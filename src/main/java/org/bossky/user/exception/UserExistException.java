package org.bossky.user.exception;

/**
 * 用户已存在
 * 
 * @author daibo
 *
 */
public class UserExistException extends UserException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserExistException() {
		super();
	}

	public UserExistException(String message) {
		super(message);
	}

	public UserExistException(String message, Throwable e) {
		super(message, e);
	}
}
