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

import com.foreach.across.modules.hibernate.jpa.repositories.EntityInterceptingJpaRepositoryFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

/**
 * @author Andy Somers, Arne Vandamme
 */
@Configuration
public class ModuleJpaRepositoryInterceptorConfiguration
{
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanDefinitionRegistryPostProcessor jpaRepositoryFactoryBeanDefinitionPostProcessor() {
		return new JpaRepositoryFactoryBeanDefinitionPostProcessor();
	}

	/**
	 * PostProcessor that alters bean definitions for {@link JpaRepositoryFactoryBean} and replaces them
	 * with {@link EntityInterceptingJpaRepositoryFactoryBean} in order to correctly support entity intercepting.
	 */
	public static class JpaRepositoryFactoryBeanDefinitionPostProcessor implements BeanDefinitionRegistryPostProcessor
	{
		@Override
		public void postProcessBeanDefinitionRegistry( BeanDefinitionRegistry registry ) throws BeansException {
			String originalBeanClassName = JpaRepositoryFactoryBean.class.getName();
			String targetBeanClassName = EntityInterceptingJpaRepositoryFactoryBean.class.getName();
			for ( String beanDefinitionName : registry.getBeanDefinitionNames() ) {
				BeanDefinition beanDefinition = registry.getBeanDefinition( beanDefinitionName );

				if ( originalBeanClassName.equals( beanDefinition.getBeanClassName() ) ) {
					beanDefinition.setBeanClassName( targetBeanClassName );
				}
			}
		}

		@Override
		public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory ) throws BeansException {

		}
	}
}
