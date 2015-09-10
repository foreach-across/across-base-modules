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
package com.foreach.across.modules.spring.security.infrastructure.config;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.spring.security.config.ModuleGlobalMethodSecurityConfiguration;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;

/**
 * The security infrastructure bean is exposed and provides other modules easy access
 * to core security related services.
 *
 * @author Arne Vandamme
 */
@Configuration
@AcrossEventHandler
public class SecurityInfrastructure
{
	@Bean
	public AuthenticationTrustResolver authenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}

	@Event
	protected void registerModuleMethodSecurity( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		if ( !isSecurityModule( beforeBootstrapEvent.getBootstrapConfig() ) ) {
			beforeBootstrapEvent.getBootstrapConfig().addApplicationContextConfigurer(
					new AnnotatedClassConfigurer( ModuleGlobalMethodSecurityConfiguration.class )
			);
		}
	}

	private boolean isSecurityModule( ModuleBootstrapConfig moduleBootstrapConfig ) {
		switch ( moduleBootstrapConfig.getModuleName() ) {
			case SpringSecurityInfrastructureModule.ACL_MODULE:
			case SpringSecurityInfrastructureModule.NAME:
				return true;
			default:
				return false;
		}
	}
}
