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
package com.foreach.across.test.modules.spring.security.actions;

import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.spring.security.actions.AuthorityMatchingAllowableActions;
import com.foreach.across.modules.spring.security.authority.AuthorityMatcher;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestAuthorityMatchingAllowableActions
{
	private AllowableActions allowableActions;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		SecurityPrincipal principal = mock( SecurityPrincipal.class );

		Collection authorities = Arrays.asList(
				new SimpleGrantedAuthority( "view users" ),
				new SimpleGrantedAuthority( "delete stuff" )
		);

		when( principal.getAuthorities() ).thenReturn( authorities );

		Map<AllowableAction, AuthorityMatcher> matcherMap = new TreeMap<>();
		matcherMap.put( AllowableAction.READ, AuthorityMatcher.allOf( "view users" ) );
		matcherMap.put( AllowableAction.UPDATE, AuthorityMatcher.allOf( "modify users" ) );
		matcherMap.put( AllowableAction.DELETE, AuthorityMatcher.allOf( "view users", "delete stuff" ) );

		allowableActions = AuthorityMatchingAllowableActions.forSecurityPrincipal( principal, matcherMap );
	}

	@Test
	public void singleActions() {
		assertTrue( allowableActions.contains( AllowableAction.READ ) );
		assertFalse( allowableActions.contains( AllowableAction.UPDATE ) );
		assertTrue( allowableActions.contains( AllowableAction.DELETE ) );
		assertFalse( allowableActions.contains( AllowableAction.ADMINISTER ) );
	}

	@Test
	public void iterating() {
		List<AllowableAction> found = new ArrayList<>();

		for ( AllowableAction allowed : allowableActions ) {
			found.add( allowed );
		}

		assertEquals( Arrays.asList( AllowableAction.DELETE, AllowableAction.READ ), found );
	}
}
