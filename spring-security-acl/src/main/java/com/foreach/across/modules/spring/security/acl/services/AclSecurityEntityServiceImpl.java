/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.repositories.AclSecurityEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		return aclSecurityEntityRepository.findOne( id );
	}

	@Override
	public AclSecurityEntity getSecurityEntityByName( String name ) {
		return aclSecurityEntityRepository.findByName( name );
	}

	@Override
	public AclSecurityEntity save( AclSecurityEntity securityEntityDto ) {
		return BasicServiceHelper.save( securityEntityDto, aclSecurityEntityRepository );
	}
}
