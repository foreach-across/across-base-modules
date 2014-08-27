package com.foreach.across.modules.spring.security.infrastructure.business;

import java.util.Collection;

/**
 * Represents an entity that has one or more {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}
 * instances as parent.  Example would be user that belongs to one or more groups.  The entity does not have to be a
 * security principal in itself, but in permission checking situations, it will be considered to have all the
 * authorities of the parents it belongs to.
 *
 * @author Arne Vandamme
 */
public interface SecurityPrincipalHierarchy
{
	Collection<SecurityPrincipal> getParentPrincipals();
}
