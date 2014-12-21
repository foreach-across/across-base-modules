package com.foreach.across.modules.hibernate.provider;

/**
 * Registry interface to extend a HibernatePackage.
 */
public interface HibernatePackageRegistry
{
	/**
	 * @return Name of this hibernate package.
	 */
	String getName();

	void addPackageToScan( Class... classes );

	void addPackageToScan( String... packageToScan );

	void addMappingResource( String... mappingResource );

	void add( HibernatePackageProvider provider );
}
