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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public abstract class EntityPropertyRegistryBuilderSupport<SELF extends EntityPropertyRegistryBuilderSupport>
{
	public class EntityPropertyDescriptorBuilder<T extends EntityPropertyRegistryBuilderSupport>
	{
		private final String name;

		private String displayName;
		private ValueFetcher valueFetcher;

		private final Map<Object, Object> attributes = new HashMap<>();

		EntityPropertyDescriptorBuilder( String name ) {
			this.name = name;
		}

		/**
		 * Add a custom attribute this builder should add to the property descriptor.
		 *
		 * @param name  Name of the attribute.
		 * @param value Value of the attribute.
		 * @return current builder
		 */
		@SuppressWarnings("unchecked")
		public EntityPropertyDescriptorBuilder attribute( String name, Object value ) {
			Assert.notNull( name );
			attributes.put( name, value );
			return this;
		}

		/**
		 * Add a custom attribute this builder should add to the property descriptor.
		 *
		 * @param type  Type of the attribute.
		 * @param value Value of the attribute.
		 * @param <S>   Class that is both key and value type of the attribute
		 * @return current builder
		 */
		@SuppressWarnings("unchecked")
		public <S> EntityPropertyDescriptorBuilder attribute( Class<S> type, S value ) {
			Assert.notNull( type );
			attributes.put( type, value );
			return this;
		}

		/**
		 * @param displayName Display name to be configured on the property.
		 * @return current builder
		 */
		public EntityPropertyDescriptorBuilder displayName( String displayName ) {
			this.displayName = displayName;
			return this;
		}

		/**
		 * @param expression SpEL expression that should be used as value.
		 * @return current builder
		 */
		public EntityPropertyDescriptorBuilder spelValueFetcher( String expression ) {
			Assert.notNull( expression );
			return valueFetcher( new SpelValueFetcher( expression ) );
		}

		/**
		 * @param valueFetcher fetcher to configure on the property
		 * @return current builder
		 */
		public EntityPropertyDescriptorBuilder valueFetcher( ValueFetcher valueFetcher ) {
			this.valueFetcher = valueFetcher;
			return this;
		}

		/**
		 * @return parent builder
		 */
		public SELF and() {
			return propertiesBuilder;
		}

		void apply( EntityPropertyRegistry entityPropertyRegistry ) {
			EntityPropertyDescriptor existing = entityPropertyRegistry.getProperty( name );
			MutableEntityPropertyDescriptor descriptor;

			// Modify the existing if possible, or create a new one
			if ( existing instanceof MutableEntityPropertyDescriptor ) {
				descriptor = (MutableEntityPropertyDescriptor) existing;
			}

			else {
				descriptor = new SimpleEntityPropertyDescriptor( name );
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

			descriptor.addAllAttributes( attributes );

			// There was an existing descriptor, but not mutable, we created a custom merge
			if ( existing != null && !( existing instanceof MutableEntityPropertyDescriptor ) ) {
				descriptor = (MutableEntityPropertyDescriptor) existing.merge( descriptor );
			}

			entityPropertyRegistry.register( descriptor );
		}
	}

	private final SELF propertiesBuilder;
	private final Map<String, EntityPropertyDescriptorBuilder> builders = new HashMap<>();

	@SuppressWarnings( "unchecked" )
	public EntityPropertyRegistryBuilderSupport() {
		this.propertiesBuilder = (SELF) this;
	}

	@SuppressWarnings( "unchecked" )
	public synchronized EntityPropertyDescriptorBuilder<SELF> property( String name ) {
		Assert.notNull( name );

		EntityPropertyDescriptorBuilder builder = builders.get( name );

		if ( builder == null ) {
			builder = new EntityPropertyDescriptorBuilder( name );
			builders.put( name, builder );
		}

		return builder;
	}

	public EntityPropertyDescriptorBuilder<SELF> property( String name, String displayName ) {
		return property( name ).displayName( displayName );
	}

	public EntityPropertyDescriptorBuilder<SELF> property( String name, String displayName, String expression ) {
		Assert.notNull( expression );
		return property( name ).displayName( displayName ).spelValueFetcher( expression );
	}

	public EntityPropertyDescriptorBuilder<SELF> property( String name, String displayName, ValueFetcher valueFetcher ) {
		Assert.notNull( valueFetcher );
		return property( name ).displayName( displayName ).valueFetcher( valueFetcher );
	}

	/**
	 * @return parent builder
	 */
	public abstract Object and();

	protected void apply( EntityPropertyRegistry entityPropertyRegistry ) {
		for ( EntityPropertyDescriptorBuilder builder : builders.values() ) {
			builder.apply( entityPropertyRegistry );
		}
	}
}
