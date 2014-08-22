package com.foreach.across.modules.spring.security.business;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalHierarchy
{
	Collection<SecurityPrincipal> getParentPrincipals();
}
