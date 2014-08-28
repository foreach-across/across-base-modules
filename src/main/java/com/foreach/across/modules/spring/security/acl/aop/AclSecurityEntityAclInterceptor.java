package com.foreach.across.modules.spring.security.acl.aop;

import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.acl.support.IdBasedEntityAclInterceptor;

/**
 * @author Arne Vandamme
 */
public class AclSecurityEntityAclInterceptor extends IdBasedEntityAclInterceptor<AclSecurityEntity>
{
	public AclSecurityEntityAclInterceptor( AclSecurityService aclSecurityService ) {
		super( aclSecurityService );
	}

	@Override
	public void afterCreate( AclSecurityEntity entity ) {
		AclSecurityEntity parent = entity.getParent();

		if ( parent != null ) {
			aclSecurityService.createAclWithParent( entity, parent );
		}
		else {
			aclSecurityService.createAcl( entity );
		}
	}

	@Override
	public void afterUpdate( AclSecurityEntity entity ) {
		aclSecurityService.changeAclParent( entity, entity.getParent() );
	}

	@Override
	public void beforeDelete( AclSecurityEntity entity, boolean isSoftDelete ) {

	}
}
