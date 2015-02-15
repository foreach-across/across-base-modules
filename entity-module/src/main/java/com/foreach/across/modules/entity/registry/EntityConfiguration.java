package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.support.ReadableAttributes;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewFactory;

import java.io.Serializable;
import java.util.Collection;

public interface EntityConfiguration<T> extends ReadableAttributes
{
	String getName();

	String getDisplayName();

	Class<T> getEntityType();

	EntityModel<T, ? extends Serializable> getEntityModel();

	boolean hasView( String viewName );

	<Y extends EntityViewFactory> Y getViewFactory( String viewName );

	EntityPropertyRegistry getPropertyRegistry();

	EntityMessageCodeResolver getEntityMessageCodeResolver();

	Collection<EntityAssociation> getAssociations();

	EntityAssociation association( String name );

	boolean isNew( T entity );

	Class<?> getIdType();

	Serializable getId( T entity );

	String getLabel( T entity );
}
