/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class EntityConfigurationBuilder extends EntityBuilderSupport<EntityConfigurationBuilder>
{
	public class EntityPropertyRegistryBuilder
			extends EntityPropertyRegistryBuilderSupport<EntityConfigurationBuilder, EntityPropertyRegistryBuilder>
	{
		EntityPropertyRegistryBuilder( EntityConfigurationBuilder parent ) {
			super( parent );
		}
	}

	private final Class<?> entityType;
	private final EntitiesConfigurationBuilder parent;

	private final Map<String, EntityViewBuilder> viewBuilders = new HashMap<>();

	private EntityPropertyRegistryBuilder propertyRegistryBuilder;

	EntityConfigurationBuilder( Class<?> entityType, EntitiesConfigurationBuilder parent ) {
		this.entityType = entityType;
		this.parent = parent;
	}

	@Override
	protected Collection<EntityConfiguration> entitiesToConfigure( MutableEntityRegistry entityRegistry ) {
		return Collections.<EntityConfiguration>singleton( entityRegistry.getEntityConfiguration( entityType ) );
	}

	public EntityPropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = new EntityPropertyRegistryBuilder( this );
		}

		return propertyRegistryBuilder;
	}

	/**
	 * Returns a {@link com.foreach.across.modules.entity.config.builders.SimpleEntityViewBuilder.StandardEntityViewBuilder}
	 * instance for the view with the given name.  If there is already another builder type for that view, an exception
	 * will be thrown.
	 *
	 * @param name Name of the view for which to retrieve a builder.
	 * @return builder instance
	 */
	public SimpleEntityViewBuilder.StandardEntityViewBuilder view( String name ) {
		return view( name, SimpleEntityViewBuilder.StandardEntityViewBuilder.class );
	}

	/**
	 * Returns the default list view builder for the entity being configured.
	 * A default list view is usually available.
	 *
	 * @return builder instance
	 */
	public EntityListViewBuilder listView() {
		return listView( EntityListView.VIEW_NAME );
	}

	/**
	 * Returns a list view builder for the view with the given name.
	 *
	 * @param name Name of the view for which to retrieve a builder.
	 * @return builder instance
	 */
	public EntityListViewBuilder listView( String name ) {
		return view( name, EntityListViewBuilder.class );
	}

	/**
	 * Returns the default create form view builder for the entity being configured.
	 * A default create form view is usually available.
	 *
	 * @return builder instance
	 */
	public EntityFormViewBuilder createFormView() {
		return formView( EntityFormView.CREATE_VIEW_NAME );
	}

	/**
	 * Returns the default update form view builder for the entity being configured.
	 * A default update form view is usually available.
	 *
	 * @return builder instance
	 */
	public EntityFormViewBuilder updateFormView() {
		return formView( EntityFormView.UPDATE_VIEW_NAME );
	}

	/**
	 * Returns a form view builder for the view with the given name.
	 *
	 * @param name Name of the view for which to retrieve a builder.
	 * @return builder instance
	 */
	public EntityFormViewBuilder formView( String name ) {
		return view( name, EntityFormViewBuilder.class );
	}

	/**
	 * Returns a builder for the view with the specified name.  Any existing builder is assumed to be of the given
	 * type and a new instance of that type will be created if there is no builder yet.  Note that custom
	 * builder types *must* have a parameterless constructor.
	 *
	 * @param name         Name of the view for which to retrieve a builder.
	 * @param builderClass Type of the builder.
	 * @param <T>          Specific builder implementation.
	 * @return builder instance
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends EntityViewBuilder<?, T>> T view( String name, Class<T> builderClass ) {
		T builder = (T) viewBuilders.get( name );

		if ( builder == null ) {
			try {
				builder = builderClass.newInstance();
			}
			catch ( InstantiationException | IllegalAccessException e ) {
				throw new RuntimeException( "Could not create instance of " + builderClass, e );
			}
			builder.setName( name );
			builder.setParent( this );

			viewBuilders.put( name, builder );
		}

		return builder;
	}

	/**
	 * @return the parent builder
	 */
	public EntitiesConfigurationBuilder and() {
		return parent;
	}

	@Override
	void apply( MutableEntityRegistry entityRegistry ) {
		super.apply( entityRegistry );

		MutableEntityConfiguration configuration = entityRegistry.getMutableEntityConfiguration( entityType );

		if ( configuration == null ) {
			configuration = new EntityConfigurationImpl<>( entityType );
			configuration.setPropertyRegistry( new DefaultEntityPropertyRegistry( entityType ) );
			entityRegistry.register( configuration );
		}

		if ( propertyRegistryBuilder != null ) {
			propertyRegistryBuilder.apply( configuration.getPropertyRegistry() );
		}

		for ( EntityViewBuilder viewBuilder : viewBuilders.values() ) {
			viewBuilder.apply( configuration );
		}
	}
}
