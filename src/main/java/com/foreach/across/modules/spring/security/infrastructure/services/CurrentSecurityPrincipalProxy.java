package com.foreach.across.modules.spring.security.infrastructure.services;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;

/**
 * @author Arne Vandamme
 */
public interface CurrentSecurityPrincipalProxy extends SecurityPrincipal
{
	boolean isAuthenticated();

	boolean hasAuthority( String authority );

	boolean hasAclPermission( IdBasedEntity entity, AclPermission permission );

	<T extends SecurityPrincipal> T getPrincipal();
}
