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
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = UserSchemaConfiguration.TABLE_ROLE)
public class Role extends SettableIdBasedEntity<Role>
		implements GrantedAuthority, Comparable<GrantedAuthority>, Serializable
{
	@Id
	@GeneratedValue(generator = "seq_um_role_id")
	@GenericGenerator(
			name = "seq_um_role_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_role_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "5")
			}
	)
	private long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "description")
	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	@BatchSize(size = 50)
	@JoinTable(
			name = UserSchemaConfiguration.TABLE_ROLE_PERMISSION,
			joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new TreeSet<Permission>();

	public Role() {
	}

	public Role( String name ) {
		setName( name );
	}

	public Role( String name, String description ) {
		setName( name );
		this.description = description;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId( Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void addPermission( String... names ) {
		Permission[] permissions = new Permission[names.length];

		for ( int i = 0; i < names.length; i++ ) {
			permissions[i] = new Permission( names[i] );
		}

		addPermission( permissions );
	}

	public void addPermission( Permission... permissions ) {
		for ( Permission permission : permissions ) {
			getPermissions().add( permission );
		}
	}

	public void setPermissions( Set<Permission> permissions ) {
		this.permissions = permissions;
	}

	public boolean hasPermission( String name ) {
		return hasPermission( new Permission( name ) );
	}

	public boolean hasPermission( Permission permission ) {
		return getPermissions().contains( permission );
	}

	@Override
	public Role toDto() {
		Role role = new Role();
		BeanUtils.copyProperties( this, role );

		return role;
	}

	@Override
	public String getAuthority() {
		return getName();
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || !( o instanceof GrantedAuthority ) ) {
			return false;
		}

		GrantedAuthority that = (GrantedAuthority) o;

		return StringUtils.equalsIgnoreCase( getAuthority(), that.getAuthority() );
	}

	@Override
	public int compareTo( GrantedAuthority o ) {
		return getAuthority().compareTo( o.getAuthority() );
	}

	@Override
	public int hashCode() {
		return getAuthority() != null ? getAuthority().hashCode() : 0;
	}

	@Override
	public String toString() {
		return getAuthority();
	}
}
