package org.bossky.web.impl;

import org.bossky.user.User;
import org.bossky.web.UserSession;

/**
 * 简单的用户会话实现
 * 
 * @author daibo
 *
 */
public class SimpleUserSession implements UserSession {

	private User user;

	public SimpleUserSession(User user) {
		this.user = user;
	}

	@Override
	public User getUser() {
		return user;
	}

}
