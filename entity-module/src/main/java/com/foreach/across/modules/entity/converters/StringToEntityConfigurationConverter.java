package com.foreach.across.modules.entity.converters;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a string to an EntityConfiguration by interpreting the string as the name of
 * the EntityConfiguration.
 */
public class StringToEntityConfigurationConverter implements Converter<String, EntityConfiguration>
{
	private final EntityRegistry entityRegistry;

	public StringToEntityConfigurationConverter( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}

	@Override
	public EntityConfiguration convert( String entityName ) {
		return entityRegistry.getEntityConfiguration( entityName );
	}
}
