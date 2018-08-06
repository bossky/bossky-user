package org.bossky.user;

import org.bossky.common.ResultPage;
import org.bossky.user.exception.UserException;
import org.bossky.user.exception.UserExistException;

/**
 * 用户服务
 * 
 * @author daibo
 *
 */
public interface UserService {
	/**
	 * 添加用户
	 * 
	 * @param account
	 *            账号
	 * @param email
	 *            邮箱
	 * @param phone
	 *            手机
	 * @param password
	 *            密码
	 * @return 创建的用户
	 * 
	 * @throws such
	 *             as {@link UserExistException}
	 */
	public User addUser(String account, String password) throws UserException;

	/**
	 * 登陆
	 * 
	 * @param account
	 *            登陆凭证
	 * @param passowrd
	 *            密码
	 * @return
	 * @throws UserException
	 */
	public User login(String loginToken, String password) throws UserException;

	/**
	 * 下一次可申请验证码的时间,单位秒
	 * 
	 * @return
	 */
	public int nextCanApplyAuthCodeTime(String phone);

	/**
	 * 申请验证码
	 * 
	 * @param phone
	 *            手机 无手机对应的用户时会创建一个新的用户
	 * @return
	 */
	public String applyAuthCode(String phone) throws UserException;

	/**
	 * 验证登陆
	 * 
	 * @param loginToken
	 *            登陆凭证
	 * @param authcode
	 *            验证码
	 * @return
	 */
	public User auth(String loginToken, String authcode) throws UserException;

	/**
	 * 根据凭证查找用户
	 * 
	 * @param usertoken
	 *            账户、邮箱、手机号
	 * @return
	 */
	public User findUser(String usertoken);

	/**
	 * 搜索用户
	 * 
	 * @param keyword
	 * @return
	 */
	public ResultPage<User> searchUser(String keyword);

	/**
	 * 获取用户
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public User getUser(String id);

	/**
	 * 获取角色
	 * 
	 * @param id
	 * @return
	 */
	public Role getRole(String id);

	/**
	 * 搜索角色
	 * 
	 * @param keyword
	 * @return
	 */
	public ResultPage<Role> searchRoles(String keyword);

}
