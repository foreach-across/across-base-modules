package com.foreach.across.modules.spring.security.infrastructure;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
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
		addApplicationContextConfigurer( new AnnotatedClassConfigurer( SecurityPrincipalServiceConfiguration.class ) );
	}
}
