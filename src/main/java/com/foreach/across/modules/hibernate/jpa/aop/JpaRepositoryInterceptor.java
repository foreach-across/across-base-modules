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
package com.foreach.across.modules.hibernate.jpa.aop;

import com.foreach.across.modules.hibernate.aop.EntityInterceptor;
import com.foreach.across.modules.hibernate.repositories.Undeletable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.domain.Persistable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intercepts persistence calls on a {@link org.springframework.data.jpa.repository.JpaRepository}.
 *
 * @author Andy Somers
 */
public class JpaRepositoryInterceptor implements MethodInterceptor
{
	static final String SAVE = "save";
	static final String DELETE = "delete";
	static final String DELETE_ALL = "deleteAll";

	private final Collection<EntityInterceptor> interceptors;

	public JpaRepositoryInterceptor( Collection<EntityInterceptor> interceptors ) {
		this.interceptors = interceptors;
	}

	@Override
	public Object invoke( MethodInvocation invocation ) throws Throwable {
		Method method = invocation.getMethod();
		if ( JpaRepositoryPointcut.isEntityMethod( method ) ) {
			Object[] arguments = invocation.getArguments();
			if ( !StringUtils.equals( DELETE_ALL, method.getName() ) ) {
				Object entityObject = arguments[0];

				Class<?> targetClass = method.getParameterTypes()[0];
				Class<?> userClass = ClassUtils.getUserClass( targetClass );

				if ( Iterable.class.isAssignableFrom( userClass ) ) {
					// handle iterable

				}
				else {
					Collection<EntityInterceptor> interceptors = findInterceptorsToApply( entityObject,
					                                                                      this.interceptors );

					boolean isNew = ( (Persistable) entityObject ).isNew();
					String methodName = method.getName();
					callBefore( interceptors, methodName, entityObject, isNew );

					Object returnValue = invocation.proceed();

					callAfter( interceptors, methodName, entityObject, isNew );

					return returnValue;
				}
			}
			else {
				// handle deleteAll
			}
		}

		return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	private Collection<EntityInterceptor> findInterceptorsToApply( Object entity,
	                                                               Collection<EntityInterceptor> interceptors ) {
		Class<?> entityClass = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( entity ) );

		Collection<EntityInterceptor> matchingInterceptors = new ArrayList<>();

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
	private void callBefore( Collection<EntityInterceptor> interceptors,
	                         String methodName,
	                         Object entity,
	                         boolean isNew ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			switch ( methodName ) {
				case SAVE:
					if ( isNew ) {
						interceptor.beforeCreate( entity );
					}
					else {
						interceptor.beforeUpdate( entity );
					}
					break;
				case DELETE_ALL:
					interceptor.beforeDeleteAll();
					break;
				case DELETE:
					interceptor.beforeDelete( entity, entity instanceof Undeletable );
					break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void callAfter( Collection<EntityInterceptor> interceptors,
	                        String methodName,
	                        Object entity,
	                        boolean isNew ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			switch ( methodName ) {
				case SAVE:
					if ( isNew ) {
						interceptor.afterCreate( entity );
					}
					else {
						interceptor.afterUpdate( entity );
					}
					break;
				case DELETE_ALL:
					interceptor.afterDeleteAll();
					break;
				case DELETE:
					interceptor.afterDelete( entity, entity instanceof Undeletable );
					break;
			}
		}
	}

}
