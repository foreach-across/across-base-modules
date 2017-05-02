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
import org.springframework.security.core.GrantedAuthority;

/**
 * @author Arne Vandamme
 */
public interface CurrentSecurityPrincipalProxy extends SecurityPrincipal
{
	/**
	 * @return {@code true} if the there is an authenticated principal attached to the current thread
	 */
	boolean isAuthenticated();

	/**
	 * Checks if the authenticated principal has the matching authority.  This does a {@link String}
	 * based comparison on {@link GrantedAuthority#getAuthority()}, whereas {@link #hasAuthority(GrantedAuthority)}
	 * will perform an equality check on the actual {@link GrantedAuthority} instance.
	 * <p/>
	 * String based authority checks are usually safest to use.
	 *
	 * @param authority string the authentication should have
	 * @return true if authority string was present
	 */
	boolean hasAuthority( String authority );

	/**
	 * Checks if the authenticated principal has the matching exact authority.  This does an equality check on
	 * the {@link GrantedAuthority} instance, meaning the type will be taken into account.  In most cases
	 * using authority strings is enough, see {@link #hasAuthority(String)}.
	 *
	 * @param authority instance the authentication should have
	 * @return true if authority instance was present
	 */
	boolean hasAuthority( GrantedAuthority authority );

	/**
	 * @return the backing SecurityPrincipal instance
	 */
	SecurityPrincipal getPrincipal();

	/**
	 * Return the {@link SecurityPrincipal} instance that is being proxied if and only if it is of the required type.
	 * In case there is a principal that does not match the type, {@code null} will be returned.
	 *
	 * @param principalType expected type of the principal
	 * @param <V>           type of the principal
	 * @return instance of available and of the required type, null otherwise
	 */
	<V extends SecurityPrincipal> V getPrincipal( Class<V> principalType );
}
