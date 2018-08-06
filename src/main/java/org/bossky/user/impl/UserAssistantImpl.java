package org.bossky.user.impl;

import org.bossky.search.Searcher;
import org.bossky.search.SearcherHub;
import org.bossky.search.support.IndexEntrys;
import org.bossky.store.Store;
import org.bossky.store.StoreHub;
import org.bossky.user.Role;

/**
 * 用户助手实现
 * 
 * @author daibo
 *
 */
public abstract class UserAssistantImpl implements UserAssistant {
	/** 存储器集合 */
	protected final StoreHub storeHub;
	/** 用户存储器 */
	protected final Store<SimpleUser> userStore;
	/** 角色存储器 */
	protected final Store<XmlRole> roleStore;
	/** 搜索器集合 */
	protected SearcherHub searcherHub;
	/** 用户搜索器 */
	protected Searcher userSearcher;

	public UserAssistantImpl(StoreHub hub) {
		this.storeHub = hub;
		userStore = hub.openStore(SimpleUser.class, this);
		roleStore = hub.openStore(XmlRole.class, this);
	}

	public void setSearcherHub(SearcherHub shub) {
		searcherHub = shub;
		userSearcher = searcherHub.openSearcher("user");
	}

	@Override
	public StoreHub getStoreHub() {
		return storeHub;
	}

	@Override
	public void buildIndex(SimpleUser simpleUser) {
		if (null == userSearcher) {
			return;
		}
		userSearcher.updateEntry(IndexEntrys.valueOf(simpleUser.getId().getId()), simpleUser.getIndexKeywords());
	}

	@Override
	public Role getRole(String id) {
		return roleStore.get(id);
	}

}
