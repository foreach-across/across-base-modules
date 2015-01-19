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
package com.foreach.across.modules.entity.registry.properties;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that allows overriding properties from a parent registry.
 *
 * @author Arne Vandamme
 */
public class MergingEntityPropertyRegistry extends EntityPropertyRegistrySupport
{
	private EntityPropertyRegistry parent;

	public MergingEntityPropertyRegistry( EntityPropertyRegistry parent ) {
		this.parent = parent;
	}

	public void setParent( EntityPropertyRegistry parent ) {
		this.parent = parent;
	}

	@Override
	public EntityPropertyDescriptor getProperty( String propertyName ) {
		EntityPropertyDescriptor parentProperty = parent.getProperty( propertyName );
		EntityPropertyDescriptor localProperty = super.getProperty( propertyName );

		if ( parentProperty != null && localProperty != null ) {
			return parentProperty.merge( localProperty );
		}

		return localProperty != null ? localProperty : parentProperty;
	}

	@Override
	public Collection<EntityPropertyDescriptor> getRegisteredDescriptors() {
		Map<String, EntityPropertyDescriptor> actual = new HashMap<>();

		for ( EntityPropertyDescriptor descriptor: super.getRegisteredDescriptors() ) {
			actual.put( descriptor.getName(), descriptor );
		}

		for ( EntityPropertyDescriptor descriptor : parent.getRegisteredDescriptors() ) {
			EntityPropertyDescriptor local = actual.get( descriptor.getName() );

			if ( local != null ) {
				actual.put( descriptor.getName(), descriptor.merge( local ) );
			}
			else {
				actual.put( descriptor.getName(), descriptor );
			}
		}

		return actual.values();
	}

	@Override
	public void setDefaultOrder( Comparator<EntityPropertyDescriptor> defaultOrder ) {
		super.setDefaultOrder( EntityPropertyOrder.composite( defaultOrder, parent.getDefaultOrder() ) );
	}

	@Override
	public Comparator<EntityPropertyDescriptor> getDefaultOrder() {
		Comparator<EntityPropertyDescriptor> configured = super.getDefaultOrder();

		return configured != null ? configured : parent.getDefaultOrder();
	}

	@Override
	public EntityPropertyFilter getDefaultFilter() {
		EntityPropertyFilter configured = super.getDefaultFilter();

		return configured != null ? configured : parent.getDefaultFilter();
	}
}
