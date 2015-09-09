package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.core.AcrossModuleSettingsRegistry;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;

public class AcrossHibernateJpaModuleSettings extends AcrossHibernateModuleSettings
{
	public static final String PERSISTENCE_UNIT_NAME = "acrossHibernate.jpa.persistenceUnitName";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		super.registerSettings( registry );

		registry.require( PERSISTENCE_UNIT_NAME, String.class,
		                  "Name of the persistence unit that is being managed by this module.  Defaults to the module name." );
	}

	public String getPersistenceUnitName() {
		return getRequiredProperty( PERSISTENCE_UNIT_NAME );
	}
}
