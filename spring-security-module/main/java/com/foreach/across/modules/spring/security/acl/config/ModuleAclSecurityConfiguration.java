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
package com.foreach.across.modules.spring.security.acl.config;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptor;
import com.foreach.across.modules.spring.security.acl.aop.BasicRepositoryAclInterceptorAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.PermissionEvaluator;

/**
 * Client module configuration that registers the PermissionEvaluator in all consuming modules.
 *
 * @author Arne Vandamme
 */
@Configuration
public class ModuleAclSecurityConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	PermissionEvaluator permissionEvaluator() {
		return contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityAclModule.NAME, PermissionEvaluator.class );
	}

	@Bean
	@AcrossDepends(required = "AcrossHibernateModule")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryAclInterceptorAdvisor aclInterceptorAdvisor() {
		BasicRepositoryAclInterceptor interceptor = contextBeanRegistry.getBeanOfTypeFromModule(
				SpringSecurityAclModule.NAME, BasicRepositoryAclInterceptor.class
		);

		BasicRepositoryAclInterceptorAdvisor advisor = new BasicRepositoryAclInterceptorAdvisor();
		advisor.setAdvice( interceptor );
		advisor.setOrder( BasicRepositoryAclInterceptorAdvisor.INTERCEPT_ORDER );

		return advisor;
	}
}
