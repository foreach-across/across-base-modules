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

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.registry.builders.EntityPropertyRegistryBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the {@link EntityPropertyRegistry} instances for a set of entity types.
 * Dispatches to {@link EntityPropertyRegistryBuilder} instances for attribute generation.
 *
 * @author Arne Vandamme
 */
public class EntityPropertyRegistries
{
	@RefreshableCollection(includeModuleInternals = true, incremental = true)
	private Collection<EntityPropertyRegistryBuilder> registryBuilders;

	private final Map<Class<?>, EntityPropertyRegistry> registries = new HashMap<>();

	public EntityPropertyRegistry getRegistry( Class<?> entityType ) {
		return getRegistry( entityType, true );
	}

	public EntityPropertyRegistry getRegistry( Class<?> entityType, boolean createIfNotFound ) {
		EntityPropertyRegistry registry = registries.get( entityType );

		if ( registry == null && createIfNotFound ) {
			DefaultEntityPropertyRegistry newRegistry = new DefaultEntityPropertyRegistry( entityType, this );

			for ( EntityPropertyRegistryBuilder builder : registryBuilders ) {
				builder.buildRegistry( entityType, newRegistry );
			}

			registries.put( entityType, newRegistry );

			registry = newRegistry;
		}

		return registry;
	}

	public void add( Class<?> entityType, EntityPropertyRegistry registry ) {
		registries.put( entityType, registry );
	}
}
