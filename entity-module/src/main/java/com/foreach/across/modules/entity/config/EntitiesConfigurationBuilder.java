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
package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class EntitiesConfigurationBuilder
{
	private final Map<Class<?>, EntityConfigurationBuilder> builders = new HashMap<>();

	private final Map<Object, Object> attributes = new HashMap<>();

	private final Collection<PostProcessor<MutableEntityConfiguration>> postProcessors = new LinkedList<>();

	public synchronized EntityConfigurationBuilder entity( Class<?> entityType ) {
		Assert.notNull( entityType );

		EntityConfigurationBuilder builder = builders.get( entityType );

		if ( builder == null ) {
			builder = new EntityConfigurationBuilder( entityType, this );
			builders.put( entityType, builder );
		}

		return builder;
	}

	public EntitiesConfigurationBuilder attribute( String name, Object value ) {
		Assert.notNull( name );
		attributes.put( name, value );
		return this;
	}

	public <T> EntitiesConfigurationBuilder attribute( Class<T> type, T value ) {
		Assert.notNull( type );
		attributes.put( type, value );
		return this;
	}

	public void addPostProcessor( PostProcessor<MutableEntityConfiguration> postProcessor ) {
		postProcessors.add( postProcessor );
	}

	/**
	 * Apply the configuration to the EntityRegistry.
	 *
	 * @param entityRegistry EntityRegistry to which the configuration should be applied.
	 */
	public synchronized void apply( MutableEntityRegistry entityRegistry ) {
		for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
			MutableEntityConfiguration mutableEntityConfiguration
					= entityRegistry.getMutableEntityConfiguration( entityConfiguration.getEntityType() );

			for ( Map.Entry<Object, Object> attribute : attributes.entrySet() ) {
				if ( attribute.getKey() instanceof String ) {
					mutableEntityConfiguration.addAttribute( (String) attribute.getKey(), attribute.getValue() );
				}
				else {
					mutableEntityConfiguration.addAttribute( (Class) attribute.getKey(), attribute.getValue() );
				}
			}
		}

		// Apply the individual builder
		for ( EntityConfigurationBuilder builder : builders.values() ) {
			builder.apply( entityRegistry );
		}

		// Apply post processors
		// todo: if different instance is returned, update registry
		for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
			MutableEntityConfiguration mutableEntityConfiguration
					= entityRegistry.getMutableEntityConfiguration( entityConfiguration.getEntityType() );

			for ( PostProcessor<MutableEntityConfiguration> postProcessor : postProcessors ) {
				postProcessor.process( mutableEntityConfiguration );
			}
		}
	}

	public static class EntityConfigurationBuilder
	{
		public static class EntityPropertyRegistryBuilder
				extends EntityPropertyRegistryBuilderSupport<EntityConfigurationBuilder>
		{
			EntityPropertyRegistryBuilder( EntityConfigurationBuilder parent ) {
				super( parent );
			}
		}

		private final Class<?> entityType;
		private final EntitiesConfigurationBuilder parent;

		private final Map<String, EntityViewBuilder> viewBuilders = new HashMap<>();

		private EntityPropertyRegistryBuilder propertyRegistryBuilder;

		protected EntityConfigurationBuilder( Class<?> entityType, EntitiesConfigurationBuilder parent ) {
			this.entityType = entityType;
			this.parent = parent;
		}

		public EntityPropertyRegistryBuilder properties() {
			if ( propertyRegistryBuilder == null ) {
				propertyRegistryBuilder = new EntityPropertyRegistryBuilder( this );
			}

			return propertyRegistryBuilder;
		}

		public synchronized CommonEntityViewBuilder view( String name ) {
			return view( name, CommonEntityViewBuilder.class );
		}

		@SuppressWarnings("unchecked")
		public synchronized <T extends EntityViewBuilder> T view( String name, Class<T> builderClass ) {
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

		public EntitiesConfigurationBuilder and() {
			return parent;
		}

		private void apply( MutableEntityRegistry entityRegistry ) {
			MutableEntityConfiguration configuration = entityRegistry.getMutableEntityConfiguration( entityType );

			if ( configuration == null ) {
				configuration = new EntityConfigurationImpl<>( entityType );
				configuration.setPropertyRegistry( new DefaultEntityPropertyRegistry( entityType ) );
				entityRegistry.register( configuration );
			}

			for ( EntityViewBuilder viewBuilder : viewBuilders.values() ) {
				viewBuilder.apply( configuration );
			}

			if ( propertyRegistryBuilder != null ) {
				propertyRegistryBuilder.apply( configuration.getPropertyRegistry() );
			}
		}
	}

	public static abstract class EntityPropertyRegistryBuilderSupport<T>
	{
		private final T parent;
		private final Map<String, Descriptor> descriptors = new HashMap<>();

		public EntityPropertyRegistryBuilderSupport( T parent ) {
			this.parent = parent;
		}

		public EntityPropertyRegistryBuilderSupport<T> property( String name, String displayName ) {
			return property( name, displayName, (ValueFetcher) null );
		}

		public EntityPropertyRegistryBuilderSupport<T> property( String name, String displayName, String expression ) {
			Assert.notNull( expression );

			return property( name, displayName, new SpelValueFetcher( expression ) );
		}

		public EntityPropertyRegistryBuilderSupport<T> property( String name,
		                                                         String displayName,
		                                                         ValueFetcher valueFetcher ) {
			Assert.notNull( name );

			Descriptor descriptor = new Descriptor();
			descriptor.name = name;
			if ( displayName != null ) {
				descriptor.displayName = displayName;
			}
			if ( valueFetcher != null ) {
				descriptor.valueFetcher = valueFetcher;
			}

			descriptors.put( name, descriptor );

			return this;
		}

		public T and() {
			return parent;
		}

		protected void apply( EntityPropertyRegistry entityPropertyRegistry ) {
			for ( Descriptor configured : descriptors.values() ) {
				EntityPropertyDescriptor descriptor = entityPropertyRegistry.getProperty( configured.name );
				MutableEntityPropertyDescriptor merged = (MutableEntityPropertyDescriptor) configured.merge( descriptor );

				entityPropertyRegistry.register( merged );
			}
		}

		private static class Descriptor
		{
			String name, displayName;
			ValueFetcher valueFetcher;

			EntityPropertyDescriptor merge( EntityPropertyDescriptor existing ) {
				SimpleEntityPropertyDescriptor descriptor;

				// Modify the existing if possible, or create a new one
				if ( existing instanceof SimpleEntityPropertyDescriptor ) {
					descriptor = (SimpleEntityPropertyDescriptor) existing;
				}
				else {
					descriptor = new SimpleEntityPropertyDescriptor();
					descriptor.setName( name );
				}

				// Update configured properties
				if ( displayName != null ) {
					descriptor.setDisplayName( displayName );
				}

				if ( valueFetcher != null ) {
					descriptor.setValueFetcher( valueFetcher );
					descriptor.setReadable( true );
				}
				else if ( existing == null ) {
					descriptor.setValueFetcher( new SpelValueFetcher( name ) );
					descriptor.setReadable( true );
				}

				// There was an existing descriptor, but not mutable, we created a custom merge
				if ( existing != null && !( existing instanceof SimpleEntityPropertyDescriptor ) ) {
					return existing.merge( descriptor );
				}

				return descriptor;
			}
		}
	}

	public static abstract class EntityViewBuilder<T extends EntityViewFactory>
	{
		private String name;
		private EntityConfigurationBuilder parent;

		private T factory;

		protected void setName( String name ) {
			this.name = name;
		}

		protected void setParent( EntityConfigurationBuilder parent ) {
			this.parent = parent;
		}

		public EntityViewBuilder factory( T entityViewFactory ) {
			this.factory = entityViewFactory;
			return this;
		}

		public EntityConfigurationBuilder and() {
			return parent;
		}

		@SuppressWarnings("unchecked")
		protected void apply( MutableEntityConfiguration configuration ) {
			T configuredFactory = (T) configuration.getViewFactory( name );

			if ( configuredFactory == null ) {
				configuredFactory = factory != null ? factory : createFactoryInstance();
				configuration.registerView( name, configuredFactory );
			}

			applyToFactory( configuration, configuredFactory );
		}

		protected abstract T createFactoryInstance();

		protected abstract void applyToFactory( EntityConfiguration configuration, T factory );
	}

	public static class CommonEntityViewBuilder extends EntityViewBuilder<ConfigurablePropertiesEntityViewFactorySupport>
	{
		public static class EntityViewPropertyRegistryBuilder
				extends EntityPropertyRegistryBuilderSupport<CommonEntityViewBuilder>
		{
			EntityViewPropertyRegistryBuilder( CommonEntityViewBuilder parent ) {
				super( parent );
			}

			public EntityViewPropertyRegistryBuilder filter( String... propertyNames ) {
				and().viewPropertyFilter = EntityPropertyFilters.includeOrdered( propertyNames );
				return this;
			}
		}

		private String template;
		private EntityViewPropertyRegistryBuilder propertyRegistryBuilder;
		private EntityPropertyFilter viewPropertyFilter;

		public CommonEntityViewBuilder template( String template ) {
			this.template = template;
			return this;
		}

		public EntityViewPropertyRegistryBuilder properties() {
			if ( propertyRegistryBuilder == null ) {
				propertyRegistryBuilder = new EntityViewPropertyRegistryBuilder( this );
			}

			return propertyRegistryBuilder;
		}

		public EntityViewPropertyRegistryBuilder properties( String... propertyNames ) {
			return properties().filter( propertyNames );
		}

		@Override
		protected ConfigurablePropertiesEntityViewFactorySupport createFactoryInstance() {
			return new ConfigurablePropertiesEntityViewFactorySupport()
			{
				@Override
				protected void extendViewModel( EntityConfiguration entityConfiguration, EntityView view ) {
				}

				@Override
				protected EntityView createEntityView() {
					return new EntityView();
				}
			};
		}

		@Override
		protected void applyToFactory( EntityConfiguration configuration,
		                               ConfigurablePropertiesEntityViewFactorySupport factory ) {
			if ( template != null ) {
				factory.setTemplate( template );
			}

			EntityPropertyRegistry registry = factory.getPropertyRegistry();

			if ( registry == null ) {
				registry = new MergingEntityPropertyRegistry( configuration.getPropertyRegistry() );
				factory.setPropertyRegistry( registry );
			}

			if ( propertyRegistryBuilder != null ) {
				propertyRegistryBuilder.apply( registry );
			}

			if ( viewPropertyFilter != null ) {
				factory.setPropertyFilter( viewPropertyFilter );
			}
		}
	}
}
