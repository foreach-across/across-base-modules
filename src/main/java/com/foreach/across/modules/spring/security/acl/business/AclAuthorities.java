package com.foreach.across.modules.spring.security.acl.business;

/**
 * @author Arne Vandamme
 */
public interface AclAuthorities
{
	/**
	 * Global authority that allows the authentication to take ownership of an ACL.
	 */
	String TAKE_OWNERSHIP = "acl take ownership";

	/**
	 * Global authority that allows an ACL to be modified (add/delete aces).
	 */
	String MODIFY_ACL = "acl modify";

	/**
	 * Global authority that allows the auditing settings of an ACL to be modified.
	 */
	String AUDIT_ACL = "acl audit";

}
