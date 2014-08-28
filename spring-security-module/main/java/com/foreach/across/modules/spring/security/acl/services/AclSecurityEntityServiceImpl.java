package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.dto.AclSecurityEntityDto;
import com.foreach.across.modules.spring.security.acl.repositories.AclSecurityEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 * @see com.foreach.across.modules.spring.security.acl.aop.AclSecurityEntityAclInterceptor
 */
@Service
public class AclSecurityEntityServiceImpl implements AclSecurityEntityService
{
	@Autowired
	private AclSecurityEntityRepository aclSecurityEntityRepository;

	@Override
	public AclSecurityEntity getSecurityEntityById( long id ) {
		return aclSecurityEntityRepository.getById( id );
	}

	@Override
	public AclSecurityEntity getSecurityEntityByName( String name ) {
		return aclSecurityEntityRepository.getByName( name );
	}

	@Transactional
	@Override
	public AclSecurityEntity save( AclSecurityEntityDto securityEntityDto ) {
		return BasicServiceHelper.save( securityEntityDto, AclSecurityEntity.class, aclSecurityEntityRepository );
	}
}
