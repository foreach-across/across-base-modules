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

import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalAuthenticationToken;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import com.foreach.across.modules.spring.security.infrastructure.events.SecurityPrincipalRenamedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Arne Vandamme
 * @see SecurityPrincipalRetrievalStrategy
 */
@Service
public class SecurityPrincipalServiceImpl implements SecurityPrincipalService
{
	private SecurityPrincipalRetrievalStrategy securityPrincipalRetrievalStrategy;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

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
			key = "#principalName.get().toLowerCase()",
			condition = "#principalName.isPresent()",
			unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY
	)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> Optional<T> getPrincipalByName( String principalName ) {
		return (Optional<T>) securityPrincipalRetrievalStrategy.getPrincipalByName( principalName );
	}

	@Cacheable(
			value = SpringSecurityModuleCache.SECURITY_PRINCIPAL,
			key = "#securityPrincipalId.get().id.toLowerCase()",
			condition = "#securityPrincipalId.isPresent()",
			unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY
	)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> Optional<T> getPrincipalById( SecurityPrincipalId securityPrincipalId ) {
		return (Optional<T>) securityPrincipalRetrievalStrategy.getPrincipalByName( securityPrincipalId.getId() );
	}

	@Override
	@Transactional
	public void publishRenameEvent( String oldPrincipalName, String newPrincipalName ) {
		SecurityPrincipalRenamedEvent renamedEvent = new SecurityPrincipalRenamedEvent( oldPrincipalName,
		                                                                                newPrincipalName );

		eventPublisher.publishEvent( renamedEvent );
	}
}
