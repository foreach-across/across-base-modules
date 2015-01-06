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
package com.foreach.across.modules.spring.security.acl.business;

import com.foreach.across.modules.hibernate.business.SettableIdAuditableEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents a generic named entity that can be used for ACL control.
 * This is usually a more abstract concept: like the default system ACL that does not have any
 * other entity representation (unlike User records for example).
 * <p/>
 * An AclSecurityEntity can optionally have a single parent AclSecurityEntity.
 *
 * @author Arne Vandamme
 */
@Entity
@Table(name = "acl_entity")
public class AclSecurityEntity extends SettableIdAuditableEntity<AclSecurityEntity>
{
	@Id
	@GeneratedValue(generator = "seq_acl_entity_id")
	@GenericGenerator(
			name = "seq_acl_entity_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_acl_entity_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private Long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@ManyToOne(optional = true)
	@JoinColumn(name = "parent_id")
	private AclSecurityEntity parent;

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

	public AclSecurityEntity getParent() {
		return parent;
	}

	public void setParent( AclSecurityEntity parent ) {
		this.parent = parent;
	}

	@Override
	public AclSecurityEntity toDto() {
		AclSecurityEntity entity = new AclSecurityEntity();
		BeanUtils.copyProperties( this, entity );

		return entity;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		AclSecurityEntity that = (AclSecurityEntity) o;

		return Objects.equals( id, that.id );
	}

	@Override
	public int hashCode() {
		return (int) ( id ^ ( id >>> 32 ) );
	}
}
