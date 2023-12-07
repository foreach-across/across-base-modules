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

import com.foreach.across.core.AcrossException;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistryInterceptor;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.jupiter.api.Test;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.jupiter.api.Assertions.*;

public class ITDebugWebModule
{
	@Test
	public void minimalBootstrap() {
		try (AcrossTestWebContext ctx = web().useTestDataSource( false )
		                                     .modules( DebugWebModule.NAME )
		                                     .build()) {
			DebugWeb debugWeb = ctx.getBeanOfType( DebugWeb.class );
			assertNotNull( debugWeb );
			assertEquals( "/debug", debugWeb.getPathPrefix() );

			assertNotNull( ctx.getBean( "debugWebTemplateRegistry", WebTemplateRegistry.class ) );
			assertNotNull( ctx.getBean( "debugWebResourcePackageManager", WebResourcePackageManager.class ) );
			assertNotNull( ctx.getBean( "debugWebResourceRegistryInterceptor", WebResourceRegistryInterceptor.class ) );
			assertNotNull( ctx.getBean( "debugWebHandlerMapping", PrefixingRequestMappingHandlerMapping.class ) );
		}
	}

	public void defaultRequiresPasswordToBeSet() {
		assertThrows( AcrossException.class, () -> {
			try (AcrossTestWebContext ignore = web().useTestDataSource( false )
			                                        .modules( DebugWebModule.NAME, SpringSecurityModule.NAME )
			                                        .build()) {
				fail( "Should not have started" );
			}
		} );

	}
}
