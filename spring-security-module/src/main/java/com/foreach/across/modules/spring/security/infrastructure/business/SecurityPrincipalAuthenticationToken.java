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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;

/**
 * Represents an {@link org.springframework.security.core.Authentication} instance for an authenticated
 * {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal
 */
public class SecurityPrincipalAuthenticationToken extends PreAuthenticatedAuthenticationToken
{
	public SecurityPrincipalAuthenticationToken( SecurityPrincipal securityPrincipal ) {
		this( securityPrincipal != null ? securityPrincipal.getSecurityPrincipalId() : null,
		      securityPrincipal != null ? securityPrincipal.getAuthorities() : null );
	}

	public SecurityPrincipalAuthenticationToken( SecurityPrincipalId securityPrincipalId, Collection<? extends GrantedAuthority> authorities ) {
		super( securityPrincipalId, null, authorities );
	}

	@Override
	public SecurityPrincipalId getPrincipal() {
		return (SecurityPrincipalId) super.getPrincipal();
	}
}
