package com.foreach.across.modules.hibernate.testmodules.jpa;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfiguringModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageRegistry;

import java.util.Set;

@AcrossDepends(required = AcrossHibernateJpaModule.NAME)
public class SimpleJpaModule extends AcrossModule implements HibernatePackageConfiguringModule
{
	@Override
	public String getName() {
		return "SimpleJpaModule";
	}

	@Override
	public String getDescription() {
		return "Module containing a single JPA managed entity and repository the old fashioned - manual - way.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( new ComponentScanConfigurer( getClass().getPackage().getName() ) );
	}

	@Override
	public void configureHibernatePackage( HibernatePackageRegistry hibernatePackage ) {
		hibernatePackage.addPackageToScan( getClass() );
	}
}
