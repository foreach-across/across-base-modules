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
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * Utility methods for authentication and authority checking.
 * Mainly for internal use within the Across framework.
 */
public final class AuthenticationUtils
{
	private AuthenticationUtils() {
	}

	/**
	 * Check if a {@link SecurityPrincipal} has as given authority string.
	 * This assumes the call to {@link SecurityPrincipal#getAuthorities()} to be complete: all possible authorities
	 * should be present.
	 *
	 * @param securityPrincipal to check
	 * @param authority         to check for
	 * @return {@code true} if authority was present
	 */
	public static boolean hasAuthority( SecurityPrincipal securityPrincipal, String authority ) {
		return securityPrincipal != null && hasAuthority( securityPrincipal.getAuthorities(), authority );
	}

	/**
	 * Check if an authentication has as given authority string.
	 * This assumes the call to {@link Authentication#getAuthorities()} to be complete: all possible authorities
	 * should be present.
	 *
	 * @param authentication to check
	 * @param authority      to check for
	 * @return {@code true} if authority was present
	 */
	public static boolean hasAuthority( Authentication authentication, String authority ) {
		return authentication != null && hasAuthority( authentication.getAuthorities(), authority );
	}

	/**
	 * Check if a collection of {@link GrantedAuthority} contains a specific authority string.
	 *
	 * @param authorities collection of authorities
	 * @param authority   string to check for
	 * @return {@code true} if authority was present
	 */
	public static boolean hasAuthority( Collection<? extends GrantedAuthority> authorities, String authority ) {
		if ( !CollectionUtils.isEmpty( authorities ) ) {
			for ( GrantedAuthority grantedAuthority : authorities ) {
				if ( grantedAuthority != null && StringUtils.equals( grantedAuthority.getAuthority(),
				                                                     authority ) ) {
					return true;
				}
			}
		}

		return false;
	}
}
