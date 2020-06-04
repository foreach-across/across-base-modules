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
package test;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import com.foreach.across.modules.spring.security.infrastructure.services.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestSecurityPrincipalService.Config.class)
public class TestSecurityPrincipalService
{
	@Autowired
	private AuthenticationSecurityPrincipalResolver authenticationSecurityPrincipalResolver;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	private CurrentSecurityPrincipalProxy currentPrincipal;

	@After
	public void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void authenticateAndAuthenticationBlock() {
		SecurityPrincipal one = mock( SecurityPrincipal.class );
		SecurityPrincipal two = mock( SecurityPrincipal.class );

		assertNull( currentPrincipal.getPrincipal() );
		assertNull( SecurityContextHolder.getContext().getAuthentication() );

		SecurityPrincipalId oneId = SecurityPrincipalId.of( "one" );
		when( one.getSecurityPrincipalId() ).thenReturn( oneId );
		SecurityPrincipalId twoId = SecurityPrincipalId.of( "two" );
		when( two.getSecurityPrincipalId() ).thenReturn( twoId );

		try (CloseableAuthentication ignored = securityPrincipalService.authenticate( one )) {
			when( authenticationSecurityPrincipalResolver.resolveSecurityPrincipal( SecurityContextHolder.getContext().getAuthentication() ) )
					.thenReturn( Optional.of( one ) );
			assertSame( one, currentPrincipal.getPrincipal() );
			assertSame( oneId, SecurityContextHolder.getContext().getAuthentication().getPrincipal() );

			try (CloseableAuthentication ignore = securityPrincipalService.authenticate( two )) {
				when( authenticationSecurityPrincipalResolver.resolveSecurityPrincipal( SecurityContextHolder.getContext().getAuthentication() ) )
						.thenReturn( Optional.of( two ) );
				assertSame( two, currentPrincipal.getPrincipal() );
				assertSame( twoId, SecurityContextHolder.getContext().getAuthentication().getPrincipal() );
			}

			assertSame( one, currentPrincipal.getPrincipal() );
			assertSame( oneId, SecurityContextHolder.getContext().getAuthentication().getPrincipal() );
		}

		assertNull( currentPrincipal.getPrincipal() );
		assertNull( SecurityContextHolder.getContext().getAuthentication() );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public SecurityPrincipalService securityPrincipalService() {
			return new SecurityPrincipalServiceImpl( mock( SecurityPrincipalRetrievalStrategy.class ) );
		}

		@Bean
		public CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy() {
			return new CurrentSecurityPrincipalProxyImpl();
		}

		@Bean
		public AuthenticationSecurityPrincipalResolver authenticationSecurityPrincipalResolver() {
			return mock( AuthenticationSecurityPrincipalResolver.class );
		}

		@Bean
		public SecurityInfrastructure securityInfrastructure() {
			SecurityInfrastructure securityInfrastructure = mock( SecurityInfrastructure.class );
			when( securityInfrastructure.authenticationTrustResolver() ).thenReturn( mock( AuthenticationTrustResolver.class ) );
			return securityInfrastructure;
		}
	}
}
