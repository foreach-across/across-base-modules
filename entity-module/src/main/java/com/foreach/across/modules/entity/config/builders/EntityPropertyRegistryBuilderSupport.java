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
public abstract class EntityPropertyRegistryBuilderSupport<T>
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
			MutableEntityPropertyDescriptor merged = (MutableEntityPropertyDescriptor) configured.merge(
					descriptor );

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
