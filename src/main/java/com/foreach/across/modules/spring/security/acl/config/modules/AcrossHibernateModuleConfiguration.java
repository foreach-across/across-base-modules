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
package com.foreach.across.modules.spring.security.acl.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.registry.IncrementalRefreshableRegistry;
import com.foreach.across.core.registry.RefreshableRegistry;
import com.foreach.across.modules.spring.security.acl.aop.AclSecurityEntityAclInterceptor;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptor;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptorAdvisor;
import com.foreach.across.modules.spring.security.acl.repositories.AclSecurityEntityRepository;
import com.foreach.across.modules.spring.security.acl.repositories.AclSecurityEntityRepositoryImpl;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityServiceImpl;
import com.foreach.across.modules.spring.security.acl.support.IdBasedEntityAclInterceptor;
import org.hibernate.SessionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.util.Assert;

/**
 * Configures intercepting the BasicRepository methods to create or delete ACLs when an entity
 * gets inserted/updated/deleted.
 * <p/>
 * Also enables the {@link com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity}.
 *
 * @author Arne Vandamme
 */
@Configuration
@AcrossDepends(required = "AcrossHibernateModule")
public class AcrossHibernateModuleConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	RefreshableRegistry<IdBasedEntityAclInterceptor> aclEntityInterceptors() {
		return new IncrementalRefreshableRegistry<>( IdBasedEntityAclInterceptor.class, true );
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryAclInterceptor aclInterceptor() {
		return new BasicRepositoryAclInterceptor( aclEntityInterceptors() );
	}

	@Bean
	SessionFactory sessionFactory() {
		ModuleInternalBeanTargetSource sessionFactorySource = new ModuleInternalBeanTargetSource();
		sessionFactorySource.setContextBeanRegistry( contextBeanRegistry );
		sessionFactorySource.setBeanType( SessionFactory.class );
		sessionFactorySource.setModule( "AcrossHibernateModule" );

		return ProxyFactory.getProxy( SessionFactory.class, sessionFactorySource );
	}

	@Bean
	public AclSecurityEntityRepository aclSecurityEntityRepository() {
		return new AclSecurityEntityRepositoryImpl();
	}

	@Bean
	public AclSecurityEntityService aclSecurityEntityService() {
		return new AclSecurityEntityServiceImpl();
	}

	@Bean
	public AclSecurityEntityAclInterceptor aclSecurityEntityAclInterceptor() {
		return new AclSecurityEntityAclInterceptor();
	}

	@Bean
	@AcrossDepends(required = "AcrossHibernateModule")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryAclInterceptorAdvisor aclInterceptorAdvisor() {
		BasicRepositoryAclInterceptorAdvisor advisor = new BasicRepositoryAclInterceptorAdvisor();
		advisor.setAdvice( aclInterceptor() );
		advisor.setOrder( BasicRepositoryAclInterceptorAdvisor.INTERCEPT_ORDER );

		return advisor;
	}

	static class ModuleInternalBeanTargetSource extends AbstractLazyCreationTargetSource
	{
		private AcrossContextBeanRegistry contextBeanRegistry;
		private Class<?> beanType;
		private String module;

		public void setContextBeanRegistry( AcrossContextBeanRegistry contextBeanRegistry ) {
			this.contextBeanRegistry = contextBeanRegistry;
		}

		public void setBeanType( Class<?> beanType ) {
			this.beanType = beanType;
		}

		public void setModule( String module ) {
			this.module = module;
		}

		@Override
		protected Object createObject() throws Exception {
			Assert.notNull( contextBeanRegistry );
			Assert.notNull( beanType );
			Assert.notNull( module );

			return contextBeanRegistry.getBeanOfTypeFromModule( module, beanType );
		}
	}
}
