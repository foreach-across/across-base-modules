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

package com.foreach.across.modules.spring.security;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthenticationUtils
{
	@Test
	public void invalidAuthenticationReturnsFalse() throws Exception {
		assertFalse( AuthenticationUtils.hasAuthority( (Authentication) null, "foezoj" ) );
	}

	@Test
	public void invalidPrincipalReturnsFalse() {
		assertFalse( AuthenticationUtils.hasAuthority( (SecurityPrincipal) null, "foezoj" ) );
	}

	@Test
	public void authenticationWithNullAuthoritiesReturnsFalse() throws Exception {
		assertFalse( AuthenticationUtils.hasAuthority( mock( Authentication.class ), "foezoj" ) );
	}

	@Test
	public void principalWithNullAuthoritiesReturnsFalse() throws Exception {
		assertFalse( AuthenticationUtils.hasAuthority( mock( SecurityPrincipal.class ), "foezoj" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void authenticationWithAuthoritiesAndNullAuthorityReturnsFalse() throws Exception {
		Authentication authentication = mock( Authentication.class );
		Collection grantedAuthorities = Sets.newSet( null, new SimpleGrantedAuthority( "bla" ) );
		when( authentication.getAuthorities() ).thenReturn( grantedAuthorities );
		assertFalse( AuthenticationUtils.hasAuthority( authentication, null ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void securityPrincipalWithAuthoritiesAndNullAuthorityReturnsFalse() throws Exception {
		SecurityPrincipal securityPrincipal = mock( SecurityPrincipal.class );
		Collection grantedAuthorities = Sets.newSet( null, new SimpleGrantedAuthority( "bla" ) );
		when( securityPrincipal.getAuthorities() ).thenReturn( grantedAuthorities );
		assertFalse( AuthenticationUtils.hasAuthority( securityPrincipal, null ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void authenticationWithAuthoritiesAndNullAuthorityReturnsTrue() throws Exception {
		Authentication authentication = mock( Authentication.class );
		Collection grantedAuthorities = Sets.newSet( null, new SimpleGrantedAuthority( "bla" ) );
		when( authentication.getAuthorities() ).thenReturn( grantedAuthorities );
		assertTrue( AuthenticationUtils.hasAuthority( authentication, "bla" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void securityPrinicpalWithAuthoritiesAndNullAuthorityReturnsTrue() throws Exception {
		SecurityPrincipal securityPrincipal = mock( SecurityPrincipal.class );
		Collection grantedAuthorities = Sets.newSet( null, new SimpleGrantedAuthority( "bla" ) );
		when( securityPrincipal.getAuthorities() ).thenReturn( grantedAuthorities );
		assertTrue( AuthenticationUtils.hasAuthority( securityPrincipal, "bla" ) );
	}
}
