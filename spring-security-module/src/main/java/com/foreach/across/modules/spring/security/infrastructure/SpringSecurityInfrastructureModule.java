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
package com.foreach.across.modules.spring.security.infrastructure;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.spring.security.infrastructure.config.AuditableConfiguration;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityPrincipalServiceConfiguration;
import org.springframework.core.Ordered;

import java.util.Set;

/**
 * @author Arne Vandamme
 */
@AcrossRole(value = AcrossModuleRole.INFRASTRUCTURE, order = Ordered.HIGHEST_PRECEDENCE)
@AcrossDepends(optional = "EhcacheModule")
public class SpringSecurityInfrastructureModule extends AcrossModule
{
	public static final String ACL_MODULE = "SpringSecurityAclModule";
	public static final String NAME = "SpringSecurityInfrastructureModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Extension module: provides security services available in the early stages " +
				"of an Across context. This module is added automatically by the SpringSecurityModule and in turn updates AcrossContextInfrastructureModule";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		// don't bootstrap yourself
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		ModuleBootstrapConfig infrastructureModule = contextConfig.getModule( AcrossBootstrapConfigurer.CONTEXT_INFRASTRUCTURE_MODULE );
		infrastructureModule.addApplicationContextConfigurer( SecurityPrincipalServiceConfiguration.class,
		                                                      SecurityInfrastructure.class,
		                                                      AuditableConfiguration.class );
		// Exposed the security infrastructure bean manually, but don't annotate it as that would also expose
		// the separate security beans and we don't want that
		infrastructureModule.addExposeFilter( new ClassBeanFilter( SecurityInfrastructure.class ) );
	}
}
