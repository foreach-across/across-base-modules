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
package com.foreach.across.modules.spring.security.configuration;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.context.support.AcrossOrderUtils;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.util.ClassUtils;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Configures Spring security support in an AcrossWeb enabled context.
 */
@RequiredArgsConstructor
@Slf4j
@EnableWebSecurity
@Import(AcrossWebSecurityConfiguration.DefaultSpringBootSecurityRemoval.class)
public class AcrossWebSecurityConfiguration
{
	private static final String CLASS_THYMELEAF_TEMPLATE_ENGINE = "org.thymeleaf.spring5.SpringTemplateEngine";
	private static final String CLASS_SPRING_SECURITY_DIALECT = "org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect";

	private final ApplicationContext applicationContext;

	@PostConstruct
	public void registerThymeleafDialect() {
		if ( shouldRegisterThymeleafDialect() ) {
			LOG.debug( "Registering Thymeleaf Spring security dialect" );

			Object springTemplateEngine = applicationContext.getBean( "springTemplateEngine" );

			if ( springTemplateEngine instanceof SpringTemplateEngine ) {
				( (SpringTemplateEngine) springTemplateEngine ).addDialect( new SpringSecurityDialect() );
				LOG.trace( "Thymeleaf Spring security dialect registered successfully." );
			}
			else {
				LOG.warn(
						"Unable to register Thymeleaf Spring security dialect as bean springTemplateEngine is not of the right type." );
			}
		}
	}

	private boolean shouldRegisterThymeleafDialect() {
		if ( applicationContext.containsBean( "springTemplateEngine" ) ) {
			ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

			if ( ClassUtils.isPresent( CLASS_THYMELEAF_TEMPLATE_ENGINE, threadClassLoader ) && ClassUtils.isPresent(
					CLASS_SPRING_SECURITY_DIALECT, threadClassLoader ) ) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Ignore any configured error controller path from security matching by default.
	 * TODO: actually security configuration should happen in the post processor module in the future,
	 * making this obsolete hopefully
	 */
//	@Bean
//	@ConditionalOnBean(ServerProperties.class)
//	public IgnoredRequestCustomizer ignoreErrorPathRequestCustomizer( ServerProperties serverProperties ) {
//		return configurer -> {
//			String result = StringUtils.cleanPath( serverProperties.getError().getPath() );
//			if ( !result.startsWith( "/" ) ) {
//				result = "/" + result;
//			}
//			configurer.antMatchers( result );
//		};
//	}

	/**
	 * Uses a custom approach to fetch the {@link WebSecurityConfigurer} instances that should be applied.
	 *
	 * @see AcrossOrderedWebSecurityConfigurerSet
	 */
	@Bean(name = "autowiredWebSecurityConfigurersIgnoreParents")
	public AcrossOrderedWebSecurityConfigurerSet autowiredWebSecurityConfigurersIgnoreParents( AcrossModuleInfo moduleInfo ) {
		AcrossOrderedWebSecurityConfigurerSet webSecurityConfigurers = new AcrossOrderedWebSecurityConfigurerSet( moduleInfo );

		if ( webSecurityConfigurers.isEmpty() ) {
			throw new IllegalStateException(
					"At least one non-null instance of AcrossWebSecurityConfigurer or WebSecurityConfigurer should be present in the Across context." );
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Applying the following WebSecurityConfigurers in order:" );
			List<WebSecurityConfigurer> list = webSecurityConfigurers.getWebSecurityConfigurers();
			list.sort( AnnotationAwareOrderComparator.INSTANCE );
			list.forEach( cfg -> {
				              int order = AcrossOrderUtils.findOrder( cfg );
				              LOG.debug( " {} - {}", order, cfg );
			              }
			);
		}

		return webSecurityConfigurers;
	}

	@Bean
	public AuthenticationTrustResolver authenticationTrustResolver( SecurityInfrastructure securityInfrastructure ) {
		return securityInfrastructure.authenticationTrustResolver();
	}

	/**
	 * Removes the default Spring Boot security rules if there is a custom AcrossWebSecurityConfigurer present.
	 * Done this way because the current implementation of regular Spring security vs Across setup uses separate
	 * configuration classes and Spring Boot thinks there never is any security configured and applies its default.
	 */
	static class DefaultSpringBootSecurityRemoval implements BeanDefinitionRegistryPostProcessor
	{
		private static final String DEFAULT_SECURITY_CONFIGURATION =
				"org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration.DefaultConfigurerAdapter";

		@Override
		public void postProcessBeanDefinitionRegistry( BeanDefinitionRegistry beanDefinitionRegistry ) throws BeansException {
			if ( beanDefinitionRegistry.containsBeanDefinition( DEFAULT_SECURITY_CONFIGURATION )
					&& !( (ListableBeanFactory) beanDefinitionRegistry ).getBean( AcrossContextBeanRegistry.class )
					                                                    .getBeansOfType( AcrossWebSecurityConfigurer.class, true )
					                                                    .isEmpty() ) {
				beanDefinitionRegistry.removeBeanDefinition( DEFAULT_SECURITY_CONFIGURATION );
			}
		}

		@Override
		public void postProcessBeanFactory( ConfigurableListableBeanFactory configurableListableBeanFactory ) throws BeansException {

		}
	}
}
