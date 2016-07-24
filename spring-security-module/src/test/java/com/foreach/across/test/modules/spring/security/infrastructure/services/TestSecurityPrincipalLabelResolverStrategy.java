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

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolver;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolverStrategy;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestSecurityPrincipalLabelResolverStrategy
{
	private SecurityPrincipalLabelResolverStrategy labelResolverStrategy;

	@Mock
	private SecurityPrincipalLabelResolver labelResolverOne, labelResolverTwo;

	@Mock
	private SecurityPrincipalService securityPrincipalService;

	@Before
	public void before() {
		MockitoAnnotations.initMocks( this );

		labelResolverStrategy = new SecurityPrincipalLabelResolverStrategy();
		labelResolverStrategy.setResolvers( Arrays.asList( labelResolverOne, labelResolverTwo ) );
		labelResolverStrategy.setSecurityPrincipalService( securityPrincipalService );

		when( labelResolverOne.resolvePrincipalLabel( any( SecurityPrincipal.class ) ) ).thenReturn( Optional.empty() );
		when( labelResolverTwo.resolvePrincipalLabel( any( SecurityPrincipal.class ) ) ).thenReturn( Optional.empty() );
	}

	@Test
	public void principalToStringReturnedIfNoPrincipalFound() {
		assertEquals( "123", labelResolverStrategy.resolvePrincipalLabel( 123 ) );

		verify( labelResolverOne, never() ).resolvePrincipalLabel( any( SecurityPrincipal.class ) );
		verify( securityPrincipalService, never() ).getPrincipalByName( anyString() );
	}

	@Test
	public void principalIsPassedIfAlreadySecurityPrincipal() {
		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( labelResolverOne.resolvePrincipalLabel( principal ) ).thenReturn( Optional.of( "my principal" ) );

		assertEquals( "my principal", labelResolverStrategy.resolvePrincipalLabel( principal ) );

		verify( securityPrincipalService, never() ).getPrincipalByName( anyString() );
	}

	@Test
	public void securityPrincipalIsFetchedIfString() {
		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( labelResolverTwo.resolvePrincipalLabel( principal ) ).thenReturn( Optional.of( "my principal" ) );
		when( securityPrincipalService.getPrincipalByName( "principal name" ) ).thenReturn( principal );

		assertEquals( "my principal", labelResolverStrategy.resolvePrincipalLabel( "principal name" ) );
	}

	@Test
	public void allResolversAreTriedIfNonReturns() {
		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principal.toString() ).thenReturn( "my principal" );
		assertEquals( "my principal", labelResolverStrategy.resolvePrincipalLabel( principal ) );

		verify( labelResolverOne ).resolvePrincipalLabel( principal );
		verify( labelResolverTwo ).resolvePrincipalLabel( principal );
	}

	@Test
	public void nullPrincipalReturnsEmptyLabel() {
		assertEquals( "", labelResolverStrategy.resolvePrincipalLabel( null ) );
	}
}

