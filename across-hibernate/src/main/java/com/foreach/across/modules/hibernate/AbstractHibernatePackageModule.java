package com.foreach.across.modules.hibernate;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.SingletonBeanConfigurer;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfiguringModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageProvider;

import javax.sql.DataSource;
import java.util.*;

public abstract class AbstractHibernatePackageModule extends AcrossModule
{
	private DataSource dataSource;
	private boolean scanForHibernatePackages = true;
	private Set<HibernatePackageProvider> hibernatePackageProviders = new HashSet<>();

	/**
	 * Get the datasource associated with this module.  Will return the context datasource if none
	 * has been set explicitly.
	 *
	 * @return Datasource associated with this module.
	 */
	public DataSource getDataSource() {
		return dataSource != null ? dataSource : getContext().getDataSource();
	}

	/**
	 * Set the datasource that all entities managed by this module should use.
	 *
	 * @param dataSource Datasource associated with this module.
	 */
	public void setDataSource( DataSource dataSource ) {
		this.dataSource = dataSource;
	}

	/**
	 * Returns the set of HibernatePackageProvider instances configured directly on this module.
	 *
	 * @return Set of configured HibernatePackageProviders.
	 */
	public Set<HibernatePackageProvider> getHibernatePackageProviders() {
		return hibernatePackageProviders;
	}

	public void setHibernatePackageProviders( Set<HibernatePackageProvider> hibernatePackageProviders ) {
		this.hibernatePackageProviders = hibernatePackageProviders;
	}

	public void addHibernatePackageProvider( HibernatePackageProvider... hibernatePackageProvider ) {
		this.hibernatePackageProviders.addAll( Arrays.asList( hibernatePackageProvider ) );
	}

	/**
	 * If true this module will scan other modules to see if they implement the HibernatePackageConfiguringModule
	 * interface.
	 *
	 * @return True if modules will be scanned and activated automatically.
	 * @see com.foreach.across.modules.hibernate.provider.HibernatePackageConfiguringModule
	 */
	public boolean isScanForHibernatePackages() {
		return scanForHibernatePackages;
	}

	public void setScanForHibernatePackages( boolean scanForHibernatePackages ) {
		this.scanForHibernatePackages = scanForHibernatePackages;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getHibernateProperties() {
		Map<String, Object> props = (Map<String, Object>) getProperties().get(
				AcrossHibernateModuleSettings.HIBERNATE_PROPERTIES );

		if ( props == null ) {
			props = new HashMap<>();
			setProperty( AcrossHibernateModuleSettings.HIBERNATE_PROPERTIES, props );
		}

		return props;
	}

	public void setHibernateProperties( Map<String, Object> hibernateProperties ) {
		Map<String, Object> current = getHibernateProperties();

		current.clear();
		current.putAll( hibernateProperties );
	}

	public void setHibernateProperty( String name, String value ) {
		getHibernateProperties().put( name, value );
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		HibernatePackage hibernatePackage = new HibernatePackage( getName() );

		for ( HibernatePackageProvider provider : getHibernatePackageProviders() ) {
			hibernatePackage.add( provider );
		}

		if ( isScanForHibernatePackages() ) {
			for ( ModuleBootstrapConfig config : contextConfig.getModules() ) {
				AcrossModule module = config.getModule();

				if ( module instanceof HibernatePackageConfiguringModule ) {
					( (HibernatePackageConfiguringModule) module ).configureHibernatePackage( hibernatePackage );
				}
			}
		}

		currentModule.addApplicationContextConfigurer(
				new SingletonBeanConfigurer( "hibernatePackage", hibernatePackage, true )
		);

	}
}
