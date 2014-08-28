package com.foreach.across.modules.spring.security.acl.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Repository
public class AclSecurityEntityRepositoryImpl
		extends BasicRepositoryImpl<AclSecurityEntity>
		implements AclSecurityEntityRepository
{
	@Transactional(readOnly = true)
	@Override
	public AclSecurityEntity getByName( String name ) {
		return (AclSecurityEntity) distinct()
				.add( Restrictions.eq( "name", StringUtils.lowerCase( name ) ) )
				.uniqueResult();
	}
}
