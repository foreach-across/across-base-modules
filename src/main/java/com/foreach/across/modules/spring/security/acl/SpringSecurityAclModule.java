package com.foreach.across.modules.spring.security.acl;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.config.AclSecurityConfiguration;
import com.foreach.across.modules.spring.security.acl.config.ModuleAclSecurityConfiguration;
import com.foreach.across.modules.spring.security.acl.installers.AclSchemaInstaller;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;

import java.util.Set;

/**
 * @author Arne Vandamme
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(
		required = { SpringSecurityModule.NAME, SpringSecurityInfrastructureModule.NAME },
               optional = "EhcacheModule"
)
public class SpringSecurityAclModule extends AcrossModule
{
	public static final String NAME = "SpringSecurityAclModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Spring Security ACL module - provides ACL infrastructure.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( new AnnotatedClassConfigurer( AclSecurityConfiguration.class ) );
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		setProperties( contextConfig.getModule( SpringSecurityModule.NAME ).getModule().getProperties() );

		for ( ModuleBootstrapConfig module : contextConfig.getModules() ) {
			// Later modules can use ACL permission checking
			if ( module.getBootstrapIndex() > currentModule.getBootstrapIndex() ) {
				module.addApplicationContextConfigurer(
						new AnnotatedClassConfigurer( ModuleAclSecurityConfiguration.class )
				);
			}
		}
	}

	@Override
	public Object[] getInstallers() {
		return new Object[] { AclSchemaInstaller.class };
	}
}
