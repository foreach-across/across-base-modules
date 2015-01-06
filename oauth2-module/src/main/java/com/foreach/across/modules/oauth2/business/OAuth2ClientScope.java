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

import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = OAuth2SchemaConfiguration.TABLE_CLIENT_SCOPE)
@AssociationOverrides({
		                      @AssociationOverride(name = "pk.oAuth2Client",
		                                           joinColumns = @JoinColumn(name = "client_id")),
		                      @AssociationOverride(name = "pk.oAuth2Scope",
		                                           joinColumns = @JoinColumn(name = "scope_id")) })
public class OAuth2ClientScope implements Comparable
{
	@EmbeddedId
	private OAuth2ClientScopeId pk = new OAuth2ClientScopeId();

	@Column(name = "auto_approve")
	private boolean autoApprove;

	public OAuth2ClientScopeId getPk() {
		return pk;
	}

	public void setPk( OAuth2ClientScopeId pk ) {
		this.pk = pk;
	}

	@Transient
	public OAuth2Client getOAuth2Client() {
		return this.getPk().getOAuth2Client();
	}

	public void setOAuth2Client( OAuth2Client oAuth2Client ) {
		this.getPk().setOAuth2Client( oAuth2Client );
	}

	@Transient
	public OAuth2Scope getOAuth2Scope() {
		return this.getPk().getOAuth2Scope();
	}

	public void setOAuth2Scope( OAuth2Scope oAuth2Scope ) {
		this.getPk().setOAuth2Scope( oAuth2Scope );
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove( boolean autoApprove ) {
		this.autoApprove = autoApprove;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		OAuth2ClientScope that = (OAuth2ClientScope) o;

		return Objects.equals( pk, that.pk );
	}

	@Override
	public int hashCode() {
		return Objects.hash( pk );
	}

	@Override
	public int compareTo( Object o ) {
		OAuth2ClientScope that = (OAuth2ClientScope) o;
		return this.getOAuth2Scope().getName().compareTo( that.getOAuth2Scope().getName() );
	}
}
