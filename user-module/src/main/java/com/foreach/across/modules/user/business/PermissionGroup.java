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
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = UserSchemaConfiguration.TABLE_PERMISSION_GROUP)
public class PermissionGroup extends SettableIdBasedEntity<PermissionGroup>
{
	@Id
	@GeneratedValue(generator = "seq_um_permission_group_id")
	@GenericGenerator(
			name = "seq_um_permission_group_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_permission_group_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private long id;

	@NotBlank
	@Size(max = 255)
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Size(max = 255)
	@Column(name = "title")
	private String title;

	@Size(max = 2000)
	@Column(name = "description")
	private String description;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
	@BatchSize(size = 50)
	private Set<Permission> permissions = new TreeSet<>();

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
		this.name = name;
	}

	public String getTitle() {
		return title != null ? title : name;
	}

	public void setTitle( String title ) {
		this.title = title;
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

	public void setPermissions( Set<Permission> permissions ) {
		this.permissions = permissions;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		PermissionGroup that = (PermissionGroup) o;

		if ( !StringUtils.equalsIgnoreCase( getName(), that.getName() ) ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	public String toString() {
		return "PermissionGroup{" +
				"name='" + name + '\'' +
				'}';
	}
}
