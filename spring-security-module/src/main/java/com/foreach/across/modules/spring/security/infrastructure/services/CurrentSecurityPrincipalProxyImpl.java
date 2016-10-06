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

import com.foreach.across.modules.spring.security.AuthenticationUtils;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalAuthenticationToken;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Provides a proxy for the Authentication, where a request is only assumed to be authenticated
 * if the the principal associated with it is in fact a valid
 * {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}.
 * <p>
 * The proxy will attempt to load the SecurityPrincipal if it is not yet available (for example
 * only the principal name is loaded on the Authentication object).  Be aware that this will done
 * every time if the implementing {@link org.springframework.security.core.Authentication} does not
 * hold the {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal} instance.
 * </p>
 *
 * @author Arne Vandamme
 */
@Service
public class CurrentSecurityPrincipalProxyImpl implements CurrentSecurityPrincipalProxy, SecurityPrincipalHierarchy
{
	private static final Logger LOG = LoggerFactory.getLogger( CurrentSecurityPrincipalProxyImpl.class );

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Override
	public boolean isAuthenticated() {
		Authentication currentAuthentication = currentAuthentication();
		return currentAuthentication != null && currentAuthentication.isAuthenticated();
	}

	@Override
	public boolean hasAuthority( String authority ) {
		return isAuthenticated() && AuthenticationUtils.hasAuthority( currentAuthentication(), authority );
	}

	@Override
	public boolean hasAuthority( GrantedAuthority authority ) {
		return isAuthenticated() && currentAuthentication().getAuthorities().contains( authority );
	}

	@Override
	public SecurityPrincipal getPrincipal() {
		return loadPrincipal();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V extends SecurityPrincipal> V getPrincipal( Class<V> principalType ) {
		SecurityPrincipal securityPrincipal = loadPrincipal();
		return principalType.isInstance( securityPrincipal ) ? (V) securityPrincipal : null;
	}

	@Override
	public String getPrincipalName() {
		return isAuthenticated() ? currentAuthentication().getName() : null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return isAuthenticated() ? currentAuthentication().getAuthorities() : Collections.<GrantedAuthority>emptyList();
	}

	private Authentication currentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public Collection<SecurityPrincipal> getParentPrincipals() {
		SecurityPrincipal principal = getPrincipal();

		return principal instanceof SecurityPrincipalHierarchy
				? ( (SecurityPrincipalHierarchy) principal ).getParentPrincipals()
				: Collections.<SecurityPrincipal>emptyList();
	}

	private SecurityPrincipal loadPrincipal() {
		Authentication authentication = currentAuthentication();

		if ( authentication != null && authentication.isAuthenticated() ) {
			if ( authentication instanceof SecurityPrincipalAuthenticationToken ) {
				return ( (SecurityPrincipalAuthenticationToken) authentication ).getPrincipal();
			}

			Object authenticationPrincipal = authentication.getPrincipal();

			if ( authenticationPrincipal instanceof SecurityPrincipal ) {
				return (SecurityPrincipal) authenticationPrincipal;
			}

			if ( authenticationPrincipal instanceof String ) {
				LOG.debug( "Loading SecurityPrincipal with name {}", authenticationPrincipal );
				return securityPrincipalService.getPrincipalByName( (String) authenticationPrincipal );

			}

			LOG.debug( "Loading SecurityPrincipal with name {}", authentication.getName() );
			return securityPrincipalService.getPrincipalByName( authentication.getName() );
		}

		return null;
	}

	@Override
	public String toString() {
		return isAuthenticated() ? getPrincipal().toString() : "not-authenticated";
	}
}
