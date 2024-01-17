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
import com.foreach.across.core.context.support.AcrossOrderUtils;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.ClassUtils;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.List;

/**
 * Configures Spring security support in an AcrossWeb enabled context.
 */
@RequiredArgsConstructor
@Slf4j
//@EnableWebSecurity
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
			List<WebSecurityConfigurer<? extends SecurityBuilder<Filter>>> list = webSecurityConfigurers.getWebSecurityConfigurers();
			list.sort( AnnotationAwareOrderComparator.INSTANCE );
			list.forEach( cfg -> {
				              int order = AcrossOrderUtils.findOrder( cfg );
				              LOG.debug( " {} - {}", order, cfg );
			              }
			);
		}

		return webSecurityConfigurers;
	}

/*
	@Bean
	public AcrossOrderedHttpSecurityBuilderSet autowiredHttpSecurityBuildersIgnoreParents( HttpSecurity http, AcrossModuleInfo moduleInfo ) {
		AcrossOrderedHttpSecurityBuilderSet securityBuilders = new AcrossOrderedHttpSecurityBuilderSet( http, moduleInfo );

		if ( securityBuilders.isEmpty() ) {
			throw new IllegalStateException(
					"At least one non-null instance of AcrossWebSecurityConfigurer or WebSecurityConfigurer should be present in the Across context." );
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Applying the following SecurityBuilder in order:" );
			List<SecurityBuilder<? extends SecurityFilterChain>> list = securityBuilders.getHttpSecurityBuilders();
			list.sort( AnnotationAwareOrderComparator.INSTANCE );
			list.forEach( cfg -> {
				              int order = AcrossOrderUtils.findOrder( cfg );
				              LOG.debug( " {} - {}", order, cfg );
			              }
			);
		}

		return securityBuilders;
	}
*/

	@Bean
	public AuthenticationTrustResolver authenticationTrustResolver( SecurityInfrastructure securityInfrastructure ) {
		return securityInfrastructure.authenticationTrustResolver();
	}

	/**
	 * Workaround for the default activation in {@link org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration}
	 * Previously, SSM would remove the bean definition in that class, it seems more appropriate to let {@link ConditionalOnDefaultWebSecurity} not match.
	 * <p>
	 * This method creates a default {@link WebSecurityConfigurerAdapter} bean that allows all requests by default, with lowest precedence.
	 * You can override it by specifying your own deferred {@link com.foreach.across.core.annotations.ModuleConfiguration} in your application.
	 * <p>
	 * This {@code @ModuleConfiguration} should return a {@link WebSecurityConfigurerAdapter} bean with your permission logic.
	 *
	 * @since 4.2.0
	 */
	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	@ConditionalOnMissingBean
	public WebSecurityConfigurerAdapter springSecurityModuleWebSecurityConfiguration( AcrossModuleInfo moduleInfo ) {
		return new WebSecurityConfigurerAdapter()
		{
			@Override
			public void init( WebSecurity web ) {
				// Avoid registering any filter chains, since permitAll() still restricts csrf().
				// Overriding init() here instead of configure() because an empty configure() will still construct getHttp()
				// getHttp() would override the PrivilegeEvaluator
				// Basically this empty bean exists but does nothing and only exists to fool SpringBootWebSecurityConfiguration
			}



/*
			@Override
			protected void configure( HttpSecurity http ) throws Exception {
				// super.configure( http );
				AcrossOrderedHttpSecurityBuilderSet securityBuilders = new AcrossOrderedHttpSecurityBuilderSet( http, moduleInfo );
				for ( SecurityBuilder<? extends SecurityFilterChain> securityBuilder : securityBuilders ) {
					securityBuilder.build(); // TODO the result isn't registered as a bean
				}
			}
*/
		};
	}

/*
	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE)
	@ConditionalOnMissingBean
	public SecurityFilterChain springSecurityModuleHttpSecurityBuilder() {
		return new SecurityFilterChain()
		{
			@Override
			public boolean matches( HttpServletRequest request ) {
				return false;
			}

			@Override
			public List<Filter> getFilters() {
				return null;
			}
		};
	}
*/
//	@Bean
//	@Order(Ordered.LOWEST_PRECEDENCE)
//	@ConditionalOnMissingBean
//	public SecurityFilterChain filterChain( HttpSecurity http, AcrossOrderedWebSecurityConfigurerSet configurerSet ) throws Exception {
///*
//		http
//				.authorizeHttpRequests((authz) -> authz
//						.anyRequest().authenticated()
//				)
//				.httpBasic( Customizer.withDefaults());
//*/
//		for ( AcrossWebSecurityConfigurer configurer : configurerSet.getConfigurers() ) {
//			configurer.configure( http );
//		}
//
//		return http.build();
//	}

//	@Bean
//	@Order(Ordered.LOWEST_PRECEDENCE)
//	@ConditionalOnMissingBean
//	public SecurityFilterChain filterChain2( HttpSecurity http, AcrossOrderedWebSecurityConfigurerSet configurerSet ) throws Exception {
///*
//		http
//				.authorizeHttpRequests((authz) -> authz
//						.anyRequest().authenticated()
//				)
//				.httpBasic( Customizer.withDefaults());
//*/
//		for ( AcrossWebSecurityConfigurer configurer : configurerSet.getConfigurers() ) {
//			configurer.configure( http );
//		}
//
//		return http.build();
//	}

}

