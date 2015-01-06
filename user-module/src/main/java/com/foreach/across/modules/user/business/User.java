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
package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.repositories.Undeletable;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import com.foreach.across.modules.user.converters.HibernateUserRestriction;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Entity
@DiscriminatorValue("user")
@Table(name = UserSchemaConfiguration.TABLE_USER)
public class User extends GroupedPrincipal<User> implements UserDetails, Undeletable
{
	@Column(nullable = false, name = "username")
	private String username;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "email_confirmed", nullable = false)
	private boolean emailConfirmed;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@Column(name = "restrictions", nullable = true)
	@Type(type = HibernateUserRestriction.CLASS_NAME)
	private Set<UserRestriction> restrictions = EnumSet.noneOf( UserRestriction.class );

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = StringUtils.lowerCase( username );
		setPrincipalName( this.username );
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName( String firstName ) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName( String lastName ) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = StringUtils.lowerCase( email );
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed( boolean emailConfirmed ) {
		this.emailConfirmed = emailConfirmed;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}

	public Set<UserRestriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions( Set<UserRestriction> restrictions ) {
		this.restrictions = restrictions != null ? restrictions : Collections.<UserRestriction>emptySet();
	}

	public boolean hasRestriction( UserRestriction restriction ) {
		return getRestrictions().contains( restriction );
	}

	public boolean hasRestrictions() {
		return !restrictions.isEmpty();
	}

	@Override
	public boolean isAccountNonExpired() {
		return !restrictions.contains( UserRestriction.EXPIRED );
	}

	@Override
	public boolean isAccountNonLocked() {
		return !restrictions.contains( UserRestriction.LOCKED );
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !restrictions.contains( UserRestriction.CREDENTIALS_EXPIRED );
	}

	@Override
	public boolean isEnabled() {
		return !restrictions.contains( UserRestriction.DISABLED )
				&& !restrictions.contains( UserRestriction.REQUIRES_CONFIRMATION );
	}

	@Override
	public User toDto() {
		User user = new User();
		BeanUtils.copyProperties( this, user, "password" );

		return user;
	}

}
