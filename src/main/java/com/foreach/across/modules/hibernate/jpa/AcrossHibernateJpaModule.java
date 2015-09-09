package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.modules.hibernate.AbstractHibernatePackageModule;

import javax.sql.DataSource;

/**
 * Enables JPA support using Hibernate as the implementation.
 * <p/>
 * Note that this module essentially supports the same properties as the
 * {@link com.foreach.across.modules.hibernate.AcrossHibernateModule}.  In an application where both
 * modules are configured, care should be taken that properties are set on the module directly.
 */
@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(optional = "EhcacheModule")
public class AcrossHibernateJpaModule extends AbstractHibernatePackageModule
{
	public static final String NAME = "AcrossHibernateJpaModule";

	public AcrossHibernateJpaModule() {
		setPersistenceUnitName( getName() );
	}

	public AcrossHibernateJpaModule( DataSource dataSource ) {
		this();
		setDataSource( dataSource );
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Enables JPA support on the Across context using Hibernate as the vendor implementation.";
	}

	protected void setPersistenceUnitName( String persistenceUnitName ) {
		setProperty( AcrossHibernateJpaModuleSettings.PERSISTENCE_UNIT_NAME, persistenceUnitName );
	}
}
