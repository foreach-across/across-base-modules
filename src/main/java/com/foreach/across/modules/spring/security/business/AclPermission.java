package com.foreach.across.modules.spring.security.business;

import org.springframework.security.acls.domain.AbstractPermission;

/**
 * Redefined instance of {@link org.springframework.security.acls.domain.BasePermission} to avoid
 * confusion with the Permission from UserModule.
 * }
 *
 * @author Arne Vandamme
 */
public class AclPermission extends AbstractPermission
{
	public static final AclPermission READ = new AclPermission( 1 << 0, 'R' ); // 1
	public static final AclPermission WRITE = new AclPermission( 1 << 1, 'W' ); // 2
	public static final AclPermission CREATE = new AclPermission( 1 << 2, 'C' ); // 4
	public static final AclPermission DELETE = new AclPermission( 1 << 3, 'D' ); // 8
	public static final AclPermission ADMINISTRATION = new AclPermission( 1 << 4, 'A' ); // 16

	protected AclPermission( int mask ) {
		super( mask );
	}

	protected AclPermission( int mask, char code ) {
		super( mask, code );
	}
}
