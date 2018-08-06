package org.bossky.user;

import java.util.List;

import org.bossky.store.Storeble;

/**
 * 角色
 * 
 * @author daibo
 *
 */
public interface Role extends Storeble {
	/**
	 * 角色名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 角色描述
	 * 
	 * @return
	 */
	public String getCaption();

	/**
	 * 一堆权限
	 * 
	 * @return
	 */
	public List<Right> getRights();

}
