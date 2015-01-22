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
package com.foreach.across.modules.adminweb.config;

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.events.AdminWebUrlRegistry;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import com.foreach.across.modules.spring.security.filters.LocaleChangeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class AdminWebSecurityConfiguration extends SpringSecurityWebConfigurerAdapter
{
	@Autowired
	private AcrossEventPublisher publisher;

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private AdminWebModuleSettings settings;

	@Autowired(required = false)
	@Qualifier(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	private LocaleResolver localeResolver;

	@Override
	@SuppressWarnings("SignatureDeclareThrowsException")
	public void configure( HttpSecurity root ) throws Exception {
		HttpSecurity http = root.antMatcher( adminWeb.path( "/**" ) );

		// Allow locale to be changed before security applied
		if ( localeResolver != null ) {
			http.addFilterBefore( new LocaleChangeFilter( localeResolver ), SecurityContextPersistenceFilter.class );
		}

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry =
				http.authorizeRequests();

		publisher.publish( new AdminWebUrlRegistry( adminWeb, urlRegistry ) );

		// Only users with the "access administration " permission can login
		urlRegistry.anyRequest().hasAuthority( "access administration" ).and().formLogin().defaultSuccessUrl(
				adminWeb.path( "/" ) ).loginPage( adminWeb.path( "/login" ) ).permitAll().and().logout().permitAll();

		configureRememberMe( http );
	}

	@SuppressWarnings("SignatureDeclareThrowsException")
	private void configureRememberMe( HttpSecurity http ) throws Exception {
		if ( adminWeb.getSettings().isRememberMeEnabled() ) {
			String rememberMeKey = settings.getProperty( AdminWebModuleSettings.REMEMBER_ME_KEY );
			int rememberMeValiditySeconds = settings.getProperty(
					AdminWebModuleSettings.REMEMBER_ME_TOKEN_VALIDITY_SECONDS, Integer.class );

			http.rememberMe().key( rememberMeKey ).tokenValiditySeconds( rememberMeValiditySeconds );
		}
	}
}
