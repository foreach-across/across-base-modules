package com.foreach.across.modules.spring.security.infrastructure.services;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;

/**
 * Interface to implement the backend for fetching security principals
 * for a {@link SecurityPrincipalService}.
 *
 * @author Arne Vandamme
 */
public interface SecurityPrincipalRetrievalStrategy
{
	SecurityPrincipal getPrincipalByName( String principalName );
}
