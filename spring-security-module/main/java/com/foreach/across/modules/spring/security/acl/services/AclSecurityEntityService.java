package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.dto.AclSecurityEntityDto;

/**
 * @author Arne Vandamme
 */
public interface AclSecurityEntityService
{
	AclSecurityEntity getSecurityEntityById( long id );

	AclSecurityEntity getSecurityEntityByName( String name );

	AclSecurityEntity save( AclSecurityEntityDto securityEntityDto );
}
