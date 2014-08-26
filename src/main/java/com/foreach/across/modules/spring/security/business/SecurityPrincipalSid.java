package com.foreach.across.modules.spring.security.business;

import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * @author Arne Vandamme
 */
public class SecurityPrincipalSid extends PrincipalSid
{
	public SecurityPrincipalSid( SecurityPrincipal principal ) {
		super( principal.getPrincipalName() );
	}

	public static PrincipalSid forAuthentication( Authentication authentication ) {
		Assert.notNull( authentication, "Authentication required" );

		Object principal = authentication.getPrincipal();

		Assert.notNull( principal, "Principal required" );

		if ( principal instanceof SecurityPrincipal ) {
			return new SecurityPrincipalSid( (SecurityPrincipal) principal );
		}
		else if ( principal instanceof UserDetails ) {
			return new PrincipalSid( ( (UserDetails) authentication.getPrincipal() ).getUsername() );
		}
		else {
			return new PrincipalSid( authentication.getPrincipal().toString() );
		}
	}
}
