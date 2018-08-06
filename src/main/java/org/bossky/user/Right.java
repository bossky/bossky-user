package org.bossky.user;

/**
 * 权限
 * 
 * @author daibo
 *
 */
public interface Right {
	/** 规则-允许 */
	public static final String RULE_ALLOW = "allow";
	/** 规则-不允许 */
	public static final String RULE_DISALLOW = "disallow";

	/**
	 * 规则
	 * 
	 * @return
	 */
	public String getRule();

	/**
	 * 是否匹配
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isMatch(String uri);

}
