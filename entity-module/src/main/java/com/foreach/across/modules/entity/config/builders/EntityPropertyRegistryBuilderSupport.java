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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public abstract class EntityPropertyRegistryBuilderSupport<T>
{
	private final T parent;

	private final Map<String, EntityPropertyDescriptorBuilder<EntityPropertyRegistryBuilderSupport<T>>> builders =
			new HashMap<>();

	public EntityPropertyRegistryBuilderSupport( T parent ) {
		this.parent = parent;
	}

	public synchronized EntityPropertyDescriptorBuilder<EntityPropertyRegistryBuilderSupport<T>> property( String name ) {
		Assert.notNull( name );

		EntityPropertyDescriptorBuilder<EntityPropertyRegistryBuilderSupport<T>> builder = builders.get( name );

		if ( builder == null ) {
			builder = new EntityPropertyDescriptorBuilder<>( this, name );
			builders.put( name, builder );
		}

		return builder;
	}

	public EntityPropertyDescriptorBuilder<EntityPropertyRegistryBuilderSupport<T>> property( String name,
	                                                                                          String displayName ) {
		return property( name ).displayName( displayName );
	}

	public EntityPropertyDescriptorBuilder<EntityPropertyRegistryBuilderSupport<T>> property( String name,
	                                                                                          String displayName,
	                                                                                          String expression ) {
		Assert.notNull( expression );
		return property( name ).displayName( displayName ).spelValueFetcher( expression );
	}

	public EntityPropertyDescriptorBuilder<EntityPropertyRegistryBuilderSupport<T>> property( String name,
	                                                                                          String displayName,
	                                                                                          ValueFetcher valueFetcher ) {
		Assert.notNull( valueFetcher );
		return property( name ).displayName( displayName ).valueFetcher( valueFetcher );
	}

	/**
	 * @return parent builder
	 */
	public T and() {
		return parent;
	}

	protected void apply( EntityPropertyRegistry entityPropertyRegistry ) {
		for ( EntityPropertyDescriptorBuilder builder : builders.values() ) {
			builder.apply( entityPropertyRegistry );
		}
	}
}
