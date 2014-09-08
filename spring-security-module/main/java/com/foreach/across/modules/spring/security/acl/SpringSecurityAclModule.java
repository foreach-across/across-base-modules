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
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.provider.HasHibernatePackageProvider;
import com.foreach.across.modules.hibernate.provider.HibernatePackageProvider;
import com.foreach.across.modules.hibernate.provider.PackagesToScanProvider;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.config.AclSecurityConfiguration;
import com.foreach.across.modules.spring.security.acl.config.ModuleAclSecurityConfiguration;
import com.foreach.across.modules.spring.security.acl.config.modules.AcrossHibernateModuleConfiguration;
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
public class SpringSecurityAclModule extends AcrossModule implements HasHibernatePackageProvider
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
		                                                      AcrossHibernateModuleConfiguration.class ) );
	}

	@Override
	public HibernatePackageProvider getHibernatePackageProvider( AcrossHibernateModule hibernateModule ) {
		if ( hibernateModule.getName().equals( "AcrossHibernateModule" ) ) {
			return new PackagesToScanProvider( "com.foreach.across.modules.spring.security.acl.business" );
		}

		return null;
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
