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
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Intercepts persistence calls on a {@link org.springframework.data.jpa.repository.JpaRepository}.
 *
 * @author Andy Somers
 */
public class JpaRepositoryInterceptor implements MethodInterceptor
{
	static final String SAVE = "save";
	static final String SAVE_AND_FLUSH = "saveAndFlush";
	static final String DELETE = "delete";
	static final String DELETE_IN_BATCH = "deleteInBatch";
	static final String DELETE_ALL = "deleteAll";
	static final String DELETE_ALL_BATCH = "deleteAllInBatch";

	private final Collection<EntityInterceptor> interceptors;

	public JpaRepositoryInterceptor( Collection<EntityInterceptor> interceptors ) {
		this.interceptors = interceptors;
	}

	@Override
	public Object invoke( MethodInvocation invocation ) throws Throwable {
		Method method = invocation.getMethod();
		if ( JpaRepositoryPointcut.isEntityMethod( method ) ) {
			Object[] arguments = invocation.getArguments();
			String methodName = method.getName();
			if ( DELETE_ALL.equalsIgnoreCase( methodName ) || DELETE_ALL_BATCH.equalsIgnoreCase( methodName ) ) {
				// handle deleteAll
				Class<?> entityClass = TypeDescriptor.forObject( invocation.getThis() )
				                                     .upcast( CrudRepository.class ).getResolvableType().getGeneric( 0 )
				                                     .resolve();
				Collection<EntityInterceptor> interceptors = findInterceptorsToApply( entityClass,
				                                                                      this.interceptors );
				callBeforeDeleteAll( interceptors, entityClass );

				Object returnValue = invocation.proceed();
				callAfterDeleteAll( interceptors, entityClass );
				return returnValue;
			}
			else {
				Object entityObject = arguments[0];

				Class<?> targetClass = method.getParameterTypes()[0];
				Class<?> userClass = ClassUtils.getUserClass( targetClass );

				if ( Iterable.class.isAssignableFrom( userClass ) ) {
					Class<?> entityClass = TypeDescriptor.forObject( invocation.getThis() )
					                                     .upcast( CrudRepository.class ).getResolvableType().getGeneric( 0 )
					                                     .resolve();
					Iterable iterable = (Iterable) entityObject;
					IdentityHashMap<Persistable, Boolean> objects = new IdentityHashMap<>();
					Collection<EntityInterceptor> interceptors = findInterceptorsToApply( entityClass,
					                                                                      this.interceptors );

					for ( Object o : iterable ) {
						boolean isNew = ( (Persistable) o ).isNew();
						objects.put( (Persistable) o, isNew );
					}
					for ( Map.Entry<Persistable, Boolean> persistableBooleanEntry : objects.entrySet() ) {
						callBefore( interceptors, methodName, persistableBooleanEntry.getKey(),
						            persistableBooleanEntry.getValue() );
					}

					Object returnValue = invocation.proceed();
					for ( Map.Entry<Persistable, Boolean> persistableBooleanEntry : objects.entrySet() ) {
						callAfter( interceptors, methodName, persistableBooleanEntry.getKey(),
						           persistableBooleanEntry.getValue() );
					}
					return returnValue;
				}
				else {
					Class<?> entityClass = ClassUtils.getUserClass( AopProxyUtils.ultimateTargetClass( entityObject ) );

					Collection<EntityInterceptor> interceptors = findInterceptorsToApply( entityClass,
					                                                                      this.interceptors );

					boolean isNew = ( (Persistable) entityObject ).isNew();
					callBefore( interceptors, methodName, entityObject, isNew );

					Object returnValue = invocation.proceed();

					callAfter( interceptors, methodName, entityObject, isNew );

					return returnValue;
				}
			}
		}

		return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	Collection<EntityInterceptor> findInterceptorsToApply( Class<?> entityClass,
	                                                       Collection<EntityInterceptor> interceptors ) {
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
				case SAVE_AND_FLUSH:
					if ( isNew ) {
						interceptor.beforeCreate( entity );
					}
					else {
						interceptor.beforeUpdate( entity );
					}
					break;
				case DELETE:
				case DELETE_IN_BATCH:
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
				case SAVE_AND_FLUSH:
					if ( isNew ) {
						interceptor.afterCreate( entity );
					}
					else {
						interceptor.afterUpdate( entity );
					}
					break;
				case DELETE:
				case DELETE_IN_BATCH:
					interceptor.afterDelete( entity, entity instanceof Undeletable );
					break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void callBeforeDeleteAll( Collection<EntityInterceptor> interceptors,
	                                  Class entityClass ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.beforeDeleteAll( entityClass );
		}
	}

	@SuppressWarnings("unchecked")
	private void callAfterDeleteAll( Collection<EntityInterceptor> interceptors,
	                                 Class entityClass ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.afterDeleteAll( entityClass );
		}
	}
}
