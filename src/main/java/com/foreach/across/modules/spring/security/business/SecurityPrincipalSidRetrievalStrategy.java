package com.foreach.across.modules.spring.security.business;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * General implementation of {@link org.springframework.security.acls.model.SidRetrievalStrategy}
 * supporting {@link com.foreach.across.modules.spring.security.business.SecurityPrincipal} and
 * {@link com.foreach.across.modules.spring.security.business.SecurityPrincipalHierarchy} implementations.
 *
 * All parent sids (eg. user groups the user principal belongs to) will be added right after the principal
 * sid but before any authorities.
 *
 * @author Arne Vandamme
 */
public class SecurityPrincipalSidRetrievalStrategy implements SidRetrievalStrategy
{
	private static final Collection<SecurityPrincipal> EMPTY = Collections.emptyList();

	@Override
	public List<Sid> getSids( Authentication authentication ) {
		Object principal = authentication.getPrincipal();

		Collection<SecurityPrincipal> parents = ( principal instanceof SecurityPrincipalHierarchy ) ?
				( (SecurityPrincipalHierarchy) principal ).getParentPrincipals() : EMPTY;

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		List<Sid> sids = new ArrayList<>( authorities.size() + 1 + parents.size() );

		if ( principal instanceof SecurityPrincipal ) {
			sids.add( new PrincipalSid( ( (SecurityPrincipal) principal ).getPrincipalId() ) );
		}
		else {
			sids.add( new PrincipalSid( authentication ) );
		}

		for ( SecurityPrincipal parent : parents ) {
			sids.add( new PrincipalSid( parent.getPrincipalId() ) );
		}

		for ( GrantedAuthority authority : authorities ) {
			sids.add( new GrantedAuthoritySid( authority ) );
		}

		return sids;
	}
}
