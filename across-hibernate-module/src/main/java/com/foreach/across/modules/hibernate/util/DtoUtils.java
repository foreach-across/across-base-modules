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

import org.springframework.util.ClassUtils;

/**
 * @author Arne Vandamme
 */
public class DtoUtils
{
	static DtoMapper dtoMapper = DtoUtils::createDto;

	private DtoUtils() {
	}

	/**
	 * Attempts to create a default dto for an object by creating a new instance
	 * and copying all properties.  This requires the entity type to have a parameterless constructor.
	 * <p>
	 * Will throw a runtime exception if anything goes wrong.
	 *
	 * @param entity Original entity.
	 * @param <T>    Entity type.
	 * @return New instance with the same properties or null if the original was null.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createDto( T entity ) {
		if ( entity != null ) {
			Class entityType = ClassUtils.getUserClass( entity );
			return (T) dtoMapper.createDto( entityType, entity );
		}

		return null;
	}

	static <T> T createDto( Class<T> entityType, T entity ) {
		try {
			T dto = entityType.newInstance();
			org.springframework.beans.BeanUtils.copyProperties( entity, dto );

			return dto;
		}
		catch ( IllegalAccessException | InstantiationException e ) {
			e.printStackTrace();
		}
		return null;
	}
}
