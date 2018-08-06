package org.bossky.web.impl;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.bossky.user.User;
import org.bossky.web.AuthorizeException;
import org.bossky.web.Authorizer;
import org.bossky.web.UserSession;

/**
 * 基于用户的认证器实现
 * 
 * @author daibo
 *
 */
public class UserAuthorizer implements Authorizer {
	/** 用户会话key */
	protected String userSessionKey = "bossky-uuss";

	public UserAuthorizer() {

	}

	@Override
	public boolean auth(ServletRequest request, ServletResponse response) throws AuthorizeException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			User user = getSession(httpRequest).getUser();
			if (null == user) {
				return false;
			}
			if (user.isRight(httpRequest.getRequestURI())) {
				return true;
			} else {
				throw new AuthorizeException();
			}
		}
		return false;

	}

	/**
	 * 设置会话
	 * 
	 * @param request
	 * @param response
	 * @param user
	 */
	public void setSession(ServletRequest request, ServletResponse response, UserSession session) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			setSession(httpRequest, session);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * 获取会话
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public UserSession getSession(ServletRequest request, ServletResponse response) {
		if (request instanceof HttpServletRequest) {
			return getSession((HttpServletRequest) request);
		}
		return EMPTY;
	}

	/**
	 * 设置会话
	 * 
	 * @param request
	 */
	private void setSession(HttpServletRequest request, UserSession session) {
		request.getSession().setAttribute(userSessionKey, session);
	}

	/**
	 * 获取会话
	 * 
	 * @param request
	 * @return
	 */
	private UserSession getSession(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(userSessionKey);
		if (obj instanceof UserSession) {
			return (UserSession) obj;
		}
		return EMPTY;
	}

	/**
	 * 空会话
	 */
	private UserSession EMPTY = new UserSession() {

		@Override
		public User getUser() {
			return null;
		}

	};

}
