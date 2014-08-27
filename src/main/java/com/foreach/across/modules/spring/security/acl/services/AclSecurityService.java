package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Provides easy access to ACL related services and checking.
 *
 * @author Arne Vandamme
 */
public interface AclSecurityService
{
	MutableAcl getAcl( IdBasedEntity entity );

	MutableAcl createAcl( IdBasedEntity entity );

	MutableAcl createAclWithParent( IdBasedEntity entity, IdBasedEntity parent );

	void allow( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions );

	void deleteAcl( IdBasedEntity entity, boolean deleteChildren );

	MutableAcl updateAcl( MutableAcl acl );

	void changeAclOwner( MutableAcl acl, SecurityPrincipal principal );

	boolean hasPermission( Authentication authentication, IdBasedEntity entity, AclPermission permission );

	boolean hasPermission( SecurityPrincipal principal, IdBasedEntity entity, AclPermission permission );
}
