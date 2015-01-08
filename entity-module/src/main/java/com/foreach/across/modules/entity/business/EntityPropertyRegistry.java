package com.foreach.across.modules.entity.business;

import java.util.Comparator;
import java.util.List;

/**
 * Registry containing the property information for a particular entity type.
 */
public interface EntityPropertyRegistry
{
	/**
	 * @return Number of properties in the registry.
	 */
	int size();

	/**
	 * @param propertyName Name of the property.
	 * @return True if a property with that name is registered.
	 */
	boolean contains( String propertyName );

	List<EntityPropertyDescriptor> getProperties();

	List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter );

	List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter,
	                                              Comparator<EntityPropertyDescriptor> comparator );

	void setDefaultOrder( String... propertyNames );

	void setDefaultFilter( EntityPropertyFilter filter );
}
