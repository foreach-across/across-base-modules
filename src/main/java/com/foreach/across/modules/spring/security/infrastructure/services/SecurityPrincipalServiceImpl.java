package com.foreach.across.modules.spring.security.infrastructure.services;

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.events.SecurityPrincipalRenamedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
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
	public void authenticate( SecurityPrincipal principal ) {
		PreAuthenticatedAuthenticationToken authRequest = new PreAuthenticatedAuthenticationToken(
				principal, null, principal.getAuthorities()
		);

		SecurityContextHolder.getContext().setAuthentication( authRequest );
	}

	@Override
	public void clearAuthentication() {
		SecurityContextHolder.clearContext();
	}

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
