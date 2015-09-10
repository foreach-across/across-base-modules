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
package com.foreach.across.modules.spring.security.infrastructure.aop;

import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;

/**
 * Set Auditable-related properties whenever an Auditable-entity is created or updated.
 *
 * @author Wim Tibackx
 */
public class AuditableEntityInterceptor extends EntityInterceptorAdapter<Auditable>
{
	@Autowired
	private CurrentSecurityPrincipalProxy currentPrincipal;

	@Override
	@SuppressWarnings("unchecked")
	public void beforeCreate( Auditable entity ) {
		Date createdDate = entity.getCreatedDate() == null ? new Date() : entity.getCreatedDate();
		entity.setCreatedDate( createdDate );
		entity.setLastModifiedDate( createdDate );

		Object createdBy = entity.getCreatedBy() == null ? getAuditablePrincipal( entity ) : entity.getCreatedBy();
		entity.setCreatedBy( createdBy );
		entity.setLastModifiedBy( createdBy );
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeUpdate( Auditable entity ) {
		entity.setLastModifiedDate( new Date() );

		Object lastModifiedBy = getAuditablePrincipal( entity );
		entity.setLastModifiedBy( lastModifiedBy );
	}

	@SuppressWarnings("unchecked")
	private Object getAuditablePrincipal( Auditable entity ) {
		Collection<Type> types = TypeUtils.getTypeArguments( entity.getClass(), Auditable.class ).values();
		Class typeVariable = (Class) types.iterator().next();
		Object createdBy = null;

		if ( typeVariable.isAssignableFrom( String.class ) ) {
			createdBy = currentPrincipal.getPrincipalName();
		}
		else if ( typeVariable.isAssignableFrom( SecurityPrincipal.class ) ) {
			createdBy = currentPrincipal.getPrincipal();
		}

		return createdBy;
	}
}
