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
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Resolver that attempts to load the {@link SecurityPrincipal} represented by a principal object
 * attached to an {@link Authentication}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.spring.security.annotations.CurrentSecurityPrincipal
 * @since 4.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationSecurityPrincipalResolver
{
	private final SecurityPrincipalService securityPrincipalService;

	/**
	 * Resolve the principal associated with an {@link Authentication}.
	 *
	 * @param authentication that holds the principal
	 * @return security principal
	 */
	public Optional<SecurityPrincipal> resolveSecurityPrincipal( @NonNull Authentication authentication ) {
		return resolveSecurityPrincipal( authentication.getPrincipal() );
	}

	/**
	 * Resolve the actual {@link SecurityPrincipal} attached that an {@code AuthenticationPrincipal} represents.
	 *
	 * @param authenticationPrincipal principal embedded in the authentication
	 * @return security principal
	 */
	public Optional<SecurityPrincipal> resolveSecurityPrincipal( Object authenticationPrincipal ) {
		if ( authenticationPrincipal instanceof SecurityPrincipalReference ) {
			return securityPrincipalService.getPrincipalById( ( (SecurityPrincipalReference) authenticationPrincipal ).getSecurityPrincipalId() );
		}
		if ( authenticationPrincipal instanceof SecurityPrincipalId ) {
			return securityPrincipalService.getPrincipalById( (SecurityPrincipalId) authenticationPrincipal );
		}
		if ( authenticationPrincipal instanceof SecurityPrincipal ) {
			return Optional.of( (SecurityPrincipal) authenticationPrincipal );
		}
		if ( authenticationPrincipal instanceof String ) {
			return securityPrincipalService.getPrincipalByName( (String) authenticationPrincipal );
		}
		return Optional.empty();
	}
}
