package com.foreach.across.modules.spring.security.business;

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
	 * Any SecurityPrincipal should return the principal name as
	 * toString() implementation to ensure maximum compatibility with
	 * SpringSecurity.
	 */
	String toString();
}
