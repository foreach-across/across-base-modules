package com.foreach.across.modules.spring.security.acl.business;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.security.acls.domain.PrincipalSid;

/**
 * @author Arne Vandamme
 */
public class SecurityPrincipalSid extends PrincipalSid
{
	public SecurityPrincipalSid( SecurityPrincipal principal ) {
		super( principal.getPrincipalName() );
	}
}
