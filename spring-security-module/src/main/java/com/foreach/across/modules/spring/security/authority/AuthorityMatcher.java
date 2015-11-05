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
	 * Creates a matcher for authorities represented by the simple string.  Note that the equals() implementation
	 * of the specific {@link org.springframework.security.core.GrantedAuthority} instances will determine if
	 * these will match or not.
	 * <p><strong>
	 * If possible, use {@link #anyOf(org.springframework.security.core.GrantedAuthority...)} instead.
	 * </strong></p>
	 *
	 * @param authorities array of authorities represented by their id
	 * @return matcher for the colleciton passed
	 */
	public static AuthorityMatcher anyOf( final String... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( String authority : authorities ) {
					if ( actual.contains( new NamedGrantedAuthority( authority ) ) ) {
						return true;
					}
				}
				return false;
			}
		};
	}

	public static AuthorityMatcher anyOf( final GrantedAuthority... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( GrantedAuthority authority : authorities ) {
					if ( actual.contains( authority ) ) {
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
	 * Creates a matcher for authorities represented by the simple string.  Note that the equals() implementation
	 * of the specific {@link org.springframework.security.core.GrantedAuthority} instances will determine if
	 * these will match or not.
	 * <p><strong>
	 * If possible, use {@link #allOf(org.springframework.security.core.GrantedAuthority...)} instead.
	 * </strong></p>
	 *
	 * @param authorities array of authorities represented by their id
	 * @return matcher for the colleciton passed
	 */
	public static AuthorityMatcher allOf( final String... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( String authority : authorities ) {
					if ( !actual.contains( new NamedGrantedAuthority( authority ) ) ) {
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
					if ( !actual.contains( authority ) ) {
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
	 * Creates a matcher for authorities represented by the simple string.  Note that the equals() implementation
	 * of the specific {@link org.springframework.security.core.GrantedAuthority} instances will determine if
	 * these will match or not.
	 * <p><strong>
	 * If possible, use {@link #noneOf(org.springframework.security.core.GrantedAuthority...)} instead.
	 * </strong></p>
	 *
	 * @param authorities array of authorities represented by their id
	 * @return matcher for the colleciton passed
	 */
	public static AuthorityMatcher noneOf( final String... authorities ) {
		return new AuthorityMatcher()
		{
			@Override
			public boolean matches( Collection<? extends GrantedAuthority> actual ) {
				for ( String authority : authorities ) {
					if ( actual.contains( new NamedGrantedAuthority( authority ) ) ) {
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
					if ( actual.contains( authority ) ) {
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
