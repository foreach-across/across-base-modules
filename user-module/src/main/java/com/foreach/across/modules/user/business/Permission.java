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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A single permission that can be checked against.
 */
@Entity
@Table(name = UserSchemaConfiguration.TABLE_PERMISSION)
public class Permission
		extends SettableIdBasedEntity<Permission>
		implements GrantedAuthority, Comparable<GrantedAuthority>, Serializable
{
	@Id
	@GeneratedValue(generator = "seq_um_permission_id")
	@GenericGenerator(
			name = "seq_um_permission_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_permission_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "5")
			}
	)
	private long id;

	@NotBlank
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "description")
	private String description;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "permission_group_id")
	private PermissionGroup group;

	public Permission() {
	}

	public Permission( String name ) {
		setName( name );
	}

	public Permission( String name, String description ) {
		setName( name );
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = StringUtils.lowerCase( name );
	}

	@Override
	public String getAuthority() {
		return getName();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public PermissionGroup getGroup() {
		return group;
	}

	public void setGroup( PermissionGroup group ) {
		this.group = group;
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
		return getName() != null ? getName().hashCode() : 0;
	}

	@Override
	public String toString() {
		return getName();
	}

	private void writeObject( ObjectOutputStream oos ) throws IOException {
		oos.writeObject( name );
	}

	private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException {
		name = (String) ois.readObject();
	}
}
