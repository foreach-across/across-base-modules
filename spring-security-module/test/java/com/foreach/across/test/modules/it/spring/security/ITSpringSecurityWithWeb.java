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
package com.foreach.across.test.modules.it.spring.security;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurer;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITSpringSecurityWithWeb.Config.class)
public class ITSpringSecurityWithWeb
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Autowired(required = false)
	private FilterChainProxy filterChainProxy;

	@Autowired(required = false)
	private WebInvocationPrivilegeEvaluator webInvocationPrivilegeEvaluator;

	@Autowired(required = false)
	private SecurityExpressionHandler securityExpressionHandler;

	@Autowired(required = false)
	@Qualifier("requestDataValueProcessor")
	private Object requestDataValueProcessor;

	@Autowired(required = false)
	private CurrentSecurityPrincipalProxy currentPrincipal;

	@Autowired
	private SecurityPrincipalRetrievalStrategy principalRetrievalStrategy;

	@Test
	public void authenticationManagerBuilderShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME,
		                                                            AuthenticationManagerBuilder.class ) );
	}

	@Test
	public void exposedBeans() {
		assertNotNull( filterChainProxy );
		assertNotNull( securityExpressionHandler );
		assertNotNull( requestDataValueProcessor );
		assertNotNull( webInvocationPrivilegeEvaluator );
	}

	@Test
	public void currentSecurityPrincipalCanBeFetchedUsingTheRetrievalStrategy() {
		assertNotNull( currentPrincipal );
		assertFalse( currentPrincipal.isAuthenticated() );

		Authentication auth = mock( Authentication.class );
		when( auth.isAuthenticated() ).thenReturn( true );
		when( auth.getPrincipal() ).thenReturn( "principalName" );

		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principalRetrievalStrategy.getPrincipalByName( "principalName" ) ).thenReturn( principal );

		SecurityContextHolder.getContext().setAuthentication( auth );

		assertTrue( currentPrincipal.isAuthenticated() );
		assertSame( principal, currentPrincipal.getPrincipal() );
	}

	/**
	 * At least one SpringSecurityConfigurer should be present.
	 */
	@Configuration
	@OrderInModule(2)
	protected static class SimpleSpringSecurityConfigurer extends SpringSecurityWebConfigurerAdapter
	{
		@Override
		public void configure( AuthenticationManagerBuilder auth ) throws Exception {
			auth.inMemoryAuthentication().withUser( "test" ).password( "test" ).roles( "test" );
		}

		@Override
		public void configure( HttpSecurity http ) throws Exception {
			http
					.authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.formLogin().and()
					.httpBasic();
		}
	}

	@Configuration
	@OrderInModule(1)
	protected static class OtherSpringSecurityConfigurer extends SpringSecurityWebConfigurerAdapter
	{
		@Override
		public void configure( HttpSecurity http ) throws Exception {
			http.antMatcher( "/bla" ).authorizeRequests().anyRequest().denyAll();
		}
	}

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( springSecurityModule() );
		}

		@Bean
		public SpringSecurityWebConfigurer springSecurityWebConfigurer() {
			return new SimpleSpringSecurityConfigurer();
		}

		@Bean
		public OtherSpringSecurityConfigurer otherSpringSecurityConfigurer() {
			return new OtherSpringSecurityConfigurer();
		}

		private SpringSecurityModule springSecurityModule() {
			return new SpringSecurityModule();
		}

		@Bean
		public SecurityPrincipalRetrievalStrategy principalRetrievalStrategy() {
			return mock( SecurityPrincipalRetrievalStrategy.class );
		}
	}
}
