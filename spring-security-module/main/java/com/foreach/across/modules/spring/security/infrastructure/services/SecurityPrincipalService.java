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
