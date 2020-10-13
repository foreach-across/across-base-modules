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

package com.foreach.across.modules.spring.security.authority;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
public class TestAuthorityMatcher
{
	@Test
	public void anyOf() {
		AuthorityMatcher matcher = AuthorityMatcher.anyOf( "manage users", "view groups" );

		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "view groups" ) ) ) );
		assertTrue( matcher.matches(
				asList(
						new SimpleGrantedAuthority( "manage users" ),
						new SimpleGrantedAuthority( "view groups" )
				)
		            )
		);
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void anyOfWithAuthorities() {
		AuthorityMatcher matcher = AuthorityMatcher.anyOf( new SimpleGrantedAuthority( "manage users" ),
		                                                   new SimpleGrantedAuthority( "view groups" ) );

		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertTrue( matcher.matches( singletonList( new SwitchUserGrantedAuthority( "view groups", mock( Authentication.class ) ) ) ) );
		assertTrue( matcher.matches(
				asList(
						new SwitchUserGrantedAuthority( "manage users", mock( Authentication.class ) ),
						new SimpleGrantedAuthority( "view groups" )
				)
		            )
		);
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void anyOfWithMatchers() {
		AuthorityMatcher matcher = AuthorityMatcher.anyOf(
				AuthorityMatcher.allOf( "manage users" ),
				AuthorityMatcher.allOf( "view groups" )
		);

		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "view groups" ) ) ) );
		assertTrue( matcher.matches(
				asList(
						new SimpleGrantedAuthority( "manage users" ),
						new SimpleGrantedAuthority( "view groups" )
				)
		            )
		);
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void allOf() {
		AuthorityMatcher matcher = AuthorityMatcher.allOf( "manage users", "view groups" );

		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "view groups" ) ) ) );
		assertTrue( matcher.matches( asList(
				new SimpleGrantedAuthority( "manage users" ), new SimpleGrantedAuthority( "view groups" )
		            ) )
		);
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void allOfWithAuthorities() {
		AuthorityMatcher matcher = AuthorityMatcher.allOf( new SimpleGrantedAuthority( "manage users" ),
		                                                   new SwitchUserGrantedAuthority( "view groups", mock( Authentication.class ) ) );

		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertFalse( matcher.matches( singletonList( new SwitchUserGrantedAuthority( "view groups", mock( Authentication.class ) ) ) ) );
		assertTrue( matcher.matches( asList(
				new SwitchUserGrantedAuthority( "manage users", mock( Authentication.class ) ), new SimpleGrantedAuthority( "view groups" )
		            ) )
		);
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void allOfWithMatchers() {
		AuthorityMatcher matcher = AuthorityMatcher.allOf(
				AuthorityMatcher.anyOf( "manage users", "manage people" ),
				AuthorityMatcher.allOf( "view groups" )
		);

		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "view groups" ) ) ) );
		assertTrue( matcher.matches( asList(
				new SimpleGrantedAuthority( "manage users" ), new SimpleGrantedAuthority( "view groups" )
		            ) )
		);
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void noneOf() {
		AuthorityMatcher matcher = AuthorityMatcher.noneOf( "manage users", "view groups" );

		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "view groups" ) ) ) );
		assertFalse( matcher.matches( asList(
				new SimpleGrantedAuthority( "manage users" ), new SimpleGrantedAuthority( "view groups" )
		             ) )
		);
		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void noneOfWithAuthorities() {
		AuthorityMatcher matcher = AuthorityMatcher.noneOf(
				new SimpleGrantedAuthority( "manage users" ),
				new SwitchUserGrantedAuthority( "view groups", mock( Authentication.class ) )
		);

		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertFalse( matcher.matches( singletonList( new SwitchUserGrantedAuthority( "view groups", mock( Authentication.class ) ) ) ) );
		assertFalse( matcher.matches( asList(
				new SwitchUserGrantedAuthority( "manage users", mock( Authentication.class ) ), new SimpleGrantedAuthority( "view groups" )
		             ) )
		);
		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}

	@Test
	public void noneOfWithMatchers() {
		AuthorityMatcher matcher = AuthorityMatcher.noneOf(
				AuthorityMatcher.allOf( "manage users" ),
				AuthorityMatcher.anyOf( "view groups" )
		);

		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "manage users" ) ) ) );
		assertFalse( matcher.matches( singletonList( new SimpleGrantedAuthority( "view groups" ) ) ) );
		assertFalse( matcher.matches( asList(
				new SimpleGrantedAuthority( "manage users" ), new SimpleGrantedAuthority( "view groups" )
		             ) )
		);
		assertTrue( matcher.matches( singletonList( new SimpleGrantedAuthority( "other perm" ) ) ) );
	}
}
