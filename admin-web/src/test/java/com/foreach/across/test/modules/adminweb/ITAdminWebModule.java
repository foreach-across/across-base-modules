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
package com.foreach.across.test.modules.adminweb;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
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
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITAdminWebModule.Config.class)
public class ITAdminWebModule
{
	@Autowired
	@Qualifier("adminWebTemplateRegistry")
	private WebTemplateRegistry adminWebTemplateRegistry;

	@Autowired
	@Qualifier("adminWebResourcePackageManager")
	private WebResourcePackageManager adminWebResourcePackageManager;

	@Autowired
	@Qualifier("adminWebResourceRegistryInterceptor")
	private WebResourceRegistryInterceptor adminWebResourceRegistryInterceptor;

	@Autowired
	@Qualifier("adminWebHandlerMapping")
	private PrefixingRequestMappingHandlerMapping adminRequestMappingHandlerMapping;

	@Autowired
	private CookieLocaleResolver localeResolver;

	@Autowired
	private AdminWeb adminWeb;

	@Test
	public void exposedBeans() {
		assertNotNull( adminWeb );
		assertEquals( "/administration", adminWeb.getPathPrefix() );

		assertNotNull( adminWebTemplateRegistry );
		assertNotNull( adminWebResourcePackageManager );
		assertNotNull( adminWebResourceRegistryInterceptor );
		assertNotNull( adminRequestMappingHandlerMapping );
		assertNotNull( localeResolver );
	}

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityModule() );
			context.addModule( adminWebModule() );
		}

		private AdminWebModule adminWebModule() {
			AdminWebModule adminWebModule = new AdminWebModule();
			adminWebModule.setRootPath( "/administration/" );

			return adminWebModule;
		}
	}
}
