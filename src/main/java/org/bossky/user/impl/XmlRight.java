package org.bossky.user.impl;

import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.bossky.common.util.Misc;
import org.bossky.user.Right;

/**
 * 简单的权限实现
 * 
 * @author daibo
 *
 */
public class XmlRight implements Right {
	/** 资源链接 */
	@Resource
	protected String uri;
	/** 规则 */
	@Resource
	protected String rule;

	private Pattern pattern;

	public XmlRight() {
	}

	public XmlRight(String uri, String rule) {
		this.uri = uri;
		this.rule = rule;
	}

	public String getRule() {
		return this.rule;
	}

	@Override
	public boolean isMatch(String uri) {
		return getPattern().matcher(uri).matches();
	}

	private Pattern getPattern() {
		if (null == pattern) {
			pattern = Pattern.compile(uri);
		}
		return pattern;
	}

	@Override
	public int hashCode() {
		return this.uri.hashCode() * 31 + this.rule.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XmlRight) {
			return Misc.eq(((XmlRight) obj).uri, uri) && Misc.eq(((XmlRight) obj).rule, rule);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[ uri = \"" + uri + "\" , rule = \"" + rule + "\"]";
	}

	public static void main(String[] args) {
		XmlRight right = new XmlRight("/[a-z/]*.do", XmlRight.RULE_ALLOW);
		System.out.println(right.getPattern().matcher("/user/user.do").matches());
	}

}
