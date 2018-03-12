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
package com.foreach.across.modules.spring.security.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Enables Spring method security in modules, ensuring that the same AuthenticationManager is being used.
 * This exposes an AuthenticationManager delegate in every module.
 */
@ModuleConfiguration(exclude = "SpringSecurityAclModule")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ModuleGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration
{
	private SecurityInfrastructure securityInfrastructure;

	@Autowired
	private void setSecurityInfrastructure( SecurityInfrastructure securityInfrastructure ) {
		this.securityInfrastructure = securityInfrastructure;
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		MethodSecurityExpressionHandler expressionHandler = super.createExpressionHandler();
		if ( expressionHandler instanceof DefaultMethodSecurityExpressionHandler ) {
			( (DefaultMethodSecurityExpressionHandler) expressionHandler )
					.setTrustResolver( securityInfrastructure.authenticationTrustResolver() );
		}
		return expressionHandler;
	}

	@Override
	protected AuthenticationManager authenticationManager() {
		return securityInfrastructure.authenticationManager();
	}
}
