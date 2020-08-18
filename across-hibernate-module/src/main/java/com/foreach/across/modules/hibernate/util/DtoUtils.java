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

import com.foreach.across.modules.hibernate.business.EntityWithDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Arne Vandamme
 */
@Slf4j
public class DtoUtils
{
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

			try {
				T dto = (T) entityType.newInstance();
				BeanUtils.copyProperties( entity, dto );
				PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors( entity.getClass() );

				Arrays.stream( propertyDescriptors )
				      .filter( propertyDescriptor -> !BeanUtils.isSimpleProperty( propertyDescriptor.getPropertyType() ) )
				      .forEach( pd -> DtoUtils.deepCopy( pd, dto ) );

				return dto;
			}
			catch ( IllegalAccessException | InstantiationException iae ) {
				throw new IllegalArgumentException( "Unable to create a default DTO", iae );
			}
		}

		return null;
	}

	private static <T> void deepCopy( PropertyDescriptor propertyDescriptor, T dto ) {
		try {
			Class<?> propertyType = propertyDescriptor.getPropertyType();
			Object currentValue = propertyDescriptor.getReadMethod().invoke( dto );
			if ( currentValue != null ) {
				Object newValue = getClonedValue( propertyType, currentValue );
				if ( newValue != null ) {
					propertyDescriptor.getWriteMethod().invoke( dto, newValue );
				}
			}
		}
		catch ( IllegalAccessException | InvocationTargetException | InstantiationException | UnsupportedOperationException e ) {
			LOG.debug( "Unable to make a better clone for property '{}', type {}", propertyDescriptor.getName(),
			           propertyDescriptor.getPropertyType().getSimpleName(), e );
		}
	}

	private static Object getClonedValue( Class<?> propertyType,
	                                      Object currentValue ) throws InstantiationException, IllegalAccessException {
		Object newValue = null;

		if ( propertyType.isArray() ) {
			Object[] typedValue = (Object[]) currentValue;
			Object[] clone = ((Object[]) currentValue).clone();
			Object[] clonedItems = Arrays.stream( typedValue )
			                             .map( value -> copyValue( propertyType.getComponentType(), value ) )
			                             .filter( Objects::nonNull )
			                             .toArray();
			for ( int i = 0; i < clonedItems.length; i++ ) {
				clone[i] = clonedItems[i];
			}
			if ( clonedItems.length == typedValue.length ) {
				newValue = clone;
			}
		}
		else if ( Collection.class.isAssignableFrom( propertyType ) ) {
			Collection<Object> asLinkedList = (Collection<Object>) currentValue.getClass().newInstance();
			Collection<Object> typedValue = (Collection<Object>) currentValue;

			typedValue.stream()
			          .map( value -> copyValue( value.getClass(), value ) )
			          .filter( Objects::nonNull )
			          .forEach( asLinkedList::add );
			if ( typedValue.size() == asLinkedList.size() ) {
				newValue = asLinkedList;
			}
		}
		else {
			newValue = copyValue( propertyType, currentValue );
		}
		return newValue;
	}

	private static Object copyValue( Class<?> propertyType, Object currentValue ) {
		Object newValue = null;
		if ( EntityWithDto.class.isAssignableFrom( propertyType ) ) {
			EntityWithDto typedValue = (EntityWithDto) currentValue;
			newValue = typedValue.toDto();
		}
		else if ( Cloneable.class.isAssignableFrom( propertyType ) ) {
			newValue = ObjectUtils.clone( currentValue );
		}
		return newValue;
	}
}
