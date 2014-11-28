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
package com.foreach.across.modules.hibernate.aop;

import com.foreach.across.modules.hibernate.repositories.Undeletable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Intercepts persistence calls on a {@link com.foreach.across.modules.hibernate.repositories.BasicRepository} to
 * allow external management of the ACL attached to it.
 *
 * @author Arne Vandamme
 */
public class BasicRepositoryInterceptor implements MethodInterceptor
{
	private static final Logger LOG = LoggerFactory.getLogger( BasicRepositoryInterceptor.class );

	static final String CREATE = "create";
	static final String UPDATE = "update";
	static final String DELETE = "delete";

	private final Collection<EntityInterceptor> interceptors;

	public BasicRepositoryInterceptor( Collection<EntityInterceptor> interceptors ) {
		this.interceptors = interceptors;
	}

	@Override
	public Object invoke( MethodInvocation invocation ) throws Throwable {
		if ( BasicRepositoryPointcut.isEntityMethod( invocation.getMethod() ) ) {
			Object entityObject = invocation.getArguments()[0];

			String methodName = invocation.getMethod().getName();
			Collection<EntityInterceptor> interceptor = findInterceptorToApply( entityObject, interceptors );

			callBefore( interceptor, methodName, entityObject );

			Object returnValue = invocation.proceed();

			callAfter( interceptor, methodName, entityObject );

			return returnValue;
		}

		return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	private Collection<EntityInterceptor> findInterceptorToApply( Object entity,
	                                                              Collection<EntityInterceptor> interceptors ) {
		Class<?> entityClass = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( entity ) );

		Collection<EntityInterceptor> matchingInterceptors = new ArrayList<>(  );

		for ( EntityInterceptor candidate : interceptors ) {
			if ( candidate.getEntityClass().equals( entityClass ) ) {
				matchingInterceptors.add( candidate );
			}
			else if ( candidate.getEntityClass().isAssignableFrom( entityClass ) ) {
				matchingInterceptors.add( candidate );
			}
		}

		return matchingInterceptors;
	}

	@SuppressWarnings("unchecked")
	private void callBefore( Collection<EntityInterceptor> interceptors, String methodName, Object entity ) {
		for (EntityInterceptor interceptor : interceptors) {
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
	}

	@SuppressWarnings("unchecked")
	private void callAfter( Collection<EntityInterceptor> interceptors, String methodName, Object entity ) {
		for (EntityInterceptor interceptor : interceptors) {
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
}
