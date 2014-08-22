package com.foreach.across.modules.spring.security.business;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipal
{
	/**
	 * @return A unique identifier for this principal.
	 */
	String getPrincipalId();
}
