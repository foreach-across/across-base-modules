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

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalService
{
	/**
	 * Creates an {@link org.springframework.security.core.Authentication} for the
	 * {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal} and sets it
	 * as the security context for the current thread.
	 *
	 * @param principal Principal that should authenticate.
	 */
	void authenticate( SecurityPrincipal principal );

	/**
	 * Clears the authentication of the current thread.
	 */
	void clearAuthentication();

	<T extends SecurityPrincipal> T getPrincipalByName( String principalName );

	void publishRenameEvent( String oldPrincipalName, String newPrincipalName );
}
