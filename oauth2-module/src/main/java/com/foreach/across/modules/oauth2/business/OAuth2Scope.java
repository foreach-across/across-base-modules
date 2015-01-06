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
package com.foreach.across.modules.oauth2.business;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = OAuth2SchemaConfiguration.TABLE_SCOPE)
public class OAuth2Scope extends SettableIdBasedEntity<OAuth2Scope>
{
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_oauth_scope_id")
	@GenericGenerator(
			name = "seq_oauth_scope_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_oauth_scope_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "10")
			}
	)
	private Long id;

	@Column(name = "name")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.oAuth2Scope")
	private Set<OAuth2ClientScope> oAuth2ClientScopes = new HashSet<>();

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

	public Set<OAuth2ClientScope> getOAuth2ClientScopes() {
		return this.oAuth2ClientScopes;
	}

	public void setOAuth2ClientScopes( Set<OAuth2ClientScope> oAuth2ClientScopes ) {
		this.oAuth2ClientScopes = oAuth2ClientScopes;
	}

	@Override
	public OAuth2Scope toDto() {
		OAuth2Scope scope = new OAuth2Scope();
		BeanUtils.copyProperties( this, scope );

		return scope;
	}
}
