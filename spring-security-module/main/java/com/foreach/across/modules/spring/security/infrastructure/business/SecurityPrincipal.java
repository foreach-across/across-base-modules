package com.foreach.across.modules.spring.security.infrastructure.business;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipal
{
	/**
	 * @return A unique identifier for this principal.
	 */
	String getPrincipalName();

	/**
	 * @return The collection of authorities that have been granted to this principal.
	 */
	Collection<GrantedAuthority> getAuthorities();

	/**
	 * Any SecurityPrincipal should return the principal name as
	 * toString() implementation to ensure maximum compatibility with
	 * SpringSecurity.
	 */
	String toString();
}
