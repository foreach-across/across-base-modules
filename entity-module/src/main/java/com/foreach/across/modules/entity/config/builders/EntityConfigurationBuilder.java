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
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

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
			public PropertyDescriptorBuilder attribute( String name, Object value ) {
				return super.attribute( name, value );
			}

			@Override
			public <S> PropertyDescriptorBuilder attribute( Class<S> type, S value ) {
				return super.attribute( type, value );
			}

			@Override
			public PropertyDescriptorBuilder displayName( String displayName ) {
				return super.displayName( displayName );
			}

			@Override
			public PropertyDescriptorBuilder spelValueFetcher( String expression ) {
				return super.spelValueFetcher( expression );
			}

			@Override
			public PropertyDescriptorBuilder valueFetcher( ValueFetcher valueFetcher ) {
				return super.valueFetcher( valueFetcher );
			}

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

	private Boolean hidden;

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
	 * Ensures the configuration <strong>will not</strong> be labeled as hidden for UI implementations.
	 *
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> show() {
		return hidden( false );
	}

	/**
	 * Ensures the configuration <strong>will be</strong> labeled as hidden for UI implementations.
	 *
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> hide() {
		return hidden( true );
	}

	/**
	 * Should the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} be hidden from UI
	 * implementations. This property can be considered a hint for automatically generated user interfaces.
	 *
	 * @param hidden True if the configuration should be hidden from UI.
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> hidden( boolean hidden ) {
		this.hidden = hidden;
		return this;
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

	void apply( MutableEntityRegistry entityRegistry, AutowireCapableBeanFactory beanFactory ) {
		MutableEntityConfiguration configuration = entityRegistry.getMutableEntityConfiguration( entityType );

		if ( configuration == null ) {
			configuration = new EntityConfigurationImpl<>( entityType );
			configuration.setPropertyRegistry( new DefaultEntityPropertyRegistry( entityType ) );
			entityRegistry.register( configuration );
		}

		if ( propertyRegistryBuilder != null ) {
			propertyRegistryBuilder.apply( configuration.getPropertyRegistry() );
		}

		if ( hidden != null ) {
			configuration.setHidden( hidden );
		}

		applyAttributes( configuration );
		applyViewBuilders( configuration, beanFactory );

		for ( EntityAssociationBuilder associationBuilder : associations.values() ) {
			associationBuilder.apply( configuration, entityRegistry, beanFactory );
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
