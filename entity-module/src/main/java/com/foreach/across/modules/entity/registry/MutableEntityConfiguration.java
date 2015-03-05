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
import com.foreach.across.modules.entity.registry.support.WritableAttributes;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;

import java.io.Serializable;

/**
 * Manageable version of a {@link com.foreach.across.modules.entity.registry.EntityConfiguration},
 * implementations must allow the configuration to be modified through this interface, without
 * changing the core properties like {@link #getEntityType()} and {@link #getName()}.
 */
public interface MutableEntityConfiguration<T> extends EntityConfiguration<T>, WritableAttributes, ConfigurableEntityViewRegistry
{
	void setDisplayName( String displayName );

	void setHidden( boolean hidden );

	void setEntityModel( EntityModel<T, ? extends Serializable> entityModel );

	void setPropertyRegistry( EntityPropertyRegistry propertyRegistry );

	void setEntityMessageCodeResolver( EntityMessageCodeResolver entityMessageCodeResolver );

	@Override
	MutableEntityAssociation association( String name );

	MutableEntityAssociation createAssociation( String name );
}
