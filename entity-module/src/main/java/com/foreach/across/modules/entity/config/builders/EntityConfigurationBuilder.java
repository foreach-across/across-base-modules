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
import com.foreach.across.modules.entity.config.builders.configuration.FormViewBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.ListViewBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.ViewBuilder;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class EntityConfigurationBuilder<T> extends AbstractAttributesAndViewsBuilder<EntityConfigurationBuilder, MutableEntityConfiguration<T>>
{
	@SuppressWarnings("unchecked")
	public class PropertyRegistryBuilder
			extends AbstractEntityPropertyRegistryBuilder<PropertyRegistryBuilder>
	{
		public class PropertyDescriptorBuilder extends AbstractEntityPropertyDescriptorBuilder<PropertyDescriptorBuilder>
		{
			@Override
			public PropertyRegistryBuilder and() {
				return propertyRegistryBuilder;
			}
		}

		private final PropertyRegistryBuilder propertyRegistryBuilder;

		public PropertyRegistryBuilder() {
			this.propertyRegistryBuilder = this;
		}

		@Override
		public synchronized PropertyDescriptorBuilder property( String name ) {
			return (PropertyDescriptorBuilder) super.property( name );
		}

		@Override
		protected PropertyDescriptorBuilder createDescriptorBuilder( String name ) {
			return new PropertyDescriptorBuilder();
		}

		@Override
		public EntityConfigurationBuilder and() {
			return configurationBuilder;
		}
	}

	private final EntityConfigurationBuilder configurationBuilder;
	private final Class<T> entityType;
	private final EntitiesConfigurationBuilder parent;

	private PropertyRegistryBuilder propertyRegistryBuilder;
	private final Map<String, EntityAssociationBuilder> associations = new HashMap<>();

	EntityConfigurationBuilder( Class<T> entityType, EntitiesConfigurationBuilder parent ) {
		this.entityType = entityType;
		this.parent = parent;
		this.configurationBuilder = this;
	}

	public PropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = new PropertyRegistryBuilder();
		}

		return propertyRegistryBuilder;
	}

	/**
	 * @return the parent builder
	 */
	public EntitiesConfigurationBuilder and() {
		return parent;
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

	public synchronized EntityAssociationBuilder association( String name ) {
		EntityAssociationBuilder builder = associations.get( name );

		if ( builder == null ) {
			builder = new EntityAssociationBuilder( name );
			associations.put( name, builder );
		}

		return builder;
	}

	void apply( MutableEntityRegistry entityRegistry ) {
		MutableEntityConfiguration configuration = entityRegistry.getMutableEntityConfiguration( entityType );

		if ( configuration == null ) {
			configuration = new EntityConfigurationImpl<>( entityType );
			configuration.setPropertyRegistry( new DefaultEntityPropertyRegistry( entityType ) );
			entityRegistry.register( configuration );
		}

		if ( propertyRegistryBuilder != null ) {
			propertyRegistryBuilder.apply( configuration.getPropertyRegistry() );
		}

		applyAttributes( configuration );
		applyViewBuilders( configuration );

		for ( EntityAssociationBuilder associationBuilder : associations.values() ) {
			associationBuilder.apply( configuration, entityRegistry );
		}
	}

	void postProcess( MutableEntityRegistry entityRegistry ) {
		MutableEntityConfiguration<T> configuration = entityRegistry.getMutableEntityConfiguration( entityType );

		for ( PostProcessor<MutableEntityConfiguration<T>> postProcessor : postProcessors() ) {
			postProcessor.process( configuration );
		}

		for ( EntityAssociationBuilder associationBuilder : associations.values() ) {
			associationBuilder.postProcess( configuration );
		}
	}
}
