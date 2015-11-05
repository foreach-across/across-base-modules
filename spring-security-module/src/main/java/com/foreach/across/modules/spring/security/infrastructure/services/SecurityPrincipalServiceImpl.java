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
package com.foreach.across.modules.spring.security.infrastructure.services;

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalAuthenticationToken;
import com.foreach.across.modules.spring.security.infrastructure.events.SecurityPrincipalRenamedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Service
public class SecurityPrincipalServiceImpl implements SecurityPrincipalService
{
	private SecurityPrincipalRetrievalStrategy securityPrincipalRetrievalStrategy;

	@Autowired
	private AcrossEventPublisher eventPublisher;

	public SecurityPrincipalServiceImpl( SecurityPrincipalRetrievalStrategy securityPrincipalRetrievalStrategy ) {
		this.securityPrincipalRetrievalStrategy = securityPrincipalRetrievalStrategy;
	}

	public void setSecurityPrincipalRetrievalStrategy( SecurityPrincipalRetrievalStrategy securityPrincipalRetrievalStrategy ) {
		this.securityPrincipalRetrievalStrategy = securityPrincipalRetrievalStrategy;
	}

	@Override
	public CloseableAuthentication authenticate( SecurityPrincipal principal ) {
		return new CloseableAuthentication( new SecurityPrincipalAuthenticationToken( principal ) );
	}

	@Override
	public void clearAuthentication() {
		SecurityContextHolder.clearContext();
	}

	@Cacheable(
			value = SpringSecurityModuleCache.SECURITY_PRINCIPAL,
			key = "#principalName.toLowerCase()",
			condition = "#principalName != null",
			unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY
	)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> T getPrincipalByName( String principalName ) {
		return (T) securityPrincipalRetrievalStrategy.getPrincipalByName( principalName );
	}

	@Override
	@Transactional
	public void publishRenameEvent( String oldPrincipalName, String newPrincipalName ) {
		SecurityPrincipalRenamedEvent renamedEvent = new SecurityPrincipalRenamedEvent( oldPrincipalName,
		                                                                                newPrincipalName );

		eventPublisher.publish( renamedEvent );
	}
}
