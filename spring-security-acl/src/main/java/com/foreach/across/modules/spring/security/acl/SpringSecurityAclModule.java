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
package com.foreach.across.modules.spring.security.acl;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfiguringModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.config.AclSecurityConfiguration;
import com.foreach.across.modules.spring.security.acl.config.ModuleAclSecurityConfiguration;
import com.foreach.across.modules.spring.security.acl.config.modules.AcrossHibernateJpaModuleConfiguration;
import com.foreach.across.modules.spring.security.acl.config.modules.AdminWebModuleConfiguration;
import com.foreach.across.modules.spring.security.acl.config.modules.SpringSecurityInfrastructureModuleConfiguration;
import com.foreach.across.modules.spring.security.acl.installers.AclEntityAuditableInstaller;
import com.foreach.across.modules.spring.security.acl.installers.AclSchemaInstaller;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author Arne Vandamme
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(
		required = { SpringSecurityModule.NAME, SpringSecurityInfrastructureModule.NAME },
		optional = { "AcrossHibernateJpaModule", "EhcacheModule" }
)
public class SpringSecurityAclModule extends AcrossModule implements HibernatePackageConfiguringModule
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
		contextConfigurers.add( new AnnotatedClassConfigurer( AclSecurityConfiguration.class,
		                                                      AcrossHibernateJpaModuleConfiguration.class,
		                                                      AdminWebModuleConfiguration.class ) );
	}

	@Override
	public void configureHibernatePackage( HibernatePackageRegistry hibernatePackage ) {
		if ( StringUtils.equals( AcrossHibernateJpaModule.NAME, hibernatePackage.getName() ) ) {
			hibernatePackage.addPackageToScan( "com.foreach.across.modules.spring.security.acl.business" );
		}
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		contextConfig.getModule( SpringSecurityInfrastructureModule.NAME ).addApplicationContextConfigurer(
				new AnnotatedClassConfigurer( SpringSecurityInfrastructureModuleConfiguration.class )
		);

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
		return new Object[] { AclSchemaInstaller.class,
		                      AclEntityAuditableInstaller.class };
	}
}
