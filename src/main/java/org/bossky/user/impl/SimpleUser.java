package org.bossky.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.bossky.common.support.TranformList;
import org.bossky.common.util.AssertUtil;
import org.bossky.common.util.CollectionUtil;
import org.bossky.common.util.Misc;
import org.bossky.search.IndexKeyword;
import org.bossky.search.Searcheble;
import org.bossky.search.support.IndexKeywords;
import org.bossky.store.support.AbstractStoreble;
import org.bossky.user.Right;
import org.bossky.user.Role;
import org.bossky.user.User;
import org.bossky.user.exception.PasswordNotRightException;
import org.bossky.user.exception.UserException;

/**
 * 简单的用户实现
 * 
 * @author daibo
 *
 */
public class SimpleUser extends AbstractStoreble<UserAssistant> implements User, Searcheble {
	/** 账号 */
	@Resource
	protected String account;
	/** md5后的密码 */
	@Resource
	protected String md5password;
	/** 名称 */
	@Resource
	protected String name;
	/** 手机 */
	@Resource
	protected String phone;
	/** 邮箱 */
	@Resource
	protected String email;
	/** 角色 */
	@Resource
	protected List<String> roles;
	/** 权限 */
	@Resource
	protected List<String> rights;
	/** 标记 */
	@Resource
	protected int marks;
	/** 验证码 */
	@Resource
	protected String md5AuthCode;
	/** 最后一次申请验证码的时间 */
	@Resource
	protected long lastApplyAuthCodeTime;

	/** 验证码长度 */
	private static int AUTH_CODE_LENGTH = 6;
	/** 请求验证码的间隔 */
	private static long APPLY_AUTH_CODE_INTERVAL = 3 * 60 * 1000;
	/** 验证码有效期 */
	private static long AUTH_CODE_VALIDITY = 30 * 60 * 1000;
	/** 登陆凭证的索引 */
	private static String INDEX_LOGIN_TOKEN = "LT:";
	/** 标记用户被锁定 */
	public static final int MARK_LOCK = 1 << 0;

	protected SimpleUser(UserAssistant assistant) {
		super(assistant);
	}

