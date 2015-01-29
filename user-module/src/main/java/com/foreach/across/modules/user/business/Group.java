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

import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue("group")
@Table(name = UserSchemaConfiguration.TABLE_GROUP)
public class Group extends BasicSecurityPrincipal<Group> implements Comparable<Group>
{
	@NotBlank
	@Size(max = 255)
	@Column(name = "name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
		setPrincipalName( "group:" + name );
	}

	@Override
	public int compareTo( Group other ) {
		return StringUtils.defaultString( getName() ).compareTo( StringUtils.defaultString( other.getName() ) );
	}
}
