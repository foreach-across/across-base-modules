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
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.filters.BeanFilterComposite;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityInfrastructure;
import com.foreach.across.modules.spring.security.infrastructure.config.SecurityPrincipalServiceConfiguration;

import java.util.Set;

/**
 * @author Arne Vandamme
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(optional = "EhcacheModule")
public class SpringSecurityInfrastructureModule extends AcrossModule
{
	public static final String NAME = "SpringSecurityInfrastructureModule";

	public SpringSecurityInfrastructureModule() {
		// Exposed the security infrastructure bean manually, but don't annotate it as that would also expose
		// the separate security beans and we don't want that
		setExposeFilter(
				new BeanFilterComposite( defaultExposeFilter(), new ClassBeanFilter( SecurityInfrastructure.class ) )
		);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Spring Security infrastructure module - provides security services available in the early stages " +
				"of an Across context. This module is added automatically by the SpringSecurityModule.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add(
				new AnnotatedClassConfigurer(
						SecurityPrincipalServiceConfiguration.class,
						SecurityInfrastructure.class

				)
		);
	}
}
