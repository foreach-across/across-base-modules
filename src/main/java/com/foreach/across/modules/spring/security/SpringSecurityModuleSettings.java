package com.foreach.across.modules.spring.security;

/**
 * @author Arne Vandamme
 */
public interface SpringSecurityModuleSettings
{
	/**
	 * Should ACL support be enabled.
	 * todo: only if true should the ACL installer be run.
	 */
	public static final String ACL_ENABLED = "springSecurity.acl.enabled";
}
