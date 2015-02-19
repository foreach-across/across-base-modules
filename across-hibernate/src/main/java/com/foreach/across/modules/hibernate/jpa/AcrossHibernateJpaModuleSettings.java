package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.core.AcrossModuleSettingsRegistry;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;

public class AcrossHibernateJpaModuleSettings extends AcrossHibernateModuleSettings
{

	public static final String HIBERNATE_JPA_DIALECT = "acrossHibernateJpa.dialect";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		super.registerSettings( registry );
		registry.register( HIBERNATE_JPA_DIALECT, JpaDialect.class, new HibernateJpaDialect(),
		                   "Specify the JpaDialect. Allows for configuring custom implementations." );
	}

	public JpaDialect getJpaDialect(){
		return getProperty( HIBERNATE_JPA_DIALECT, JpaDialect.class );
	}
}
