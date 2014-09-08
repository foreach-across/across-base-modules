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
package com.foreach.across.modules.spring.security.config;

import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.core.annotations.Refreshable;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.Serializable;

/**
 * Enables Spring method security in modules, ensuring that the same AuthenticationManager is being used.
 * This exposes an AuthenticationManager delegate in every module.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ModuleGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	/*
	@Bean
	@Refreshable
	PermissionEvaluator permissionEvaluator() {
		return new PermissionEvaluator()
		{
			private PermissionEvaluator delegate;

			@Override
			public boolean hasPermission( Authentication authentication,
			                              Object targetDomainObject,
			                              Object permission ) {
				return delegate.hasPermission( authentication, targetDomainObject, permission );
			}

			@Override
			public boolean hasPermission( Authentication authentication,
			                              Serializable targetId,
			                              String targetType,
			                              Object permission ) {
				return delegate.hasPermission( authentication, targetId, targetType, permission );
			}

			@PostRefresh
			public void refresh() {
				delegate = contextBeanRegistry
						.getBeanOfTypeFromModule( SpringSecurityAclModule.NAME, AclPermissionEvaluator.class );
			}
		};
	}
*/
	@Bean
	@Refreshable
	AuthenticationManager delegatingClientAuthenticationManager() {
		return new DelegatingClientAuthenticationManager( contextBeanRegistry );
	}

	@Override
	protected AuthenticationManager authenticationManager() {
		return delegatingClientAuthenticationManager();
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

}
