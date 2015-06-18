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
package com.foreach.across.modules.hibernate.modules.config;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.aop.CrudRepositoryInterceptor;
import com.foreach.across.modules.hibernate.jpa.aop.CrudRepositoryInterceptorAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author Andy Somers
 */
@Configuration
public class ModuleCrudRepositoryInterceptorConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CrudRepositoryInterceptorAdvisor crudInterceptorAdvisor() {
		CrudRepositoryInterceptor interceptor = contextBeanRegistry.getBeanOfTypeFromModule(
				AcrossHibernateJpaModule.NAME, CrudRepositoryInterceptor.class
		);

		CrudRepositoryInterceptorAdvisor advisor = new CrudRepositoryInterceptorAdvisor();
		advisor.setAdvice( interceptor );
		advisor.setOrder( CrudRepositoryInterceptorAdvisor.INTERCEPT_ORDER );

		return advisor;
	}

}
