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
package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;

import java.util.Collection;

public interface OAuth2Service
{
	Collection<OAuth2Client> getOAuth2Clients();

	Collection<OAuth2Scope> getOAuth2Scopes();

	OAuth2Scope saveScope( OAuth2Scope oAuth2Scope );

	OAuth2Scope getScopeById( long id );

	OAuth2Client saveClient( OAuth2Client oAuth2Client );

	OAuth2Client getClientById( String clientId );
}