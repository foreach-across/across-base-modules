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

import com.github.dozermapper.core.Mapper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Arne Vandamme
 */
public class DtoUtils
{
	static Mapper beanMapper;

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
			if ( beanMapper != null ) {

				return (T) beanMapper.map( entity, entityType );
			}

			try {
				T dto = (T) entityType.newInstance();
				BeanUtils.copyProperties( entity, dto );
				return dto;
			}
			catch ( IllegalAccessException | InvocationTargetException | InstantiationException e ) {
				e.printStackTrace();
			}

		}

		return null;
	}
}
