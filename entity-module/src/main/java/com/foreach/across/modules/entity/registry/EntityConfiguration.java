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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.support.ReadableAttributes;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;

import java.io.Serializable;
import java.util.Collection;

public interface EntityConfiguration<T> extends ReadableAttributes, EntityViewRegistry
{
	String getName();

	String getDisplayName();

	Class<T> getEntityType();

	EntityModel<T, ? extends Serializable> getEntityModel();

	EntityPropertyRegistry getPropertyRegistry();

	EntityMessageCodeResolver getEntityMessageCodeResolver();

	Collection<EntityAssociation> getAssociations();

	EntityAssociation association( String name );

	boolean isNew( T entity );

	Class<?> getIdType();

	Serializable getId( T entity );

	String getLabel( T entity );

	/**
	 * @return True if this configuration should not be displayed in UI implementations.
	 */
	boolean isHidden();
}
