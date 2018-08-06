package org.bossky.web;

import org.bossky.user.User;

/**
 * 用户会话
 * 
 * @author daibo
 *
 */
public interface UserSession {
	/**
	 * 获取用户
	 * 
	 * @return
	 */
	public User getUser();
}
