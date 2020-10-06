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

package com.foreach.across.modules.hibernate.util;

/**
 * Defines a mapping function to create a dto based on an entity (and it's type)
 *
 * @author Steven Gentens
 * @since 4.0.1
 */
@FunctionalInterface
interface DtoMapper<T>
{
	/**
	 * Creates a DTO object of the entity passed into the function.
	 * A DTO is in this case a (complex) object which may be hibernate attached.
	 * If the instance cannot be mapped to a DTO, {@code null} is returned
	 *
	 * @param entityType class of the instance
	 * @param entity     instance to create a DTO for
	 * @return dto or {@code null} if unable to map
	 */
	T createDto( Class<T> entityType, T entity );
}
