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
package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.registry.RefreshableRegistry;
import com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptor;
import com.foreach.across.modules.hibernate.aop.EntityInterceptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * Configures intercepting the BasicRepository methods when an entity gets inserted/updated/deleted.
 */
@Configuration
public class BasicRepositoryInterceptorConfiguration
{
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BasicRepositoryInterceptor basicRepositoryInterceptor( RefreshableRegistry<EntityInterceptor> entityInterceptors ) {
		return new BasicRepositoryInterceptor( entityInterceptors );
	}
}
