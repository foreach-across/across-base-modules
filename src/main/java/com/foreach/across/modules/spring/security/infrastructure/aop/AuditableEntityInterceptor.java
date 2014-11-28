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

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.hibernate.business.AuditableEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Set Auditable-related properties whenever an Auditable-entity is created or updated.
 *
 * @author Wim Tibackx
 */
@AcrossDepends(required = {"AcrossHibernateModule", "SpringSecurityAclModule"})
public class AuditableEntityInterceptor extends EntityInterceptorAdapter<Auditable>
{

	@Override
	public void  beforeCreate( Auditable entity ) {
		entity.setCreatedDate( new Date( ) );
		entity.setLastModifiedDate( new Date() );
		if (isAuthenticated()) {
			Collection<Type> types = TypeUtils.getTypeArguments( entity.getClass(), Auditable.class ) .values();
			Object createdBy = getAuditableUser( (Class) types.iterator().next() );
			entity.setCreatedBy( createdBy );
			entity.setLastModifiedBy( createdBy );
		}
	}

	private Object getAuditableUser( Class typeVariable ) {
		Object createdBy = null;
		if (typeVariable.isAssignableFrom( String.class )) {
			createdBy = currentSecurityPrincipal().getPrincipalName();

		} else if (typeVariable.isAssignableFrom( SecurityPrincipal.class )) {
			createdBy = currentSecurityPrincipal();
		}
		return createdBy;
	}

	@Override
	public void beforeUpdate( Auditable entity ) {
		entity.setLastModifiedDate( new Date() );
		if (isAuthenticated()) {
			Object lastModifiedBy = getAuditableUser(  new ArrayList<>( TypeUtils.getTypeArguments( entity.getClass(),
			                                                                                        Auditable.class )
			                                                                     .values() ).get( 0 ).getClass() );
			entity.setLastModifiedBy( lastModifiedBy );
		}
	}

	/**
	 * @return The current SecurityPrincipal or null in case there is no instance of SecurityPrincipal attached.
	 */
	private SecurityPrincipal currentSecurityPrincipal() {
		if ( isAuthenticated() ) {
			Object principal = currentAuthentication().getPrincipal();

			if ( principal instanceof SecurityPrincipal ) {
				return (SecurityPrincipal) principal;
			}
		}

		return null;
	}

	private boolean isAuthenticated() {
		Authentication authentication = currentAuthentication();
		return authentication != null && authentication.isAuthenticated();
	}

	private Authentication currentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
