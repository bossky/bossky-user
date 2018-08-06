package org.bossky.user;

import java.util.List;

import org.bossky.store.Storeble;
import org.bossky.user.exception.UserException;

/**
 * 用户
 * 
 * @author daibo
 *
 */
public interface User extends Storeble {
	/**
	 * 名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 设置名称
	 * 
	 * @param name
	 */
	public void setName(String name);

	/**
	 * 获取角色
	 * 
	 * @return
	 */
	public List<Role> getRoles();

	/**
	 * 设置角色
	 * 
	 * @param roles
	 */
	public void setRoles(List<Role> roles);

	/**
	 * 添加角色
	 * 
	 * @param role
	 */
	public void addRole(Role role);

	/**
	 * 移除角色
	 * 
	 * @param role
	 */
	public void removeRole(Role role);

	/**
	 * 是否可访问该url
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isRight(String uri);

	/**
	 * 账号
	 * 
	 * @return
	 */
	public String getAccount();

	/**
	 * 设置账号
	 * 
	 * @param account
	 * @throws UserException
	 */
	public void setAccount(String account) throws UserException;

	/**
	 * 电话
	 * 
	 * @return
	 */
	public String getPhone();

	/**
	 * 设置电话
	 * 
	 * @param phone
	 * @throws UserException
	 */
	public void setPhone(String phone) throws UserException;

	/**
	 * 邮箱
	 * 
	 * @return
	 */
	public String getEmail();

	/**
	 * 设置邮箱
	 * 
	 * @param email
	 * @throws UserException
	 */
	public void setEmail(String email) throws UserException;

	/**
	 * 是否可能初始化密码
	 * 
	 * @return
	 */
	public boolean isCanInitPassword();

	/**
	 * 初始化密码
	 * 
	 * @param password
	 * @throws UserException
	 */
	public void initPassword(String password) throws UserException;

	/**
	 * 修改密码
	 * 
	 * @param oldPassword
	 *            旧密码
	 * @param newPassword
	 *            新密码
	 * @return 是否修改成功,是返回true,不是返回false
	 */
	public void changePassword(String oldPassword, String newPassword) throws UserException;

	/**
	 * 重置密码
	 * 
	 * @param password
	 *            要重置的密码
	 * @return 是否重置成功,是返回true,不是返回false
	 */
	public void resetPassword(String password) throws UserException;

	/**
	 * 是否锁定
	 * 
	 * @return
	 */
	public boolean isLock();

	/**
	 * 锁定
	 */
	public void lock();

	/**
	 * 解锁
	 */
	public void unLock();

}
