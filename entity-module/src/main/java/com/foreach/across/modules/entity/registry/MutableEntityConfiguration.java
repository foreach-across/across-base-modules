package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.support.WritableAttributes;
import com.foreach.across.modules.entity.views.EntityViewFactory;

import java.io.Serializable;

/**
 * Manageable version of a {@link com.foreach.across.modules.entity.registry.EntityConfiguration},
 * implementations must allow the configuration to be modified through this interface, without
 * changing the core properties like {@link #getEntityType()} and {@link #getName()}.
 */
public interface MutableEntityConfiguration<T> extends EntityConfiguration<T>, WritableAttributes
{
	void setDisplayName( String displayName );

	void setEntityModel( EntityModel<T, ? extends Serializable> entityModel );

	void setPropertyRegistry( EntityPropertyRegistry propertyRegistry );

	void registerView( String viewName, EntityViewFactory viewFactory );
}
