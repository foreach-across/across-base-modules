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

package com.foreach.across.modules.spring.security.config;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.web.AcrossWebModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
@ConditionalOnAcrossModule(AcrossWebModule.NAME)
@Import({ SecurityAutoConfigurationAdapter.Importer.class, SecurityAutoConfigurationAdapter.DisableNoWebSecurityAdapter.class })
@Slf4j
public class SecurityAutoConfigurationAdapter
{
	@Bean
	@ConditionalOnMissingBean(search = SearchStrategy.ANCESTORS)
	public ServerProperties serverProperties() {
		return new ServerProperties();
	}

	/**
	 * Removes the Spring Boot basic security configurations, unless it is explicitly enabled.
	 */
	static class DisableNoWebSecurityAdapter implements BeanDefinitionRegistryPostProcessor, EnvironmentAware
	{
		public static final String SPRING_BOOT_NO_WEB_SECURITY_CONFIGURER =
				"org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration.ApplicationNoWebSecurityConfigurerAdapter";
		public static final String SPRING_BOOT_BASIC_WEB_SECURITY_CONFIGURER =
				"org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration.ApplicationWebSecurityConfigurerAdapter";
		private Environment environment;

		@Override
		public void setEnvironment( Environment environment ) {
			this.environment = environment;
		}

		@Override
		public void postProcessBeanDefinitionRegistry( BeanDefinitionRegistry registry ) throws BeansException {
			Boolean securityEnabled = environment.getProperty( "security.basic.enabled", Boolean.class );

			if ( !Boolean.TRUE.equals( securityEnabled ) ) {
				boolean explicitlyDisabled = Boolean.FALSE.equals( securityEnabled );
				LOG.info( "Disabling Spring Boot basic security - only explicitly enabling it with 'security.basic.enabled' is supported by SpringSecurityModule" );
				if ( !explicitlyDisabled && registry.containsBeanDefinition( SPRING_BOOT_NO_WEB_SECURITY_CONFIGURER ) ) {
					registry.removeBeanDefinition( SPRING_BOOT_NO_WEB_SECURITY_CONFIGURER );
				}
				if ( registry.containsBeanDefinition( SPRING_BOOT_BASIC_WEB_SECURITY_CONFIGURER ) ) {
					registry.removeBeanDefinition( SPRING_BOOT_BASIC_WEB_SECURITY_CONFIGURER );
				}
			}
		}

		@Override
		public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory ) throws BeansException {

		}
	}

	static class Importer implements DeferredImportSelector
	{
		@Override
		public String[] selectImports( AnnotationMetadata importingClassMetadata ) {
			return new String[] { AcrossWebSecurityConfiguration.class.getName() };
		}
	}
}
