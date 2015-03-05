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
package com.foreach.across.modules.entity.testmodules.springdata.business;

import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Represents a link between client and group, with an extra role property.  This is the role
 * that the client has in that group.
 *
 * @author Arne Vandamme
 */
@Entity
public class ClientGroup implements Persistable<ClientGroupId>
{
	@EmbeddedId
	private ClientGroupId id = new ClientGroupId();

	@Column
	private String role;

	public ClientGroupId getId() {
		return id;
	}

	public void setId( ClientGroupId id ) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole( String role ) {
		this.role = role;
	}

	@Override
	public boolean isNew() {
		return id == null || id.getClient() == null || id.getGroup() == null;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		ClientGroup that = (ClientGroup) o;

		if ( id != null ? !id.equals( that.id ) : that.id != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
