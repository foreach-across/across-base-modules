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

package com.foreach.across.test.modules.debugweb;

import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.debugweb.DebugWebModuleSettings;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 */
public class TestDebugWebSecurity
{
	@Test
	public void bootstrapWithoutDefaultSecurity() throws Exception {
		try (AcrossTestWebContext ctx = web().useTestDataSource( false )
		                                     .modules( DebugWebModule.NAME )
		                                     .property( DebugWebModuleSettings.SECURITY_ENABLED, false )
		                                     .property( "debugWebModule.root-path", "/development/debug" )
		                                     .build()) {
			DebugWeb debugWeb = ctx.getBeanOfType( DebugWeb.class );
			assertNotNull( debugWeb );
			assertEquals( "/development/debug", debugWeb.getPathPrefix() );

			ctx.mockMvc()
			   .perform( get( "/debug" ) )
			   .andExpect( status().isNotFound() );

			ctx.mockMvc()
			   .perform( get( "/development/debug" ) )
			   .andExpect( status().isOk() );
		}
	}

	@Test
	public void debugPathShouldBeProtected() throws Exception {
		try (AcrossTestWebContext ctx = web().useTestDataSource( false )
		                                     .modules( DebugWebModule.NAME, SpringSecurityModule.NAME )
		                                     .property( "debugWebModule.security.password", "hurrah" )
		                                     .property( "debugWebModule.security.ip-addresses", "" )
		                                     .property( "debugWebModule.root-path", "/custom/path" )
		                                     .build()) {
			ctx.mockMvc()
			   .perform( get( "/custom/path" ) )
			   .andExpect( status().isUnauthorized() );

			ctx.mockMvc()
			   .perform( get( "/custom/path" ).with( httpBasic( "debug", "hurrah" ) ) )
			   .andExpect( status().isOk() );
		}
	}

	@Test
	public void protectedWithCustomUsername() throws Exception {
		try (AcrossTestWebContext ctx = web().useTestDataSource( false )
		                                     .modules( DebugWebModule.NAME, SpringSecurityModule.NAME )
		                                     .property( "debugWebModule.security.username", "johnny" )
		                                     .property( "debugWebModule.security.password", "janey" )
		                                     .property( "debugWebModule.security.ip-addresses", "" )
		                                     .build()) {
			ctx.mockMvc()
			   .perform( get( "/debug" ) )
			   .andExpect( status().isUnauthorized() );

			ctx.mockMvc()
			   .perform( get( "/debug" ).with( httpBasic( "johnny", "janey" ) ) )
			   .andExpect( status().isOk() );
		}
	}

	@Test
	public void protectedByRole() throws Exception {
		try (AcrossTestWebContext ctx = web().useTestDataSource( false )
		                                     .modules( DebugWebModule.NAME, SpringSecurityModule.NAME )
		                                     .property( "debugWebModule.security.username", "" )
		                                     .property( "debugWebModule.security.ip-addresses", "" )
		                                     .build()) {
			ctx.mockMvc()
			   .perform( get( "/debug" ) )
			   .andExpect( status().isUnauthorized() );

			ctx.mockMvc()
			   .perform( get( "/debug" ).with( user( "john" ).roles( "DEBUG_USER" ) ) )
			   .andExpect( status().isOk() );
		}
	}

	@Test
	public void protectedByCustomAuthority() throws Exception {
		try (AcrossTestWebContext ctx = web().useTestDataSource( false )
		                                     .modules( DebugWebModule.NAME, SpringSecurityModule.NAME )
		                                     .property( "debugWebModule.security.password", "custompwd" )
		                                     .property( "debugWebModule.security.ip-addresses", "" )
		                                     .property( "debugWebModule.security.authority", "access debug" )
		                                     .build()) {
			ctx.mockMvc()
			   .perform( get( "/debug" ) )
			   .andExpect( status().isUnauthorized() );

			ctx.mockMvc()
			   .perform( get( "/debug" ).with( user( "john" ).roles( "DEBUG_USER" ) ) )
			   .andExpect( status().isForbidden() );

			ctx.mockMvc()
			   .perform( get( "/debug" )
					             .with( user( "john" ).authorities( new SimpleGrantedAuthority( "access debug" ) ) ) )
			   .andExpect( status().isOk() );

			ctx.mockMvc()
			   .perform( get( "/debug" ).with( httpBasic( "debug", "custompwd" ) ) )
			   .andExpect( status().isOk() );
		}
	}
}
