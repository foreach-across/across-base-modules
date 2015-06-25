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

import com.foreach.across.core.AcrossException;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptor;
import com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptorAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.List;

@Configuration
public class ModuleBasicRepositoryInterceptorConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryInterceptorAdvisor basicRepositoryInterceptorAdvisor() {
		List<BasicRepositoryInterceptor> interceptors
				= contextBeanRegistry.getBeansOfType( BasicRepositoryInterceptor.class, true );

		if ( interceptors == null ) {
			throw new AcrossException( "Expected at least one BasicRepositoryInterceptor bean to be available." );
		}

		BasicRepositoryInterceptorAdvisor advisor = new BasicRepositoryInterceptorAdvisor();
		advisor.setAdvice( interceptors.get( 0 ) );
		advisor.setOrder( BasicRepositoryInterceptorAdvisor.INTERCEPT_ORDER );

		return advisor;
	}
}
