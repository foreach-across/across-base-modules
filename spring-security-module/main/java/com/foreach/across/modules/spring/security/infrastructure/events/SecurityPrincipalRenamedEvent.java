package com.foreach.across.modules.spring.security.infrastructure.events;

import com.foreach.across.core.events.AcrossEvent;

/**
 * Event fired through the {@link com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService}
 * to notify listeners that a {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal} has
 * been renamed.  When the unique principal name is modified, security settings often need updating.
 *
 * @author Arne Vandamme
 */
public class SecurityPrincipalRenamedEvent implements AcrossEvent
{
	private final String oldName, newName;

	public SecurityPrincipalRenamedEvent( String oldName, String newName ) {
		this.oldName = oldName;
		this.newName = newName;
	}

	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
	}
}
