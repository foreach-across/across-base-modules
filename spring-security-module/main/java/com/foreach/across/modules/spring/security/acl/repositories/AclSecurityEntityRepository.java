package com.foreach.across.modules.spring.security.acl.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;

/**
 * @author Arne Vandamme
 */
public interface AclSecurityEntityRepository extends BasicRepository<AclSecurityEntity>
{
	AclSecurityEntity getByName( String name );
}
