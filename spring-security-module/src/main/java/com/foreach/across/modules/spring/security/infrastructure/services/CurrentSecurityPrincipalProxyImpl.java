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
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalHierarchy;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
 * @see AuthenticationSecurityPrincipalResolver
 */
@Service
@RequiredArgsConstructor
public class CurrentSecurityPrincipalProxyImpl implements CurrentSecurityPrincipalProxy, SecurityPrincipalHierarchy
{
	private SecurityInfrastructure securityInfrastructure;
	private AuthenticationSecurityPrincipalResolver authenticationSecurityPrincipalResolver;

	@Autowired
	protected void setSecurityInfrastructure( @NonNull SecurityInfrastructure securityInfrastructure ) {
		this.securityInfrastructure = securityInfrastructure;
	}

	@Autowired
	protected void setAuthenticationSecurityPrincipalResolver( @NonNull AuthenticationSecurityPrincipalResolver authenticationSecurityPrincipalResolver ) {
		this.authenticationSecurityPrincipalResolver = authenticationSecurityPrincipalResolver;
	}

	@Override
	public boolean isAuthenticated() {
		Authentication currentAuthentication = currentAuthentication();
		return hasValidAuthentication() && !securityInfrastructure.authenticationTrustResolver().isAnonymous( currentAuthentication );
	}

	@Override
	public boolean hasAuthority( String authority ) {
		return hasValidAuthentication() && AuthenticationUtils.hasAuthority( currentAuthentication(), authority );
	}

	@Override
	public boolean hasAuthority( GrantedAuthority authority ) {
		return hasValidAuthentication() && AuthenticationUtils.hasAuthority( currentAuthentication(), authority.getAuthority() );
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
		if ( hasValidAuthentication() ) {
			Authentication authentication = currentAuthentication();

			Object principal = authentication.getPrincipal();

			if ( principal instanceof SecurityPrincipalId ) {
				return principal.toString();
			}

			if ( securityInfrastructure.authenticationTrustResolver().isAnonymous( authentication ) ) {
				return authentication.getName();
			}

			// fallback, attempting to load an actual SecurityPrincipal
			SecurityPrincipal securityPrincipal = getPrincipal();

			if ( securityPrincipal != null ) {
				return securityPrincipal.getPrincipalName();
			}

			return authentication.getName();
		}

		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return hasValidAuthentication() ? currentAuthentication().getAuthorities() : Collections.<GrantedAuthority>emptyList();
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
			return authenticationSecurityPrincipalResolver.resolveSecurityPrincipal( authentication ).orElse( null );
		}

		return null;
	}

	@Override
	public String toString() {
		return hasValidAuthentication() ? getPrincipalName() : "not-authenticated";
	}

	private boolean hasValidAuthentication() {
		Authentication currentAuthentication = currentAuthentication();
		return currentAuthentication != null && currentAuthentication.isAuthenticated();
	}
}
