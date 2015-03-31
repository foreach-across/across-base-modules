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
package com.foreach.across.modules.spring.security.actions;

import com.foreach.across.modules.spring.security.AuthorityMatcher;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Implementation of {@link com.foreach.across.modules.spring.security.actions.AllowableActions} that takes a map
 * of {@link com.foreach.across.modules.spring.security.actions.AllowableAction} and
 * {@link com.foreach.across.modules.spring.security.AuthorityMatcher} as input.
 * <p/>
 * Every action requested will be checked against the actual authorities provides.  This class is an abstract
 * providing nested implementations easily constructed through static helper methods.
 *
 * @author Arne Vandamme
 */
public abstract class AuthorityMatchingAllowableActions implements AllowableActions
{
	private final Map<AllowableAction, AuthorityMatcher> actionAuthorityMap;

	public AuthorityMatchingAllowableActions( Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
		this.actionAuthorityMap = actionAuthorityMap;
	}

	@Override
	public boolean contains( AllowableAction action ) {
		AuthorityMatcher matcher = actionAuthorityMap.get( action );
		return matcher != null && matcher.matches( actualAuthorities() );
	}

	@Override
	public Iterator<AllowableAction> iterator() {
		List<AllowableAction> actions = new ArrayList<>();
		Collection<? extends GrantedAuthority> actualAuthorities = actualAuthorities();
		for ( Map.Entry<AllowableAction, AuthorityMatcher> entry : actionAuthorityMap.entrySet() ) {
			if ( entry.getValue().matches( actualAuthorities ) ) {
				actions.add( entry.getKey() );
			}
		}

		return actions.iterator();
	}

	protected abstract Collection<? extends GrantedAuthority> actualAuthorities();

	public static AuthorityMatchingAllowableActions forSecurityPrincipal(
			SecurityPrincipal securityPrincipal,
			Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
		return new SecurityPrincipalAuthorityMatchingAllowableActions( securityPrincipal, actionAuthorityMap );
	}

	public static AuthorityMatchingAllowableActions forAuthentication(
			Authentication authentication,
			Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
		return new AuthenticationAuthorityMatchingAllowableActions( authentication, actionAuthorityMap );
	}

	public static AuthorityMatchingAllowableActions forCollection(
			Collection<? extends GrantedAuthority> actualAuthorities,
			Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
		return new CollectionAuthorityMatchingAllowableActions( actualAuthorities, actionAuthorityMap );
	}

	public static class SecurityPrincipalAuthorityMatchingAllowableActions extends AuthorityMatchingAllowableActions
	{
		private final SecurityPrincipal securityPrincipal;

		public SecurityPrincipalAuthorityMatchingAllowableActions( SecurityPrincipal securityPrincipal,
		                                                           Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
			super( actionAuthorityMap );
			Assert.notNull( securityPrincipal );
			this.securityPrincipal = securityPrincipal;
		}

		@Override
		protected Collection<? extends GrantedAuthority> actualAuthorities() {
			return securityPrincipal.getAuthorities();
		}
	}

	public static class AuthenticationAuthorityMatchingAllowableActions extends AuthorityMatchingAllowableActions
	{
		private final Authentication authentication;

		public AuthenticationAuthorityMatchingAllowableActions( Authentication authentication,
		                                                        Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
			super( actionAuthorityMap );
			Assert.notNull( authentication );
			this.authentication = authentication;
		}

		@Override
		protected Collection<? extends GrantedAuthority> actualAuthorities() {
			return authentication.getAuthorities();
		}
	}

	public static class CollectionAuthorityMatchingAllowableActions extends AuthorityMatchingAllowableActions
	{
		private final Collection<? extends GrantedAuthority> grantedAuthorities;

		public CollectionAuthorityMatchingAllowableActions( Collection<? extends GrantedAuthority> grantedAuthorities,
		                                                    Map<AllowableAction, AuthorityMatcher> actionAuthorityMap ) {
			super( actionAuthorityMap );
			Assert.notNull( grantedAuthorities );
			this.grantedAuthorities = grantedAuthorities;
		}

		@Override
		protected Collection<? extends GrantedAuthority> actualAuthorities() {
			return grantedAuthorities;
		}
	}
}
