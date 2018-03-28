package com.foreach.across.modules.hibernate;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageProvider;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.*;

public abstract class AbstractHibernatePackageModule extends AcrossModule
{
	private DataSource dataSource;

	/**
	 * The initial name of the datasource bean that the entity manager should use.
	 * Can be overridden using properties and any explicitly attached instance
	 * ({@link #setDataSource(DataSource)}) will always take precedence.
	 */
	@Getter
	@Setter
	private String dataSourceName = AcrossContext.DATASOURCE;

	private boolean scanForHibernatePackages = true;
	private Set<HibernatePackageProvider> hibernatePackageProviders = new HashSet<>();
	private String propertiesPrefix;

	/**
	 * @return prefix for the properties that should be considered when configuring this module
	 */
	public String getPropertiesPrefix() {
		return propertiesPrefix != null ? propertiesPrefix : StringUtils.uncapitalize( getName() );
	}

	/**
	 * Set a custom properties prefix that should be used for this module.
	 * Defaults to the uncapitalized module name when unspecified.
	 * <p/>
	 * Should be set during constructor of a custom module.
	 *
	 * @param propertiesPrefix to use
	 */
	@SuppressWarnings("all")
	protected final void setPropertiesPrefix( String propertiesPrefix ) {
		String previousPrefix = getPropertiesPrefix();
		this.propertiesPrefix = propertiesPrefix;

		Set<String> propertiesToReplace = new HashSet<>();
		Properties properties = getProperties();
		properties.forEach( ( key, value ) -> {
			if ( key instanceof String ) {
				if ( ( (String) key ).startsWith( previousPrefix + "." ) ) {
					propertiesToReplace.add( (String) key );
				}
			}
		} );

		propertiesToReplace.forEach( propertyName -> {
			String newPropertyName = StringUtils.replaceOnce( propertyName, previousPrefix + ".", propertiesPrefix + "." );
			properties.put( newPropertyName, properties.remove( propertyName ) );
		} );
	}

	/**
	 * Get the datasource associated directly with this module.
	 *
	 * @return Datasource associated with this module.
	 */
	public DataSource getDataSource() {
		return dataSource;
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
	 * @see com.foreach.across.modules.hibernate.provider.HibernatePackageConfigurer
	 */
	public boolean isScanForHibernatePackages() {
		return scanForHibernatePackages;
	}

	public void setScanForHibernatePackages( boolean scanForHibernatePackages ) {
		this.scanForHibernatePackages = scanForHibernatePackages;
	}

	/**
	 * Create the specific settings implementation instance for property binding.
	 * Override this method if a module wants to use an extended settings class.
	 *
	 * @return a new instance for property binding
	 */
	public AcrossHibernateModuleSettings createSettings() {
		return new AcrossHibernateModuleSettings();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getHibernateProperties() {
		Map<String, String> props = (Map<String, String>) getProperties().get(
				getPropertiesPrefix() + ".hibernateProperties" );

		if ( props == null ) {
			props = new HashMap<>();
			setProperty( getPropertiesPrefix() + ".hibernateProperties", props );
		}

		return props;
	}

	public void setHibernateProperties( Map<String, String> hibernateProperties ) {
		Map<String, String> current = getHibernateProperties();

		current.clear();
		current.putAll( hibernateProperties );
	}

	public void setHibernateProperty( String name, String value ) {
		getHibernateProperties().put( name, value );
	}
}
