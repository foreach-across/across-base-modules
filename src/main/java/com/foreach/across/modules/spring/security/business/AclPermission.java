package com.foreach.across.modules.spring.security.business;

import org.springframework.security.acls.domain.BasePermission;

/**
 * @author Arne Vandamme
 */
public abstract class AclPermission extends BasePermission
{
	protected AclPermission( int mask ) {
		super( mask );
	}

	protected AclPermission( int mask, char code ) {
		super( mask, code );
	}
}
