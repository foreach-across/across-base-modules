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

package com.foreach.across.modules.spring.security.infrastructure.business;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipal
{
	/**
	 * @return A unique identifier for this principal.
	 */
	String getPrincipalName();

	/**
	 * @return a principal of type {@link SecurityPrincipalId} which can be used to be stored in the {@link org.springframework.security.core.Authentication}
	 */
	default SecurityPrincipalId getSecurityPrincipalId() {
		if ( StringUtils.isEmpty( getPrincipalName() ) ) {
			return null;
		}
		return SecurityPrincipalId.of( getPrincipalName() );
	}

	/**
	 * @return The collection of authorities that have been granted to this principal.
	 */
	Collection<? extends GrantedAuthority> getAuthorities();

	/**
	 * Any SecurityPrincipal should return the principal name as
	 * toString() implementation to ensure maximum compatibility with
	 * SpringSecurity. Usually this is the same to calling {@code toString()}
	 * of the {@link #getSecurityPrincipalId()}.
	 */
	@Override
	String toString();
}
