package com.foreach.across.modules.spring.security.business;

/**
 * Base implementation of {@link com.foreach.across.modules.spring.security.business.SecurityPrincipal}
 * ensuring the contract where toString() returns the principal name.
 *
 * @author Arne Vandamme
 */
public abstract class AbstractSecurityPrincipal implements SecurityPrincipal
{
	@Override
	public final String toString() {
		return getPrincipalName();
	}
}
