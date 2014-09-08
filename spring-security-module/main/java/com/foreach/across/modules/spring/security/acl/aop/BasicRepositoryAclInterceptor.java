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
package com.foreach.across.modules.spring.security.acl.aop;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.hibernate.repositories.Undeletable;
import com.foreach.across.modules.spring.security.acl.support.IdBasedEntityAclInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.ClassUtils;

import java.util.Collection;

/**
 * Intercepts persistence calls on a {@link com.foreach.across.modules.hibernate.repositories.BasicRepository} to
 * allow external management of the ACL attached to it.
 *
 * @author Arne Vandamme
 */
public class BasicRepositoryAclInterceptor implements MethodInterceptor
{
	private static final Logger LOG = LoggerFactory.getLogger( BasicRepositoryAclInterceptor.class );

	static final String CREATE = "create";
	static final String UPDATE = "update";
	static final String DELETE = "delete";

	private final Collection<IdBasedEntityAclInterceptor> interceptors;

	public BasicRepositoryAclInterceptor( Collection<IdBasedEntityAclInterceptor> interceptors ) {
		this.interceptors = interceptors;
	}

	@Override
	public Object invoke( MethodInvocation invocation ) throws Throwable {
		if ( BasicRepositoryAclPointcut.isEntityMethod( invocation.getMethod() ) ) {
			Object entityObject = invocation.getArguments()[0];

			if ( entityObject instanceof IdBasedEntity ) {
				String methodName = invocation.getMethod().getName();
				IdBasedEntity entity = (IdBasedEntity) entityObject;
				IdBasedEntityAclInterceptor interceptor = findInterceptorToApply( entity );

				if ( interceptor != null ) {
					callBefore( interceptor, methodName, entity );

					Object returnValue = invocation.proceed();

					callAfter( interceptor, methodName, entity );

					return returnValue;
				}
			}
			else {
				LOG.debug( "Not invoking ACL interceptor because the argument of {} was not an IdBasedEntity",
				           invocation.getMethod() );
			}
		}

		return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	private IdBasedEntityAclInterceptor findInterceptorToApply( IdBasedEntity entity ) {
		Class<?> entityClass = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( entity ) );

		IdBasedEntityAclInterceptor fallback = null;

		for ( IdBasedEntityAclInterceptor candidate : interceptors ) {
			if ( candidate.getEntityClass().equals( entityClass ) ) {
				return candidate;
			}
			else if ( candidate.getEntityClass().isAssignableFrom( entityClass ) ) {
				fallback = candidate;
			}
		}

		return fallback;
	}

	@SuppressWarnings("unchecked")
	private void callBefore( IdBasedEntityAclInterceptor interceptor, String methodName, IdBasedEntity entity ) {
		switch ( methodName ) {
			case CREATE:
				interceptor.beforeCreate( entity );
				break;
			case UPDATE:
				interceptor.beforeUpdate( entity );
				break;
			case DELETE:
				interceptor.beforeDelete( entity, entity instanceof Undeletable );
				break;
		}
	}

	@SuppressWarnings("unchecked")
	private void callAfter( IdBasedEntityAclInterceptor interceptor, String methodName, IdBasedEntity entity ) {
		switch ( methodName ) {
			case CREATE:
				interceptor.afterCreate( entity );
				break;
			case UPDATE:
				interceptor.afterUpdate( entity );
				break;
			case DELETE:
				interceptor.afterDelete( entity, entity instanceof Undeletable );
				break;
		}
	}
}
