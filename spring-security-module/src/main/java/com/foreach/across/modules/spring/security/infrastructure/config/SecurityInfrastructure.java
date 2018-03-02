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

import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.config.ModuleGlobalMethodSecurityConfiguration;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * The security infrastructure bean is exposed and provides other modules easy access
 * to core security related services.
 *
 * @author Arne Vandamme
 */
@Configuration
@RequiredArgsConstructor
public class SecurityInfrastructure
{
	private final AcrossContextBeanRegistry contextBeanRegistry;

	@Bean
	public AuthenticationTrustResolver authenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new DelegatingClientAuthenticationManager( contextBeanRegistry );
	}

	private static final class DelegatingClientAuthenticationManager implements AuthenticationManager
	{
		private final AcrossContextBeanRegistry contextBeanRegistry;
		private AuthenticationManager delegate;

		private DelegatingClientAuthenticationManager( AcrossContextBeanRegistry contextBeanRegistry ) {
			this.contextBeanRegistry = contextBeanRegistry;
		}

		@Override
		public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
			if ( delegate != null ) {
				return delegate.authenticate( authentication );
			}

			return authentication;
		}

		@PostRefresh
		public void refresh() {
			delegate = contextBeanRegistry
					.getBeanOfTypeFromModule( SpringSecurityModule.NAME, AuthenticationManagerBuilder.class )
					.getOrBuild();
		}
	}

	@EventListener
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
