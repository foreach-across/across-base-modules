package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.support.ReadableAttributes;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;

import java.io.Serializable;
import java.util.Collection;

public interface EntityConfiguration<T> extends ReadableAttributes, EntityViewRegistry
{
	String getName();

	String getDisplayName();

	Class<T> getEntityType();

	EntityModel<T, ? extends Serializable> getEntityModel();

	EntityPropertyRegistry getPropertyRegistry();

	EntityMessageCodeResolver getEntityMessageCodeResolver();

	Collection<EntityAssociation> getAssociations();

	EntityAssociation association( String name );

	boolean isNew( T entity );

	Class<?> getIdType();

	Serializable getId( T entity );

	String getLabel( T entity );
}
