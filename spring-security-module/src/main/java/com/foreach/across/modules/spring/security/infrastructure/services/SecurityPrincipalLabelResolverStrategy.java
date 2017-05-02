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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Responsible for resolving a unique principal name representing a {@link SecurityPrincipal}, into a more
 * descriptive label or display name.  Dispatches to {@link SecurityPrincipalLabelResolver} implementations.
 * If none is able to return a more descriptive label, the principal name will be returned.
 * <p>Uses the {@link SecurityPrincipalService} for fetching the actual {@link SecurityPrincipal},
 * for performance reasons it will be best if the security principal cache is enabled
 * (see {@link com.foreach.across.modules.spring.security.SpringSecurityModuleCache}).</p>
 * <p>Note: only supports {@link SecurityPrincipal} implementations.</p>
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.hibernate.business.Auditable
 * @since 2.0.0
 */
@Exposed
@Deprecated
public class SecurityPrincipalLabelResolverStrategy
{
	private SecurityPrincipalService securityPrincipalService;
	private Collection<SecurityPrincipalLabelResolver> resolvers = Collections.emptyList();

	public void setSecurityPrincipalService( SecurityPrincipalService securityPrincipalService ) {
		Assert.notNull( securityPrincipalService );
		this.securityPrincipalService = securityPrincipalService;
	}

	public void setResolvers( Collection<SecurityPrincipalLabelResolver> resolvers ) {
		Assert.notNull( resolvers );
		this.resolvers = resolvers;
	}

	/**
	 * Resolve the principal (usually unique principal name) into a more descriptive label.
	 *
	 * @param principal to resolvePrincipalLabel
	 * @return label or {code toString()} of principal if could not be resolved
	 */
	public String resolvePrincipalLabel( Object principal ) {
		String label = null;

		if ( principal == null ) {
			return "";
		}

		SecurityPrincipal securityPrincipal = retrieveSecurityPrincipal( principal );

		if ( securityPrincipal != null ) {
			label = retrieveLabel( securityPrincipal );
		}

		return label != null ? label : Objects.toString( principal );
	}

	private SecurityPrincipal retrieveSecurityPrincipal( Object principal ) {
		if ( principal instanceof String ) {
			return securityPrincipalService.getPrincipalByName( (String) principal );
		}
		else if ( principal instanceof SecurityPrincipal ) {
			return (SecurityPrincipal) principal;
		}

		return null;
	}

	private String retrieveLabel( SecurityPrincipal securityPrincipal ) {
		for ( SecurityPrincipalLabelResolver resolver : resolvers ) {
			Optional<String> candidate = resolver.resolvePrincipalLabel( securityPrincipal );

			if ( candidate.isPresent() ) {
				return candidate.get();
			}
		}

		return null;
	}
}
