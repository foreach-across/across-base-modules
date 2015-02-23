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

import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.config.builders.registry.FormViewBuilder;
import com.foreach.across.modules.entity.config.builders.registry.ListViewBuilder;
import com.foreach.across.modules.entity.config.builders.registry.ViewBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Central builder for customizing configuration entries in the
 * {@link com.foreach.across.modules.entity.registry.EntityRegistry}.
 *
 * @author Arne Vandamme
 */
public class EntitiesConfigurationBuilder extends AbstractAttributesAndViewsBuilder<EntitiesConfigurationBuilder, MutableEntityConfiguration<?>>
{
	private final EntitiesConfigurationBuilder self;
	private final Map<Class<?>, EntityConfigurationBuilder> builders = new HashMap<>();

	public EntitiesConfigurationBuilder() {
		this.self = this;
	}

	@Override
	public ViewBuilder view( String name ) {
		return view( name, ViewBuilder.class );
	}

	@Override
	public ListViewBuilder listView() {
		return listView( EntityListView.VIEW_NAME );
	}

	@Override
	public ListViewBuilder listView( String name ) {
		return view( name, ListViewBuilder.class );
	}

	@Override
	public FormViewBuilder createFormView() {
		return formView( EntityFormView.CREATE_VIEW_NAME );
	}

	@Override
	public FormViewBuilder updateFormView() {
		return formView( EntityFormView.UPDATE_VIEW_NAME );
	}

	@Override
	public FormViewBuilder formView( String name ) {
		return view( name, FormViewBuilder.class );
	}

	/**
	 * Retrieve a builder for a specific entity type.
	 *
	 * @param entityType for which to retrieve the builder.
	 * @return entity builder instance
	 */
	@SuppressWarnings("unchecked")
	public synchronized <V> EntityConfigurationBuilder<V> entity( Class<V> entityType ) {
		Assert.notNull( entityType );

		EntityConfigurationBuilder<V> builder = (EntityConfigurationBuilder<V>) builders.get( entityType );

		if ( builder == null ) {
			builder = new EntityConfigurationBuilder<>( entityType, this );
			builders.put( entityType, builder );
		}

		return builder;
	}

	/**
	 * Apply the builder configuration to the EntityRegistry.  This will not invoke the post processors,
	 * use {@link #postProcess(com.foreach.across.modules.entity.registry.MutableEntityRegistry)} to execute
	 * the post processors after the configuration has been applied.
	 *
	 * @param entityRegistry EntityRegistry to which the configuration should be applied.
	 */
	public synchronized void apply( MutableEntityRegistry entityRegistry ) {
		for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
			MutableEntityConfiguration mutableEntityConfiguration
					= entityRegistry.getMutableEntityConfiguration( entityConfiguration.getEntityType() );

			applyAttributes( mutableEntityConfiguration );
			applyViewBuilders( mutableEntityConfiguration );
		}

		// Apply the individual builder
		for ( EntityConfigurationBuilder builder : builders.values() ) {
			builder.apply( entityRegistry );
		}
	}

	/**
	 * Apply the post processors to the registry.  Post processors are applied one after the other to the entire
	 * registry (instead of one after the other per entity configuration).  First the global post processors
	 * are applied, followed by the individual
	 * {@link com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder} post processors.
	 * <p/>
	 * If a PostProcessor returns a different instance than the one passed in as parameter, the new instance
	 * will replace the new one in the registry even if it would defer in entity type.  If the post processor
	 * returns null, the entity configuration will be removed.
	 *
	 * @param entityRegistry EntityRegistry to which the post processors should be applied.
	 */
	public synchronized void postProcess( MutableEntityRegistry entityRegistry ) {
		for ( PostProcessor<MutableEntityConfiguration<?>> postProcessor : postProcessors() ) {
			for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
				MutableEntityConfiguration mutableEntityConfiguration
						= entityRegistry.getMutableEntityConfiguration( entityConfiguration.getEntityType() );

				postProcessor.process( mutableEntityConfiguration );
			}
		}

		for ( EntityConfigurationBuilder builder : builders.values() ) {
			builder.postProcess( entityRegistry );
		}
	}
}
