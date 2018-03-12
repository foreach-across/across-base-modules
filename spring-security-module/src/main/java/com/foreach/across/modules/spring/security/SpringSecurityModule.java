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
package com.foreach.across.modules.spring.security;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.modules.spring.security.config.DynamicSecurityFilterConfiguration;
import com.foreach.across.modules.spring.security.config.SecurityAutoConfigurationAdapter;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;

import java.util.Set;

@AcrossRole(AcrossModuleRole.POSTPROCESSOR)
public class SpringSecurityModule extends AcrossModule
{
	public static final String NAME = "SpringSecurityModule";

	@Override
	protected void setContext( AcrossContext context ) {
		super.setContext( context );

		context.addModule( new SpringSecurityInfrastructureModule() );
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Hooks up Spring Security.  Requires at least one custom SpringSecurityWebConfigurer class to be added at runtime for web support.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( new AnnotatedClassConfigurer( DynamicSecurityFilterConfiguration.class, SecurityAutoConfigurationAdapter.class ) );
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		// Fallback to regular authentication configuration in case there is no web context
		if ( !contextConfig.hasModule( "AcrossWebModule" ) ) {
			currentModule.addApplicationContextConfigurer(
					new AnnotatedClassConfigurer(
							AuthenticationConfiguration.class,
							ObjectPostProcessorConfiguration.class
					)
			);
		}

		currentModule.expose( "requestDataValueProcessor" );
		currentModule.expose( FilterChainProxy.class, WebInvocationPrivilegeEvaluator.class, SecurityExpressionHandler.class );
	}
}
