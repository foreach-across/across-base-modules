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
package com.foreach.across.modules.hibernate.jpa.repositories;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.jpa.aop.JpaRepositoryInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.repository.util.TxUtils;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Extension to {@link JpaRepositoryFactoryBean} that adds a
 * {@link com.foreach.across.modules.hibernate.jpa.aop.JpaRepositoryInterceptor} to the
 * {@link org.springframework.data.jpa.repository.JpaRepository} it creates.
 *
 * @author Arne Vandamme
 */
public class EntityInterceptingJpaRepositoryFactoryBean extends JpaRepositoryFactoryBean
{
	private String transactionManagerName = TxUtils.DEFAULT_TRANSACTION_MANAGER;
	private AcrossContextBeanRegistry acrossContextBeanRegistry;

	@Autowired
	public void setAcrossContextBeanRegistry( AcrossContextBeanRegistry acrossContextBeanRegistry ) {
		this.acrossContextBeanRegistry = acrossContextBeanRegistry;
	}

	@Override
	public void setTransactionManager( String transactionManager ) {
		this.transactionManagerName
				= transactionManager == null ? TxUtils.DEFAULT_TRANSACTION_MANAGER : transactionManager;
		super.setTransactionManager( transactionManager );
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory( EntityManager entityManager ) {
		RepositoryFactorySupport repositoryFactory = super.createRepositoryFactory( entityManager );
		repositoryFactory.addRepositoryProxyPostProcessor(
				new EntityInterceptorProxyPostProcessor( acrossContextBeanRegistry, transactionManagerName )
		);
		return repositoryFactory;
	}

	public static class EntityInterceptorProxyPostProcessor implements RepositoryProxyPostProcessor
	{
		public static final Logger LOG = LoggerFactory.getLogger( EntityInterceptorProxyPostProcessor.class );

		private final AcrossContextBeanRegistry contextBeanRegistry;
		private final String transactionManagerName;

		public EntityInterceptorProxyPostProcessor( AcrossContextBeanRegistry beanRegistry,
		                                            String transactionManagerName ) {
			this.contextBeanRegistry = beanRegistry;
			this.transactionManagerName = transactionManagerName;
		}

		@Override
		public void postProcess( ProxyFactory factory, RepositoryInformation repositoryInformation ) {
			// request as map (so ordering applies) but ignore internals
			List<JpaRepositoryInterceptor> interceptors
					= contextBeanRegistry.getBeansOfType( JpaRepositoryInterceptor.class, false );

			if ( !interceptors.isEmpty() ) {
				if ( interceptors.size() > 1 ) {
					LOG.warn( "More than one JpaRepositoryInterceptor bean was found: {} in total.",
					          interceptors.size() );
				}

				if ( !Persistable.class.isAssignableFrom( repositoryInformation.getDomainType() ) ) {
					LOG.warn(
							"JPA repository {} detected without Persistable type parameter - entity interception is not possible.",
							repositoryInformation.getRepositoryInterface() );
				}

				JpaRepositoryInterceptor jpaRepositoryInterceptor = interceptors.get( 0 );
				jpaRepositoryInterceptor.setTransactionManagerName( transactionManagerName );

				factory.addAdvice( jpaRepositoryInterceptor );
			}
			else {
				LOG.info( "No JpaRepositoryInterceptor found - repository intercepting was possibly disabled." );
			}
		}
	}
}
