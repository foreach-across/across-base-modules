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

import com.foreach.across.modules.spring.security.AuthenticationUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Helper to create conditions that a set of GrantedAuthority instances should match.
 *
 * @author Arne Vandamme
 */
public abstract class AuthorityMatcher
{
	public abstract boolean matches( Collection<? extends GrantedAuthority> authorities );

	/**
	 * Creates a matcher for authorities represented by the simple string.
	 *
	 * @param authorities array of authority strings
	 * @return matcher for the collection passed
	 */
	public static AuthorityMatcher anyOf( final String... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( String authority : authorities ) {
					if ( AuthenticationUtils.hasAuthority( actual, authority ) ) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * Creates a matcher for authority instances.  Note that only the authority string -
	 * the return value of {@link GrantedAuthority#getAuthority()} will be compared.
	 *
	 * @param authorities array of authority instances
	 * @return matcher for the collection passed
	 */
	public static AuthorityMatcher anyOf( final GrantedAuthority... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( GrantedAuthority authority : authorities ) {
					if ( AuthenticationUtils.hasAuthority( actual, authority.getAuthority() ) ) {
						return true;
					}
				}
				return false;
			}
		};
	}

	public static AuthorityMatcher anyOf( final AuthorityMatcher... matchers ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( AuthorityMatcher matcher : matchers ) {
					if ( matcher.matches( actual ) ) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * Creates a matcher for authorities represented by the simple string.
	 *
	 * @param authorities array of authority strings
	 * @return matcher for the colleciton passed
	 */
	public static AuthorityMatcher allOf( final String... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( String authority : authorities ) {
					if ( !AuthenticationUtils.hasAuthority( actual, authority ) ) {
						return false;
					}
				}
				return true;
			}
		};
	}

	public static AuthorityMatcher allOf( final GrantedAuthority... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( GrantedAuthority authority : authorities ) {
					if ( !AuthenticationUtils.hasAuthority( actual, authority.getAuthority() ) ) {
						return false;
					}
				}
				return true;
			}
		};
	}

	public static AuthorityMatcher allOf( final AuthorityMatcher... matchers ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( AuthorityMatcher matcher : matchers ) {
					if ( !matcher.matches( actual ) ) {
						return false;
					}
				}
				return true;
			}
		};
	}

	/**
	 * Creates a matcher for authorities represented by the simple string.
	 *
	 * @param authorities array of authority strings
	 * @return matcher for the collection passed
	 */
	public static AuthorityMatcher noneOf( final String... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( String authority : authorities ) {
					if ( AuthenticationUtils.hasAuthority( actual, authority ) ) {
						return false;
					}
				}
				return true;
			}
		};
	}

	public static AuthorityMatcher noneOf( final GrantedAuthority... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( GrantedAuthority authority : authorities ) {
					if ( AuthenticationUtils.hasAuthority( actual, authority.getAuthority() ) ) {
						return false;
					}
				}
				return true;
			}
		};
	}

	public static AuthorityMatcher noneOf( final AuthorityMatcher... matchers ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( AuthorityMatcher matcher : matchers ) {
					if ( matcher.matches( actual ) ) {
						return false;
					}
				}
				return true;
			}
		};
	}
}
