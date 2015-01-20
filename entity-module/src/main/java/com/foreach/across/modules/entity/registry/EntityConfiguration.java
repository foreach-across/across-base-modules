package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.support.ReadableAttributes;
import com.foreach.across.modules.entity.views.EntityViewFactory;

import java.io.Serializable;

public interface EntityConfiguration<T> extends ReadableAttributes
{
	String getName();

	String getDisplayName();

	Class<T> getEntityType();

	EntityModel<T, ? extends Serializable> getEntityModel();

	boolean hasView( String viewName );

	<Y extends EntityViewFactory> Y getViewFactory( String viewName );

	EntityPropertyRegistry getPropertyRegistry();
}
