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

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.support.config.MockAcrossServletContextInitializer;
import com.foreach.across.test.support.config.MockMvcConfiguration;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import test.app.SpringSecurityTestApplication;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Steven Gentens
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext
@SpringBootTest(classes = { SpringSecurityTestApplication.class, MockMvcConfiguration.class })
@ContextConfiguration(initializers = MockAcrossServletContextInitializer.class)
@ActiveProfiles("custom-auth")
public class TestApplicationWithDefaultSecurity
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Autowired(required = false)
	private FilterChainProxy filterChainProxy;

	@Autowired(required = false)
	private WebInvocationPrivilegeEvaluator webInvocationPrivilegeEvaluator;

	@Autowired(required = false)
	private SecurityExpressionHandler securityExpressionHandler;

	@Autowired(required = false)
	@Qualifier("requestDataValueProcessor")
	private Object requestDataValueProcessor;

	@After
	public void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void authenticationManagerBuilderShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME, AuthenticationManagerBuilder.class ) );
	}

	@Test
	public void exposedBeans() {
		assertNotNull( filterChainProxy );
		assertNotNull( securityExpressionHandler );
		assertNotNull( requestDataValueProcessor );
		assertNotNull( webInvocationPrivilegeEvaluator );
	}

	@Test
	@SneakyThrows
	public void blockedShouldNotBeAllowed() {
		mockMvc.perform( get( "/blocked" ) )
		       .andExpect( status().isUnauthorized() );
	}

	@Test
	@Ignore("As of Boot 2.0 everything is secured by default")
	@SneakyThrows
	public void defaultPathsAreNotSecured() {
		mockMvc.perform( get( "/hello" ) )
		       .andExpect( status().isOk() )
		       .andExpect( content().string( "hello" ) );
		mockMvc.perform( get( "/hello-public" ) )
		       .andExpect( status().isOk() )
		       .andExpect( content().string( "hello-public" ) );
	}

	@Test
	@Ignore("As of Boot 2.0 everything is secured by default")
	@SneakyThrows
	public void errorPageShouldNotBeSecured() {
		mockMvc.perform( get( "/error" ) )
		       .andExpect( status().isInternalServerError() )
		       .andExpect( content().string( containsString( "No message available" ) ) );
	}
}
