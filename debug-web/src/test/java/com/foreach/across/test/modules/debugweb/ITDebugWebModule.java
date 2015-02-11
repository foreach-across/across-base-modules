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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistryInterceptor;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITDebugWebModule.Config.class)
public class ITDebugWebModule
{
	@Autowired
	@Qualifier("debugWebTemplateRegistry")
	private WebTemplateRegistry debugWebTemplateRegistry;

	@Autowired
	@Qualifier("debugWebResourcePackageManager")
	private WebResourcePackageManager debugWebResourcePackageManager;

	@Autowired
	@Qualifier("debugWebResourceRegistryInterceptor")
	private WebResourceRegistryInterceptor debugWebResourceRegistryInterceptor;

	@Autowired
	@Qualifier("debugWebHandlerMapping")
	private PrefixingRequestMappingHandlerMapping debugHandlerMapping;

	@Autowired
	private DebugWeb debugWeb;

	@Test
	public void bootstrapModule() {
		assertNotNull( debugWeb );
		assertEquals( "/development/debug", debugWeb.getPathPrefix() );

		assertNotNull( debugWebTemplateRegistry );
		assertNotNull( debugWebResourcePackageManager );
		assertNotNull( debugWebResourceRegistryInterceptor );
		assertNotNull( debugHandlerMapping );
	}

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( debugWebModule() );
		}

		private DebugWebModule debugWebModule() {
			DebugWebModule module = new DebugWebModule();
			module.setRootPath( "/development/debug" );
			return module;
		}
	}
}
