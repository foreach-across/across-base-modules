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
package com.foreach.across.modules.spring.security.infrastructure.services;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalHierarchy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Provides a proxy for the Authentication, where a request is only assumed to be authenticated
 * if the the principal associated with it is in fact a valid
 * {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}.
 * <p/>
 * The proxy will attempt to load the SecurityPrincipal if it is not yet available (for example
 * only the principal name is loaded on the Authentication object).
 *
 * @author Arne Vandamme
 */
@Service
public class CurrentSecurityPrincipalProxyImpl implements CurrentSecurityPrincipalProxy, SecurityPrincipalHierarchy
{
	private static final ThreadLocal<SecurityPrincipal> principal = new ThreadLocal<>();
	private static final ThreadLocal<String> principalName = new ThreadLocal<>();

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Override
	public boolean isAuthenticated() {
		return getPrincipal() != null;
	}

	@Override
	public boolean hasAuthority( String authority ) {
		return isAuthenticated() && getPrincipal().getAuthorities().contains( new SimpleGrantedAuthority( authority ) );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> T getPrincipal() {
		return (T) loadPrincipal();
	}

	@Override
	public String getPrincipalName() {
		return isAuthenticated() ? getPrincipal().getPrincipalName() : null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return isAuthenticated() ? getPrincipal().getAuthorities() : Collections.<GrantedAuthority>emptyList();
	}

	@Override
	public Collection<SecurityPrincipal> getParentPrincipals() {
		SecurityPrincipal principal = getPrincipal();

		return principal instanceof SecurityPrincipalHierarchy
				? ( (SecurityPrincipalHierarchy) principal ).getParentPrincipals()
				: Collections.<SecurityPrincipal>emptyList();
	}

	private SecurityPrincipal loadPrincipal() {
		String loadedName = principalName.get();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if ( authentication != null && authentication.isAuthenticated() ) {
			if ( loadedName == null || !StringUtils.equals( loadedName, authentication.getName() ) ) {
				principalName.set( authentication.getName() );

				if ( authentication.getPrincipal() instanceof SecurityPrincipal ) {
					principal.set( (SecurityPrincipal) authentication.getPrincipal() );
				}
				else if ( authentication.getPrincipal() instanceof String ) {
					principal.set( securityPrincipalService.getPrincipalByName(
							(String) authentication.getPrincipal() ) );
				}
				else {
					principal.set( securityPrincipalService.getPrincipalByName( authentication.getName() ) );
				}
			}

			return principal.get();
		}

		if ( loadedName != null ) {
			principalName.remove();
			principal.remove();
		}

		return null;
	}

	@Override
	public String toString() {
		return isAuthenticated() ? getPrincipal().toString() : "not-authenticated";
	}
}
