package com.foreach.across.modules.entity.registry.properties;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Registry containing the property information for a particular entity type.
 */
public interface EntityPropertyRegistry
{
	/**
	 * @param propertyName Name of the property.
	 * @return True if a property with that name is registered.
	 */
	boolean contains( String propertyName );

	// todo: move to mutable
	void register( MutableEntityPropertyDescriptor descriptor );

	EntityPropertyDescriptor getProperty( String propertyName );

	List<EntityPropertyDescriptor> getProperties();

	List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter );

	List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter,
	                                              Comparator<EntityPropertyDescriptor> comparator );

	void setDefaultOrder( String... propertyNames );

	void setDefaultOrder( Comparator<EntityPropertyDescriptor> defaultOrder );

	Collection<EntityPropertyDescriptor> getRegisteredDescriptors();

	Comparator<EntityPropertyDescriptor> getDefaultOrder();

	void setDefaultFilter( EntityPropertyFilter filter );

	EntityPropertyFilter getDefaultFilter();
}
