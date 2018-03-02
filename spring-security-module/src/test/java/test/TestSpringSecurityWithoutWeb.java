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
package test;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.*;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestSpringSecurityWithoutWeb.Config.class)
public class TestSpringSecurityWithoutWeb
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Autowired(required = false)
	private SecurityPrincipalService securityPrincipalService;

	@Autowired(required = false)
	private SecurityPrincipalLabelResolverStrategy securityPrincipalLabelResolverStrategy;

	@Autowired(required = false)
	private CurrentSecurityPrincipalProxy currentPrincipal;

	@Autowired
	private SecurityPrincipalRetrievalStrategy principalRetrievalStrategy;

	@Autowired
	private SecurityPrincipalLabelResolver labelResolver;

	@Autowired
	private ConversionService conversionService;

	@After
	public void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void authenticationManagerBuilderShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME,
		                                                            AuthenticationManagerBuilder.class ) );
	}

	@Test
	public void securityPrincipalServiceShouldExist() {
		assertNotNull( securityPrincipalService );
	}

	@Test
	public void securityPrincipalLabelResolverStrategyShouldExist() {
		assertNotNull( securityPrincipalLabelResolverStrategy );
		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( labelResolver.resolvePrincipalLabel( principal ) ).thenReturn( Optional.of( "test" ) );

		assertEquals( "test", securityPrincipalLabelResolverStrategy.resolvePrincipalLabel( principal ) );
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

	@Test
	public void allowableActionCanAlwaysBeConverted() {
		assertEquals( AllowableAction.CREATE, conversionService.convert( "create", AllowableAction.class ) );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( springSecurityModule() );
		}

		private SpringSecurityModule springSecurityModule() {
			return new SpringSecurityModule();
		}

		@Bean
		public SecurityPrincipalRetrievalStrategy principalRetrievalStrategy() {
			return mock( SecurityPrincipalRetrievalStrategy.class );
		}

		@Bean
		public SecurityPrincipalLabelResolver labelResolver() {
			return mock( SecurityPrincipalLabelResolver.class );
		}
	}
}
