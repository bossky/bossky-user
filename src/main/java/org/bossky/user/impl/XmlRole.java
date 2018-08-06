package org.bossky.user.impl;

import java.util.List;

import javax.annotation.Resource;

import org.bossky.common.util.Misc;
import org.bossky.store.StoreId;
import org.bossky.store.support.AbstractStoreble;
import org.bossky.user.Right;
import org.bossky.user.Role;

/**
 * 简单角色实现
 * 
 * @author daibo
 *
 */
public class XmlRole extends AbstractStoreble<UserAssistant> implements Role {
	/** 名称 */
	@Resource
	protected String name;
	/** 描述 */
	@Resource
	protected String caption;
	/** 权限 */
	@Resource
	protected List<XmlRight> rights;

	protected XmlRole(UserAssistant assistant) {
		super(assistant);
	}

	public XmlRole(UserAssistant assistant, String id) {
		super(assistant);
		this.id = new StoreId(getClass(), id);
		flush();
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		if (Misc.eq(this.name, name)) {
			return;
		}
		this.name = name;
		flush();
	}

	public void setCaption(String caption) {
		if (Misc.eq(this.caption, caption)) {
			return;
		}
		this.caption = caption;
		flush();
	}

	@Override
	public String getCaption() {
		return caption;
	}

	public void setRights(List<XmlRight> rights) {
		if (Misc.eq(this.rights, rights)) {
			return;
		}
		this.rights=rights;
		flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Right> getRights() {
		List<? extends Right> r = rights;
		return (List<Right>) r;
	}

}
