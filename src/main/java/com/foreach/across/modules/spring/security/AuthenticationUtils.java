package com.foreach.across.modules.spring.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public final class AuthenticationUtils
{
	private AuthenticationUtils() {
	}

	public static boolean hasAuthority( Authentication authentication, String authority ) {
		if ( authentication != null ) {
			Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
			if ( !CollectionUtils.isEmpty( grantedAuthorities ) ) {
				for ( GrantedAuthority grantedAuthority : grantedAuthorities ) {
					if ( grantedAuthority != null && StringUtils.equals( grantedAuthority.getAuthority(),
					                                                     authority ) ) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
