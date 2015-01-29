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
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@DiscriminatorValue("oauth2client")
@Table(name = OAuth2SchemaConfiguration.TABLE_CLIENT)
public class OAuth2Client extends BasicSecurityPrincipal<OAuth2Client> implements ClientDetails
{
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.oAuth2Client", cascade = CascadeType.ALL)
	private Set<OAuth2ClientScope> oAuth2ClientScopes = new TreeSet<OAuth2ClientScope>();

	@Cascade(org.hibernate.annotations.CascadeType.MERGE)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = OAuth2SchemaConfiguration.TABLE_RESOURCEID,
			joinColumns = @JoinColumn(name = "client_id")
	)
	@Column(name = "resource_id")
	private Set<String> resourceIds = new HashSet<String>();

	@Cascade(org.hibernate.annotations.CascadeType.MERGE)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = OAuth2SchemaConfiguration.TABLE_GRANT_TYPE,
			joinColumns = @JoinColumn(name = "client_id")
	)
	@Column(name = "grant_type")
	private Set<String> authorizedGrantTypes = new HashSet<String>();

	@Cascade(org.hibernate.annotations.CascadeType.MERGE)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = OAuth2SchemaConfiguration.TABLE_REDIRECT_URI,
			joinColumns = @JoinColumn(name = "client_id")
	)
	@Column(name = "redirect_uri")
	private Set<String> registeredRedirectUri = new HashSet<String>();

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "client_secret")
	private String clientSecret;

	@Column(name = "is_secret_required")
	private boolean isSecretRequired;

	@Column(name = "access_token_validity_seconds")
	private Integer accessTokenValiditySeconds;

	@Column(name = "refresh_token_validity_seconds")
	private Integer refreshTokenValiditySeconds;

	@Override
	public String getClientId() {
		return clientId;
	}

	public void setClientId( String clientId ) {
		this.clientId = clientId;
		setPrincipalName( clientId );
	}

	@Override
	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds( Set<String> resourceIds ) {
		this.resourceIds = resourceIds;
	}

	@Override
	public boolean isSecretRequired() {
		return isSecretRequired;
	}

	public void setSecretRequired( boolean isSecretRequired ) {
		this.isSecretRequired = isSecretRequired;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret( String clientSecret ) {
		this.clientSecret = clientSecret;
	}

	@Override
	public boolean isScoped() {
		return !oAuth2ClientScopes.isEmpty();
	}

	@Override
	public Set<String> getScope() {
		Set<String> scopeNames = new HashSet<String>();
		for ( OAuth2ClientScope oAuth2ClientScope : oAuth2ClientScopes ) {
			scopeNames.add( oAuth2ClientScope.getOAuth2Scope().getName() );
		}
		return scopeNames;
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes( Set<String> authorizedGrantTypes ) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return registeredRedirectUri;
	}

	public void setRegisteredRedirectUri( Set<String> registeredRedirectUri ) {
		this.registeredRedirectUri = registeredRedirectUri;
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds( Integer accessTokenValiditySeconds ) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds( Integer refreshTokenValiditySeconds ) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	@Override
	public boolean isAutoApprove( String scopeName ) {
		for ( OAuth2ClientScope oAuth2ClientScope : oAuth2ClientScopes ) {
			if ( StringUtils.equalsIgnoreCase( scopeName, oAuth2ClientScope.getOAuth2Scope().getName() ) ) {
				return oAuth2ClientScope.isAutoApprove();
			}
		}
		return false;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		return new LinkedHashMap<>();
	}

	public Set<OAuth2ClientScope> getOAuth2ClientScopes() {
		return oAuth2ClientScopes;
	}

	public void setoAuth2ClientScopes( Set<OAuth2ClientScope> oAuth2ClientScopes ) {
		this.oAuth2ClientScopes = oAuth2ClientScopes;
	}
}
