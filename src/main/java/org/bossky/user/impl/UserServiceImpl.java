package org.bossky.user.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bossky.common.ResultPage;
import org.bossky.common.support.TranformResultPage;
import org.bossky.common.util.AssertUtil;
import org.bossky.common.util.IoUtil;
import org.bossky.common.util.Misc;
import org.bossky.search.IndexResult;
import org.bossky.search.IndexResults;
import org.bossky.search.QueryKeyword;
import org.bossky.search.Searcher;
import org.bossky.search.support.QueryKeywords;
import org.bossky.store.StoreHub;
import org.bossky.user.Role;
import org.bossky.user.User;
import org.bossky.user.UserService;
import org.bossky.user.exception.UserException;
import org.bossky.user.exception.UserExistException;
import org.bossky.user.exception.UserNotExistException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户服务实现
 * 
 * @author daibo
 *
 */
public class UserServiceImpl extends UserAssistantImpl implements UserService {
	/** 日志 */
	static final Logger _Logger = LoggerFactory.getLogger(UserServiceImpl.class);

	public UserServiceImpl(StoreHub hub) {
		super(hub);
	}

	@Override
	public User addUser(String account, String password) throws UserException {
		AssertUtil.assertNotNull(account);
		AssertUtil.assertNotNull(password);
		synchronized (userStore) {
			if (null != findUser(account)) {
				throw new UserExistException(account + "已被占用");
			}
		}
		SimpleUser user = new SimpleUser(this, account, password);
		return user;
	}

	@Override
	public User login(String loginToken, String password) throws UserException {
		SimpleUser user = findSimpleUser(loginToken);
		if (null == user) {
			throw new UserNotExistException("帐户不存在");
		}
		if (user.isLock()) {
			throw new UserException("禁止该用户登陆");
		}
		user.checkPassword(password);
		return user;
	}

	@Override
	public int nextCanApplyAuthCodeTime(String phone) {
		SimpleUser user = findSimpleUser(phone);
		if (null == user) {
			return 0;
		}
		return user.nextCanApplyAuthCodeTime();
	}

	@Override
	public synchronized String applyAuthCode(String phone) throws UserException {
		SimpleUser user = findSimpleUser(phone);
		if (null == user) {
			user = new SimpleUser(this, null, null);
			user.setPhone(phone);
		}
		return user.applyAuthCode();
	}

	@Override
	public User auth(String loginToken, String authcode) throws UserException {
		SimpleUser user = findSimpleUser(loginToken);
		if (null == user) {
			throw new UserNotExistException("帐户不存在");
		}
		if (user.isLock()) {
			throw new UserException("禁止该用户登陆");
		}
		user.checkAuthCode(authcode);
		return user;
	}

	@Override
	public User findUser(String usertoken) {
		return findSimpleUser(usertoken);
	}

	/**
	 * 查找用户
	 * 
	 * @param usertoken
	 * @return
	 */
	private SimpleUser findSimpleUser(String usertoken) {
		IndexResult ir = userSearcher.get(SimpleUser.genLoginTokenKeyword(usertoken).getKeword());
		if (null == ir) {
			return null;
		}
		return userStore.get(ir.getKey());
	}

	@Override
	public User getUser(String id) {
		return userStore.get(id);

	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultPage<User> searchUser(String keyword) {
		if (Misc.isEmpty(keyword)) {
			ResultPage<? extends User> rp = userStore.startWith(null);
			return (ResultPage<User>) rp;
		}
		QueryKeyword prefix = QueryKeywords.valueOfEntryPrefix(SimpleUser.class.getSimpleName());
		QueryKeyword ks = QueryKeywords.valueOfKeyword(keyword);
		IndexResults irs = userSearcher.search(Searcher.OPTION_SORT_BY_SCORE_DESC, Arrays.asList(prefix, ks));
		return new TranformResultPage<User, IndexResult>(irs) {

			@Override
			public User tranform(IndexResult v) {
				return userStore.get(v.getKey());
			}
		};
	}

	@Override
	public Role getRole(String id) {
		return roleStore.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultPage<Role> searchRoles(String keyword) {
		if (Misc.isEmpty(keyword)) {
			ResultPage<? extends Role> rp = roleStore.startWith(null);
			return (ResultPage<Role>) rp;
		}
		QueryKeyword prefix = QueryKeywords.valueOfEntryPrefix(XmlRole.class.getSimpleName());
		QueryKeyword ks = QueryKeywords.valueOfKeyword(keyword);
		IndexResults irs = userSearcher.search(Searcher.OPTION_SORT_BY_SCORE_DESC, Arrays.asList(prefix, ks));
		return new TranformResultPage<Role, IndexResult>(irs) {

			@Override
			public Role tranform(IndexResult v) {
				return roleStore.get(v.getKey());
			}
		};
	}

	/**
	 * 设置管理员
	 * 
	 * @param account
	 */
	public void setAdmin(String account) {
		if (null != findUser(account)) {
			return;
		}
		SimpleUser user = new SimpleUser(this, account, account);
		user.setRolesFast(Arrays.asList("00000000001-00"));

	}

	/**
	 * 设置权限表
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void setRoles(String xml) throws IOException {
		File file = IoUtil.getFile(xml);
		_Logger.info("上传" + file.getAbsolutePath() + "权限表");
		uploadRoles(new FileInputStream(file));
	}

	/**
	 * 上传用户
	 * 
	 * @param xml
	 * @throws IOException
	 */
	public void uploadRoles(InputStream xml) throws IOException {
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(xml);
		} catch (DocumentException e) {
			throw new IOException("解析文档失败", e);
		}
		Element root = doc.getRootElement();
		String suffix = root.attributeValue("id");
		AssertUtil.assertNull(suffix, "请使用id元素指定系统id");
		@SuppressWarnings("unchecked")
		List<Element> roles = root.elements("role");
		if (Misc.isEmpty(roles)) {
			return;// 有没配置
		}
		for (Element e : roles) {
			String id = e.attributeValue("id");
			AssertUtil.assertNull(id, "请使用id元素指定角色id");
			String name = e.attributeValue("name");
			AssertUtil.assertNull(name, "请使用name元素指定角色名称");
			String caption = e.attributeValue("caption");
			XmlRole role = openXmlRole(id + "-" + suffix);
			role.setName(name);
			role.setCaption(caption);
			@SuppressWarnings("unchecked")
			List<Element> rights = e.elements("right");
			List<XmlRight> rs = new ArrayList<XmlRight>();
			for (Element ee : rights) {
				String uri = ee.attributeValue("uri");
				AssertUtil.assertNull(name, "请使用uri元素指定权限资源");
				String rule = ee.attributeValue("rule");
				if (Misc.isEmpty(rule)) {
					rule = XmlRight.RULE_ALLOW;
				}
				rs.add(new XmlRight(uri, rule));
			}
			role.setRights(rs);
		}
	}

	/**
	 * 打开一个配置用户
	 * 
	 * @param id
	 * @return
	 */
	private synchronized XmlRole openXmlRole(String id) {
		XmlRole role = roleStore.get(id);
		if (null == role) {
			role = new XmlRole(this, id);
		}
		return role;
	}

}
