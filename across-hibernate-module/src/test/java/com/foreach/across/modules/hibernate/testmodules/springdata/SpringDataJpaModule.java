package com.foreach.across.modules.hibernate.testmodules.springdata;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfiguringModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageRegistry;

import java.util.Set;

@AcrossDepends(required = AcrossHibernateJpaModule.NAME)
public class SpringDataJpaModule extends AcrossModule implements HibernatePackageConfiguringModule
{
	public SpringDataJpaModule() {
	}

	@Override
	public String getName() {
		return "SpringDataJpaModule";
	}

	@Override
	public String getDescription() {
		return "Module containing a single JPA managed entity through a generated Spring data repository.";
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

