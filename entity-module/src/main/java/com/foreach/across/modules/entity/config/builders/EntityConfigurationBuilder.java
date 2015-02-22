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
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.processors.ViewDataBinderProcessor;
import com.foreach.across.modules.entity.views.processors.ViewModelAndCommandProcessor;
import com.foreach.across.modules.entity.views.processors.ViewPostProcessor;
import com.foreach.across.modules.entity.views.processors.ViewPreProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class EntityConfigurationBuilder<T> extends EntityBuilderSupport<EntityConfigurationBuilder, MutableEntityConfiguration<T>>
{
	public class ViewBuilder extends SimpleEntityViewBuilder<ConfigurablePropertiesEntityViewFactorySupport, ViewBuilder>
	{
		public class Properties extends EntityViewPropertyRegistryBuilder<Properties>
		{
			@Override
			public ViewBuilder and() {
				return viewBuilder;
			}
		}

		private final ViewBuilder viewBuilder;

		public ViewBuilder() {
			this.viewBuilder = this;
		}

		@Override
		public Properties properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		public Properties properties() {
			return (Properties) super.properties();
		}

		@Override
		protected Properties createPropertiesBuilder() {
			return new Properties();
		}

		@Override
		public ViewBuilder template( String template ) {
			return super.template( template );
		}

		@Override
		public ViewBuilder addProcessor( Object processor ) {
			return super.addProcessor( processor );
		}

		@Override
		public ViewBuilder addPreProcessor( ViewPreProcessor preProcessor ) {
			return super.addPreProcessor( preProcessor );
		}

		@Override
		public ViewBuilder addPostProcessor( ViewPostProcessor postProcessor ) {
			return super.addPostProcessor( postProcessor );
		}

		@Override
		public ViewBuilder addModelAndCommandProcessor( ViewModelAndCommandProcessor modelAndCommandProcessor ) {
			return super.addModelAndCommandProcessor( modelAndCommandProcessor );
		}

		@Override
		public ViewBuilder addDataBinderProcessor( ViewDataBinderProcessor dataBinderProcessor ) {
			return super.addDataBinderProcessor( dataBinderProcessor );
		}

		@Override
		public ViewBuilder factory( ConfigurablePropertiesEntityViewFactorySupport entityViewFactory ) {
			return super.factory( entityViewFactory );
		}

		@Override
		public EntityConfigurationBuilder and() {
			return self;
		}
	}

	public class ListViewBuilder extends EntityListViewBuilder<ListViewBuilder>
	{
		public class Properties extends EntityViewPropertyRegistryBuilder<Properties>
		{
			@Override
			public ListViewBuilder and() {
				return viewBuilder;
			}
		}

		private final ListViewBuilder viewBuilder;

		public ListViewBuilder() {
			this.viewBuilder = this;
		}

		@Override
		public Properties properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		public Properties properties() {
			return (Properties) super.properties();
		}

		@Override
		protected Properties createPropertiesBuilder() {
			return new Properties();
		}

		@Override
		public EntityConfigurationBuilder and() {
			return self;
		}
	}

	public class FormViewBuilder extends EntityFormViewBuilder<FormViewBuilder>
	{
		public class Properties extends EntityViewPropertyRegistryBuilder<Properties>
		{
			@Override
			public FormViewBuilder and() {
				return viewBuilder;
			}
		}

		private final FormViewBuilder viewBuilder;

		public FormViewBuilder() {
			this.viewBuilder = this;
		}

		@Override
		public Properties properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		public Properties properties() {
			return (Properties) super.properties();
		}

		@Override
		protected Properties createPropertiesBuilder() {
			return new Properties();
		}

		@Override
		public EntityConfigurationBuilder and() {
			return self;
		}
	}

	public class EntityPropertyRegistryBuilder
			extends EntityPropertyRegistryBuilderSupport<EntityPropertyRegistryBuilder>
	{
		@Override
		public EntityConfigurationBuilder and() {
			return self;
		}
	}

	private final EntityConfigurationBuilder self;
	private final Class<T> entityType;
	private final EntitiesConfigurationBuilder parent;

	private EntityPropertyRegistryBuilder propertyRegistryBuilder;
	private final Map<String, EntityAssociationBuilder> associations = new HashMap<>();

	EntityConfigurationBuilder( Class<T> entityType, EntitiesConfigurationBuilder parent ) {
		this.entityType = entityType;
		this.parent = parent;
		this.self = this;
	}

	public EntityPropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = new EntityPropertyRegistryBuilder();
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
