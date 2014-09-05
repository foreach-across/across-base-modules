package com.foreach.across.modules.spring.security.infrastructure.services;

import com.foreach.across.core.annotations.Refreshable;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Provides a proxy for the Authentication, where a request is only assumed to be authenticated
 * if the the principal associated with it is in fact a valid
 * {@link com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal}.
 * <p/>
 * The proxy will attempt to load the SecurityPrincipal if it is not yet available (for example
 * only the principal name is loaded on the Authentication object).
 *
 * @author Arne Vandamme
 */
@Service
@Refreshable
public class CurrentSecurityPrincipalProxyImpl implements CurrentSecurityPrincipalProxy
{
	private static final ThreadLocal<SecurityPrincipal> principal = new ThreadLocal<>();
	private static final ThreadLocal<String> principalName = new ThreadLocal<>();

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired(required = false)
	private AclSecurityService aclSecurityService;

	@Override
	public boolean isAuthenticated() {
		return getPrincipal() != null;
	}

	@Override
	public boolean hasAuthority( String authority ) {
		return isAuthenticated() && getPrincipal().getAuthorities().contains( new SimpleGrantedAuthority( authority ) );
	}

	@Override
	public boolean hasAclPermission( IdBasedEntity entity, AclPermission permission ) {
		return aclSecurityService != null
				&& isAuthenticated()
				&& aclSecurityService.hasPermission( getPrincipal(), entity, permission );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> T getPrincipal() {
		return (T) loadPrincipal();
	}

	@Override
	public String getPrincipalName() {
		return isAuthenticated() ? getPrincipal().getPrincipalName() : null;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return isAuthenticated() ? Collections.<GrantedAuthority>emptyList() : getPrincipal().getAuthorities();
	}

	private SecurityPrincipal loadPrincipal() {
		String loadedName = principalName.get();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if ( authentication.isAuthenticated() ) {
			if ( loadedName == null || !StringUtils.equals( loadedName, authentication.getName() ) ) {
				principalName.set( authentication.getName() );

				if ( authentication.getPrincipal() instanceof SecurityPrincipal ) {
					principal.set( (SecurityPrincipal) authentication.getPrincipal() );
				}
				else if ( authentication.getPrincipal() instanceof String ) {
					principal.set( securityPrincipalService.getPrincipalByName(
							(String) authentication.getPrincipal() ) );
				}
				else {
					principal.set( securityPrincipalService.getPrincipalByName( authentication.getName() ) );
				}
			}

			return principal.get();
		}

		if ( loadedName != null ) {
			principalName.remove();
			principal.remove();
		}

		return null;
	}

	@Override
	public String toString() {
		return isAuthenticated() ? "not-authenticated" : getPrincipal().toString();
	}
}
