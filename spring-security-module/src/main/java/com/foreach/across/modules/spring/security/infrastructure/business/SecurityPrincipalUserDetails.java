/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.spring.security.infrastructure.business;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of Spring security {@link org.springframework.security.core.userdetails.UserDetails} where
 * the user details represent a unique {@link SecurityPrincipal}. The principal id is a more unique
 *
 * @author Arne Vandamme
 * @see org.springframework.security.core.userdetails.UserDetails
 * @since 4.0.0
 */
@Getter
public class SecurityPrincipalUserDetails implements UserDetails, CredentialsContainer, SecurityPrincipalReference
{
	private static final int serialVersionUID = 400;

	private final SecurityPrincipalId securityPrincipalId;
	private final String username;
	private final Set<GrantedAuthority> authorities;
	private final boolean accountNonExpired;
	private final boolean accountNonLocked;
	private final boolean credentialsNonExpired;
	private final boolean enabled;

	@Setter
	private String password;

	@Builder
	public SecurityPrincipalUserDetails( @NonNull SecurityPrincipalId securityPrincipalId,
	                                     @NonNull String username,
	                                     @NonNull String password,
	                                     boolean enabled,
	                                     boolean accountNonExpired,
	                                     boolean credentialsNonExpired,
	                                     boolean accountNonLocked,
	                                     Collection<? extends GrantedAuthority> authorities ) {
		this.securityPrincipalId = securityPrincipalId;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.authorities = Collections.unmodifiableSet( sortAuthorities( authorities ) );
	}
	// ~ Methods
	// ========================================================================================================

	private static SortedSet<GrantedAuthority> sortAuthorities(
			Collection<? extends GrantedAuthority> authorities ) {
		Assert.notNull( authorities, "Cannot pass a null GrantedAuthority collection" );
		// Ensure array iteration order is predictable (as per
		// UserDetails.getAuthorities() contract and SEC-717)
		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>( new AuthorityComparator() );

		for ( GrantedAuthority grantedAuthority : authorities ) {
			Assert.notNull( grantedAuthority,
			                "GrantedAuthority list cannot contain any null elements" );
			sortedAuthorities.add( grantedAuthority );
		}

		return sortedAuthorities;
	}

	public void eraseCredentials() {
		password = null;
	}

	@Override
	public boolean equals( Object rhs ) {
		if ( rhs instanceof SecurityPrincipalUserDetails ) {
			return securityPrincipalId.equals( ( (SecurityPrincipalUserDetails) rhs ).getSecurityPrincipalId() );
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getSecurityPrincipalId().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( super.toString() ).append( ": " );
		sb.append( "SecurityPrincipalId: " ).append( this.securityPrincipalId ).append( "; " );
		sb.append( "Username: " ).append( this.username ).append( "; " );
		sb.append( "Password: [PROTECTED]; " );
		sb.append( "Enabled: " ).append( this.enabled ).append( "; " );
		sb.append( "AccountNonExpired: " ).append( this.accountNonExpired ).append( "; " );
		sb.append( "credentialsNonExpired: " ).append( this.credentialsNonExpired )
		  .append( "; " );
		sb.append( "AccountNonLocked: " ).append( this.accountNonLocked ).append( "; " );

		if ( !authorities.isEmpty() ) {
			sb.append( "Granted Authorities: " );

			boolean first = true;
			for ( GrantedAuthority auth : authorities ) {
				if ( !first ) {
					sb.append( "," );
				}
				first = false;

				sb.append( auth );
			}
		}
		else {
			sb.append( "Not granted any authorities" );
		}

		return sb.toString();
	}

	private static class AuthorityComparator implements Comparator<GrantedAuthority>,
			Serializable
	{
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

		public int compare( GrantedAuthority g1, GrantedAuthority g2 ) {
			// Neither should ever be null as each entry is checked before adding it to
			// the set.
			// If the authority is null, it is a custom authority and should precede
			// others.
			if ( g2.getAuthority() == null ) {
				return -1;
			}

			if ( g1.getAuthority() == null ) {
				return 1;
			}

			return g1.getAuthority().compareTo( g2.getAuthority() );
		}
	}
}
