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
package com.foreach.across.test.modules.it.spring.security;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITSpringSecurityWithoutWeb.Config.class)
public class ITSpringSecurityWithoutWeb
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Autowired(required = false)
	private SecurityPrincipalService securityPrincipalService;

	@Test
	public void authenticationManagerBuilderShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME,
		                                                            AuthenticationManagerBuilder.class ) );
	}

	@Test
	public void securityPrincipalServiceShouldExist() {
		assertNotNull( securityPrincipalService );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( springSecurityModule() );
		}

		private SpringSecurityModule springSecurityModule() {
			return new SpringSecurityModule();
		}
	}
}