	/**
	 * 构造
	 * 
	 * @param assistant
	 *            助手
	 * @param account
	 *            账号
	 * @param password
	 *            密码
	 */
	public SimpleUser(UserAssistant assistant, String account, String password) {
		super(assistant);
		genId();
		boolean needFlush = false;
		boolean needIndex = false;
		if (!Misc.isEmpty(account)) {
			this.account = account;
			needFlush = true;
			needIndex = true;
		}
		if (!Misc.isEmpty(password)) {
			this.md5password = Misc.MD5(password);
			needFlush = true;
		}
		if (needFlush) {
			flush();
		}
		if (needIndex) {
			buildIndex();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (Misc.eq(this.name, name)) {
			return;
		}
		this.name = name;
		flush();
	}

	@Override
	public List<Role> getRoles() {
		return new TranformList<Role, String>(this.roles) {

			@Override
			public Role tranform(String v) {
				return getAssistant().getRole(v);
			}
		};
	}

	@Override
	public void setRoles(List<Role> roles) {
		setRolesFast(new TranformList<String, Role>(roles) {

			@Override
			public String tranform(Role v) {
				return v.getId().getId();
			}
		});
	}

	public void setRolesFast(List<String> roles) {
		this.roles = roles;
		flush();
	}

	@Override
	public void addRole(Role role) {
		AssertUtil.assertNotNull(role);
		this.roles = CollectionUtil.safeAdd(roles, role.getId().getId());
		flush();
	}

	@Override
	public void removeRole(Role role) {
		AssertUtil.assertNotNull(role);
		this.roles = CollectionUtil.safeRemove(roles, role.getId().getId());
		flush();
	}

	@Override
	public boolean isRight(String uri) {
		List<Role> roles = getRoles();
		if (Misc.isEmpty(roles)) {
			return false;
		}
		boolean isAllow = false;
		for (Role r : getRoles()) {
			List<Right> rights = r.getRights();
			if (Misc.isEmpty(rights)) {
				continue;
			}
			for (Right ri : rights) {
				if (!ri.isMatch(uri)) {
					continue;
				}
				if (Misc.eq(ri.getRule(), Right.RULE_ALLOW)) {
					isAllow = true;// 有允许的了
				} else if (Misc.eq(ri.getRule(), Right.RULE_DISALLOW)) {
					return false;// 一个禁止了就全部禁止
				}
			}
		}
		return isAllow;
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public synchronized void setAccount(String account) throws UserException {
		if (Misc.eq(this.account, account)) {
			return;
		}
		checkLoginToken(account);
		this.account = account;
		flush();
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public void setPhone(String phone) throws UserException {
		if (Misc.eq(this.phone, phone)) {
			return;
		}
		if (!Misc.isMobile(phone)) {
			throw new IllegalArgumentException("手机格式非法");
		}
		checkLoginToken(phone);
		this.phone = phone;
		flush();
		buildIndex();
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) throws UserException {
		if (Misc.eq(this.email, email)) {
			return;
		}
		if (!Misc.isEmail(email)) {
			throw new IllegalArgumentException("邮箱格式非法");
		}
		checkLoginToken(email);
		this.email = email;
		flush();
		buildIndex();
	}

	/**
	 * 检查登陆凭证
	 * 
	 * @param token
	 * @throws UserException
	 */
	private void checkLoginToken(String token) throws UserException {
		if (null != getAssistant().findUser(token)) {
			throw new UserException("登陆凭证已被占用");
		}
	}

	@Override
	public boolean isCanInitPassword() {
		return Misc.isEmpty(md5password);
	}

	@Override
	public void initPassword(String password) throws UserException {
		if (isCanInitPassword()) {
			this.md5password = Misc.MD5(password);
			flush();
			return;
		}
		throw new UserException("无法初始化密码");
	}

	/**
	 * 检查密码是否正确
	 * 
	 * @param password
	 *            密码
	 * @throws UserException
	 */
	public void checkPassword(String password) throws UserException {
		if (Misc.eq(md5password, Misc.MD5(password))) {
			return;
		}
		throw new PasswordNotRightException("密码错误");
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) throws UserException {
		checkPassword(oldPassword);
		this.md5password = Misc.MD5(newPassword);
		flush();
		return;
	}

	@Override
	public void resetPassword(String password) throws UserException {
		this.md5password = Misc.MD5(password);
		flush();
	}

	/**
	 * 下一次可申请验证码的时间,单位秒
	 * 
	 * @return
	 */
	public int nextCanApplyAuthCodeTime() {
		long offset = (lastApplyAuthCodeTime + APPLY_AUTH_CODE_INTERVAL) - System.currentTimeMillis();
		if (offset <= 0) {
			return 0;
		}
		return (int) (offset / 1000);
	}

	/**
	 * 生成验证码
	 * 
	 * @return
	 * @throws UserException
	 */
	public synchronized String applyAuthCode() throws UserException {
		if ((System.currentTimeMillis() - lastApplyAuthCodeTime) < APPLY_AUTH_CODE_INTERVAL) {
			throw new UserException("申请验证码太过频繁");
		}
		lastApplyAuthCodeTime = System.currentTimeMillis();
		String code = Misc.randomNumber(AUTH_CODE_LENGTH);
		this.md5AuthCode = Misc.MD5(code);
		return code;
	}

	/**
	 * 检查验证码
	 * 
	 * @param authcode
	 */
	public void checkAuthCode(String authcode) throws UserException {
		if ((System.currentTimeMillis() - lastApplyAuthCodeTime) > AUTH_CODE_VALIDITY) {
			throw new UserException("验证码已过期,请重新申请");
		}
		if (Misc.eq(md5AuthCode, Misc.MD5(authcode))) {
			return;
		}
		throw new PasswordNotRightException("验证码错误");
	}

	@Override
	public boolean isLock() {
		return isMark(MARK_LOCK);
	}

	@Override
	public void lock() {
		setMarks(MARK_LOCK);
	}

	@Override
	public void unLock() {
		setMarks(-MARK_LOCK);
	}

	/**
	 * 是否标记位
	 * 
	 * @param mark
	 *            要匹配的标记位
	 * @return true表示有匹配
	 */
	public boolean isMark(int mark) {
		return (mark == (mark & marks));
	}

	/**
	 * 设置位标识
	 * 
	 * @param marks
	 *            位标识，若负数则为去除，0则置0
	 */
	public void setMarks(int marks) {
		if (marks < 0) {
			this.marks &= ~(-marks);
		} else if (0 == marks) {
			this.marks = 0;
		} else {
			this.marks |= marks;
		}
		flush();
	}

	/**
	 * 生成登陆凭证关键字
	 * 
	 * @param token
	 * @return
	 */
	public static IndexKeyword genLoginTokenKeyword(String token) {
		if (Misc.isEmpty(token)) {
			return null;
		}
		return IndexKeywords.valueOf(INDEX_LOGIN_TOKEN + token);
	}

	@Override
	public List<IndexKeyword> getIndexKeywords() {
		List<IndexKeyword> ks = new ArrayList<IndexKeyword>();
		if (!Misc.isEmpty(account)) {
			ks.add(IndexKeywords.valueOf(account));
			ks.add(genLoginTokenKeyword(account));
		}
		if (!Misc.isEmpty(phone)) {
			ks.add(IndexKeywords.valueOf(phone));
			ks.add(genLoginTokenKeyword(phone));
		}
		if (!Misc.isEmpty(email)) {
			ks.add(IndexKeywords.valueOf(email));
			ks.add(genLoginTokenKeyword(email));
		}
		if (Misc.isEmpty(name)) {
			ks.add(IndexKeywords.valueOf(name));
		}
		return ks;
	}

	@Override
	public void buildIndex() {
		getAssistant().buildIndex(this);
	}

}
