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
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy;
import com.foreach.across.test.support.config.MockAcrossServletContextInitializer;
import com.foreach.across.test.support.config.MockMvcConfiguration;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import test.app.SpringSecurityTestApplication;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext
@SpringBootTest(classes = { SpringSecurityTestApplication.class, MockMvcConfiguration.class })
@TestPropertySource(properties = { "spring.security.user.password={noop}mypwd", "server.error.include-message=ALWAYS" })
@ContextConfiguration(initializers = MockAcrossServletContextInitializer.class)
public class TestApplicationWithBootAuthenticationManager
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SecurityPrincipalRetrievalStrategy securityPrincipalRetrievalStrategy;

	@Test
	@SneakyThrows
	public void blockedShouldNotBeAllowed() {
		mockMvc.perform( get( "/blocked" ) )
		       .andExpect( status().isForbidden() );
		mockMvc.perform( get( "/blocked" ).with( httpBasic( "user", "mypwd" ) ) )
		       .andExpect( status().isForbidden() );
	}

	@Test
	@SneakyThrows
	public void helloShouldBeSecured() {
		mockMvc.perform( get( "/hello" ) )
		       .andExpect( status().isUnauthorized() );
	}

	@Test
	@SneakyThrows
	public void helloCanBeCalledWithSpringBootUser() {
		mockMvc.perform( get( "/hello" ).with( httpBasic( "user", "mypwd" ) ) )
		       .andExpect( status().isOk() )
		       .andExpect( content().string( "hello" ) );
	}

	@Test
	@SneakyThrows
	public void helloFailsWithCustomUser() {
		mockMvc.perform( get( "/hello" ).with( httpBasic( "dashboard", "dashboard" ) ) )
		       .andExpect( status().isUnauthorized() );
	}

	@Test
	@SneakyThrows
	public void helloPublicShouldNotBeSecured() {
		mockMvc.perform( get( "/hello-public" ) )
		       .andExpect( status().isOk() )
		       .andExpect( content().string( "hello-public" ) );
	}

	@Test
	@SneakyThrows
	public void errorPageShouldNotBeSecured() {
		mockMvc.perform( get( "/error" ) )
		       .andExpect( status().isInternalServerError() )
		       .andExpect( content().string( containsString( "No message available" ) ) );
	}

	@Test
	@SneakyThrows
	public void currentSecurityPrincipalShouldBeUnknownWithRegularSpringSecurityUserDetails() {
		mockMvc.perform( get( "/current-user" ) )
		       .andExpect( status().isUnauthorized() );

		mockMvc.perform( get( "/current-user" ).with( httpBasic( "user", "mypwd" ) ) )
		       .andExpect( content().string( containsString( "unknown" ) ) );
	}

	@Test
	@SneakyThrows
	public void currentSecurityPrincipalShouldReturnSecurityPrincipalInAcross() {
		User user = User.builder().principalName( "userPrincipal" ).firstName( "firstname" ).lastName( "lastname" ).build();
		SecurityContext securityContext = new SecurityContextImpl(
				new UsernamePasswordAuthenticationToken( SecurityPrincipalId.of( "userPrincipal" ), null, Collections.emptyList() ) );
		when( securityPrincipalRetrievalStrategy.getPrincipalByName( "userPrincipal" ) ).thenReturn( Optional.of( user ) );
		mockMvc.perform( get( "/current-user" ) )
		       .andExpect( status().isUnauthorized() );
		mockMvc.perform( get( "/current-user" )
				                 .with( securityContext( securityContext ) ) )
		       .andExpect( content().string( containsString( "userPrincipal:firstname:lastname" ) ) );

	}

	@Builder
	@Data
	public static class User implements SecurityPrincipal
	{
		private String principalName;
		private String firstName;
		private String lastName;

		@Override
		public String getPrincipalName() {
			return principalName;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return Collections.emptyList();
		}

		@Override
		public String toString() {
			return principalName;
		}
	}
}
