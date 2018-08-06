package org.bossky.user.impl;

import org.bossky.store.Assistant;
import org.bossky.user.Role;
import org.bossky.user.User;

/**
 * 用户助手
 * 
 * @author daibo
 *
 */
public interface UserAssistant extends Assistant {
	/**
	 * 根据凭证查找用户
	 * 
	 * @param usertoken
	 *            账户、邮箱、手机号
	 * @return
	 */
	public User findUser(String usertoken);

	/**
	 * 通过id获取角色
	 * 
	 * @param id
	 * @return
	 */
	public Role getRole(String id);

	/**
	 * 构建索引
	 * 
	 * @param simpleUser
	 */
	public void buildIndex(SimpleUser simpleUser);

}
