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

import com.foreach.across.modules.entity.registry.handlers.EntityPropertyRegistryValidationConstraintHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the {@link EntityPropertyRegistry} instances for
 * a set of entity types.
 *
 * @author Arne Vandamme
 */
public class EntityPropertyRegistries
{
	@Autowired
	private EntityPropertyRegistryValidationConstraintHandler validationConstraintHandler;

	private final Map<Class<?>, EntityPropertyRegistry> registries = new HashMap<>();

	public EntityPropertyRegistry getRegistry( Class<?> entityType ) {
		return getRegistry( entityType, true );
	}

	public EntityPropertyRegistry getRegistry( Class<?> entityType, boolean createIfNotFound ) {
		EntityPropertyRegistry registry = registries.get( entityType );

		if ( registry == null && createIfNotFound ) {
			registry = new DefaultEntityPropertyRegistry( entityType, this );

			validationConstraintHandler.handle( entityType, (MutableEntityPropertyRegistry) registry );

			registries.put( entityType, registry );
		}

		return registry;
	}

	public void add( Class<?> entityType, EntityPropertyRegistry registry ) {
		registries.put( entityType, registry );
	}
}
