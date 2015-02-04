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

/**
 * Base interface for managing registered entity types.  Usually only automatic registration interacts directly
 * with the EntityRegistry.  Manual configuration or customization should be done using the
 * {@link com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder} classes.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.registrars.repository.RepositoryEntityRegistrar
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 */
public interface MutableEntityRegistry extends EntityRegistry
{
	void register( MutableEntityConfiguration<?> entityConfiguration );

	<T> MutableEntityConfiguration<T> getMutableEntityConfiguration( Class<T> entityType );

	<T> MutableEntityConfiguration<T> remove( String entityName );

	<T> MutableEntityConfiguration<T> remove( Class<T> entityType );
}
