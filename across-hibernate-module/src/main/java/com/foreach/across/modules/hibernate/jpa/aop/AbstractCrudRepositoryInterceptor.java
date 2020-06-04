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
import com.foreach.across.modules.hibernate.support.TransactionWrapper;
import com.foreach.across.modules.hibernate.support.TransactionWrapper.InvocationCallback;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Hibernate;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
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
 * Intercepts entity related methods on repository instances and executes before and after calls.
 * These calls are executed in the same transaction as the actual repository method.  If no outer transaction
 * is present, this interceptor should create one based on the transaction manager configured on the Spring Data
 * JPA repositories configuration ({@link com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories}.
 *
 * @author Andy Somers
 * @see com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories
 * @see TransactionWrapper
 */
public abstract class AbstractCrudRepositoryInterceptor implements MethodInterceptor, BeanFactoryAware
{
	static final String SAVE = "save";
	static final String SAVE_ALL = "saveAll";
	static final String DELETE = "delete";
	static final String DELETE_ALL = "deleteAll";

	private final Collection<EntityInterceptor> interceptors;
	private BeanFactory beanFactory;
	private TransactionWrapper transactionWrapper = null;

	protected AbstractCrudRepositoryInterceptor( Collection<EntityInterceptor> interceptors ) {
		this.interceptors = interceptors;
	}

	@Override
	public void setBeanFactory( BeanFactory beanFactory ) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * Set the name of the {@link org.springframework.transaction.PlatformTransactionManager} that should be used
	 * for the transaction wrapping the intercept methods.
	 *
	 * @param transactionManagerName name of the transaction manager bean
	 */
	public void setTransactionManagerName( String transactionManagerName ) {
		if ( transactionManagerName != null ) {
			transactionWrapper = new TransactionWrapper();
			transactionWrapper.setTransactionManagerBeanName( transactionManagerName );
			transactionWrapper.setBeanFactory( beanFactory );
			transactionWrapper.afterPropertiesSet();
		}
		else {
			transactionManagerName = null;
		}
	}

	@Override
	public Object invoke( MethodInvocation invocation ) throws Throwable {
		Method method = invocation.getMethod();
		if ( JpaRepositoryPointcut.isEntityMethod( method ) ) {
			InvocationCallback<Object> handler = determineCallbackMethod( invocation );

			if ( handler != null ) {
				if ( transactionWrapper != null ) {
					return transactionWrapper.invokeWithinTransaction( handler );
				}

				return handler.invoke();
			}
		}

		return invocation.proceed();
	}

	protected InvocationCallback<Object> determineCallbackMethod( final MethodInvocation invocation ) {
		Class<?> entityClass = getEntityClass( invocation );
		Method method = invocation.getMethod();

		Object[] arguments = invocation.getArguments();
		String methodName = method.getName();
		switch ( methodName ) {
			case DELETE_ALL:
				return () -> {
					if ( arguments.length == 0 ) {
						Collection<EntityInterceptor> interceptorsForDeleteAll
								= findInterceptorsToApply( entityClass, getInterceptors() );
						callBeforeDeleteAll( interceptorsForDeleteAll, entityClass );

						Object returnValueForDeleteAll = invocation.proceed();
						callAfterDeleteAll( interceptorsForDeleteAll, entityClass );

						return returnValueForDeleteAll;
					}
					else {
						Object entityObject = arguments[0];

						Collection<EntityInterceptor> interceptorsForDelete
								= findInterceptorsToApply( entityClass, getInterceptors() );

						for ( Object o : (Iterable) entityObject ) {
							callBeforeDelete( interceptorsForDelete, o );
						}
						Object returnValueForDelete = invocation.proceed();
						for ( Object o : (Iterable) entityObject ) {
							callAfterDelete( interceptorsForDelete, o );
						}
						return returnValueForDelete;
					}
				};
			case DELETE:
				return () -> {
					Object entityObject = arguments[0];

					Class<?> targetClass = method.getParameterTypes()[0];
					Class<?> userClass = ClassUtils.getUserClass( targetClass );

					if ( Iterable.class.isAssignableFrom( userClass ) ) {
						Collection<EntityInterceptor> interceptorsForDelete
								= findInterceptorsToApply( entityClass, getInterceptors() );

						for ( Object o : (Iterable) entityObject ) {
							callBeforeDelete( interceptorsForDelete, o );
						}
						Object returnValueForDelete = invocation.proceed();
						for ( Object o : (Iterable) entityObject ) {
							callAfterDelete( interceptorsForDelete, o );
						}
						return returnValueForDelete;
					}
					else {
						Class<?> entityClassForDelete = ClassUtils.getUserClass( Hibernate.getClass( entityObject ) );

						Collection<EntityInterceptor> interceptorsForDelete
								= findInterceptorsToApply( entityClassForDelete, getInterceptors() );

						callBeforeDelete( interceptorsForDelete, entityObject );

						Object returnValueForDelete = invocation.proceed();

						callAfterDelete( interceptorsForDelete, entityObject );

						return returnValueForDelete;
					}
				};
			case SAVE:
			case SAVE_ALL:
				return () -> {
					Object objectToSave = arguments[0];
					Class<?> targetClassToSave = method.getParameterTypes()[0];
					Class<?> userClassToSave = ClassUtils.getUserClass( targetClassToSave );
					if ( Iterable.class.isAssignableFrom( userClassToSave ) ) {
						IdentityHashMap<Persistable, Boolean> objects = new IdentityHashMap<>();
						Collection<EntityInterceptor> interceptors1
								= findInterceptorsToApply( entityClass, getInterceptors() );

						for ( Object o : (Iterable) objectToSave ) {
							boolean isNew = ( (Persistable) o ).isNew();
							objects.put( (Persistable) o, isNew );
						}
						for ( Map.Entry<Persistable, Boolean> persistableBooleanEntry : objects.entrySet() ) {
							Boolean forCreate = persistableBooleanEntry.getValue();
							if ( forCreate ) {
								callBeforeCreate( interceptors1, persistableBooleanEntry.getKey() );
							}
							else {
								callBeforeUpdate( interceptors1, persistableBooleanEntry.getKey() );
							}
						}

						Object returnValueForSave = invocation.proceed();
						for ( Map.Entry<Persistable, Boolean> persistableBooleanEntry : objects.entrySet() ) {
							Boolean forCreate = persistableBooleanEntry.getValue();
							if ( forCreate ) {
								callAfterCreate( interceptors1, persistableBooleanEntry.getKey() );
							}
							else {
								callAfterUpdate( interceptors1, persistableBooleanEntry.getKey() );
							}
						}
						return returnValueForSave;
					}
					else {
						Class<?> entityClassForSave = ClassUtils.getUserClass( Hibernate.getClass( objectToSave ) );

						Collection<EntityInterceptor> interceptorsForSave = findInterceptorsToApply( entityClassForSave,
						                                                                             getInterceptors() );

						boolean isNew = ( (Persistable) objectToSave ).isNew();
						if ( isNew ) {
							callBeforeCreate( interceptorsForSave, objectToSave );
						}
						else {
							callBeforeUpdate( interceptorsForSave, objectToSave );
						}

						Object returnValueForSave = invocation.proceed();

						if ( isNew ) {
							callAfterCreate( interceptorsForSave, objectToSave );
						}
						else {
							callAfterUpdate( interceptorsForSave, objectToSave );
						}

						return returnValueForSave;
					}
				};
			default:
				return null;
		}
	}

	protected Class<?> getEntityClass( MethodInvocation invocation ) {
		Object target = invocation instanceof ProxyMethodInvocation
				? ( (ProxyMethodInvocation) invocation ).getProxy()
				: invocation.getThis();

		return TypeDescriptor.forObject( target )
		                     .upcast( CrudRepository.class ).getResolvableType().getGeneric( 0 )
		                     .resolve();
	}

	@SuppressWarnings("unchecked")
	protected Collection<EntityInterceptor> findInterceptorsToApply( Class<?> entityClass,
	                                                                 Collection<EntityInterceptor> interceptors ) {
		Collection<EntityInterceptor> matchingInterceptors = new ArrayList<>();

		for ( EntityInterceptor candidate : interceptors ) {
			if ( candidate.handles( entityClass ) ) {
				matchingInterceptors.add( candidate );
			}
		}

		return matchingInterceptors;
	}

	@SuppressWarnings("unchecked")
	public void callBeforeCreate( Collection<EntityInterceptor> interceptors,
	                              Object entity ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.beforeCreate( entity );
		}
	}

	@SuppressWarnings("unchecked")
	public void callAfterCreate( Collection<EntityInterceptor> interceptors,
	                             Object entity ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.afterCreate( entity );
		}
	}

	@SuppressWarnings("unchecked")
	public void callBeforeUpdate( Collection<EntityInterceptor> interceptors,
	                              Object entity ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.beforeUpdate( entity );
		}
	}

	@SuppressWarnings("unchecked")
	public void callAfterUpdate( Collection<EntityInterceptor> interceptors,
	                             Object entity ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.afterUpdate( entity );
		}
	}

	@SuppressWarnings("unchecked")
	public void callBeforeDelete( Collection<EntityInterceptor> interceptors,
	                              Object entity ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.beforeDelete( entity );
		}
	}

	@SuppressWarnings("unchecked")
	public void callAfterDelete( Collection<EntityInterceptor> interceptors,
	                             Object entity ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.afterDelete( entity );
		}
	}

	@SuppressWarnings("unchecked")
	public void callBeforeDeleteAll( Collection<EntityInterceptor> interceptors,
	                                 Class entityClass ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.beforeDeleteAll( entityClass );
		}
	}

	@SuppressWarnings("unchecked")
	public void callAfterDeleteAll( Collection<EntityInterceptor> interceptors,
	                                Class entityClass ) {
		for ( EntityInterceptor interceptor : interceptors ) {
			interceptor.afterDeleteAll( entityClass );
		}
	}

	public Collection<EntityInterceptor> getInterceptors() {
		return interceptors;
	}
}
