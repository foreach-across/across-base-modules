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
package com.foreach.across.test.modules.spring.security.infrastructure.services;

import com.foreach.across.modules.spring.security.authority.NamedGrantedAuthority;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxyImpl;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.common.test.MockedLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestCurrentSecurityPrincipalProxy.Config.class, loader = MockedLoader.class)
public class TestCurrentSecurityPrincipalProxy
{
	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	private CurrentSecurityPrincipalProxy currentPrincipal;

	@Before
	public void before() {
		reset( securityPrincipalService );
	}

	@After
	public void after() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void principalNotLoadedIfOfTypeSecurityPrincipal() {
		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principal.getPrincipalName() ).thenReturn( "principal" );

		Authentication auth = mock( Authentication.class );
		when( auth.getPrincipal() ).thenReturn( principal );
		when( auth.getName() ).thenReturn( "principal" );
		when( auth.isAuthenticated() ).thenReturn( true );

		SecurityContextHolder.getContext().setAuthentication( auth );

		assertEquals( "principal", currentPrincipal.getPrincipalName() );
		assertSame( principal, currentPrincipal.getPrincipal() );
		assertSame( principal, currentPrincipal.getPrincipal() );

		verify( securityPrincipalService, never() ).getPrincipalByName( anyString() );
	}

	@Test
	public void principalLoadedOnlyOnceIfNecessary() {
		SecurityPrincipal principal = mock( SecurityPrincipal.class );

		Authentication auth = mock( Authentication.class );
		when( auth.getName() ).thenReturn( "principal" );
		when( auth.isAuthenticated() ).thenReturn( true );

		SecurityContextHolder.getContext().setAuthentication( auth );

		when( securityPrincipalService.getPrincipalByName( "principal" ) ).thenReturn( principal );

		assertSame( principal, currentPrincipal.getPrincipal() );
		assertSame( principal, currentPrincipal.getPrincipal() );

		verify( securityPrincipalService, times( 1 ) ).getPrincipalByName( anyString() );
	}

	@Test
	public void principalLoadedOnlyOnceEvenIfNull() {
		Authentication auth = mock( Authentication.class );
		when( auth.getName() ).thenReturn( "principal" );
		when( auth.isAuthenticated() ).thenReturn( true );

		SecurityContextHolder.getContext().setAuthentication( auth );

		assertNull( currentPrincipal.getPrincipal() );
		assertNull( currentPrincipal.getPrincipal() );

		verify( securityPrincipalService, times( 1 ) ).getPrincipalByName( anyString() );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void hasAuthority() {
		assertFalse( currentPrincipal.hasAuthority( "some authority" ) );

		Collection authorities = new HashSet<>();
		authorities.add( new SimpleGrantedAuthority( "some authority" ) );

		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principal.getPrincipalName() ).thenReturn( "principal" );
		when( principal.getAuthorities() ).thenReturn( authorities );

		Authentication auth = mock( Authentication.class );
		when( auth.getPrincipal() ).thenReturn( principal );
		when( auth.getName() ).thenReturn( "principal" );
		when( auth.isAuthenticated() ).thenReturn( true );

		SecurityContextHolder.getContext().setAuthentication( auth );

		assertTrue( currentPrincipal.hasAuthority( "some authority" ) );
		assertTrue( currentPrincipal.hasAuthority( new SimpleGrantedAuthority( "some authority" ) ) );
		assertTrue( currentPrincipal.hasAuthority( new NamedGrantedAuthority( "some authority" ) ) );
	}

	// Has authority

	@Configuration
	protected static class Config
	{
		@Bean
		public CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy() {
			return new CurrentSecurityPrincipalProxyImpl();
		}
	}
}
