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

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalHierarchy;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.*;

/**
 * Extension to {@link BasicSecurityPrincipal}
 * that allows being a member of one or more principal groups.
 *
 * @author Arne Vandamme
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class GroupedPrincipal<T extends SettableIdBasedEntity<?>>
		extends BasicSecurityPrincipal<T>
		implements SecurityPrincipalHierarchy
{
	@ManyToMany(fetch = FetchType.EAGER)
	@BatchSize(size = 50)
	@JoinTable(
			name = UserSchemaConfiguration.TABLE_PRINCIPAL_GROUP,
			joinColumns = @JoinColumn(name = "principal_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id"))
	private Set<Group> groups = new TreeSet<>();

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups( Set<Group> groups ) {
		this.groups = groups;
	}

	public boolean isMemberOf( Group group ) {
		return groups.contains( group );
	}

	public void addGroup( Group group ) {
		groups.add( group );
	}

	public void removeGroup( Group group ) {
		groups.remove( group );
	}

	@Override
	public boolean hasPermission( Permission permission ) {
		if ( super.hasPermission( permission ) ) {
			return true;
		}

		for ( Group group : getGroups() ) {
			if ( group.hasPermission( permission ) ) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new LinkedHashSet<>();

		for ( Role role : getRoles() ) {
			authorities.add( role );
			for ( Permission permission : role.getPermissions() ) {
				authorities.add( permission );
			}
		}

		for ( Group group : getGroups() ) {
			authorities.addAll( group.getAuthorities() );
		}

		return authorities;
	}

	@Override
	public Collection<SecurityPrincipal> getParentPrincipals() {
		return groups == null || groups.isEmpty()
				? Collections.<SecurityPrincipal>emptyList()
				: new ArrayList<SecurityPrincipal>( groups );
	}
}
