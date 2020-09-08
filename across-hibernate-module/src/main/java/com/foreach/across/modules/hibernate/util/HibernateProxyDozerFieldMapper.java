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

import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.github.dozermapper.core.CustomFieldMapper;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.fieldmap.FieldMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * {@link CustomFieldMapper} that initializes a non-hibernate initialized lazy field via a new session upon the first request to the object.
 * The object is then also mapped to a dto via a given {@link Mapper} instance before the resulting object is actually accessed.
 * If for some reason the mapping of the hibernate object fails, the hibernate initialized version will be accessed instead.
 */
@RequiredArgsConstructor
@Slf4j
public class HibernateProxyDozerFieldMapper implements CustomFieldMapper
{
	private final Mapper mapper;
	private final HibernateSessionHolder hibernateSessionHolder;

	@Override
	public boolean mapField( Object source, Object destination, Object sourceFieldValue, ClassMap classMap, FieldMap fieldMapping ) {
		if ( !Hibernate.isInitialized( sourceFieldValue ) ) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass( destination.getClass() );

			ValueHolder valueHolder = new ValueHolder( mapper, hibernateSessionHolder, destination.getClass() );
			valueHolder.setObject( sourceFieldValue );

			enhancer.setCallback( (MethodInterceptor) ( obj, method, args, proxy ) -> {
				valueHolder.initializeHibernateObject();
				valueHolder.convertToDto();
				return method.invoke( valueHolder.getObject(), args );
			} );

			fieldMapping.writeDestValue( destination, enhancer.create() );
			return true;
		}
		return false;
	}

	@Data
	static class ValueHolder
	{
		private final Mapper mapper;
		private final HibernateSessionHolder hibernateSessionHolder;
		private final Class destinationType;

		private Object object;
		private boolean hibernateInitialized = false;
		private boolean mappingAttempted = false;

		/**
		 * Initializes the held hibernate object if necessary.
		 */
		void initializeHibernateObject() {
			if ( !mappingAttempted && !hibernateInitialized ) {
				try (Session session = hibernateSessionHolder.openSession()) {
					session.update( object );
					Hibernate.initialize( object );
					hibernateInitialized = true;
				}
			}
		}

		/**
		 * Attempts to convert the present object to a dto to ensure its fields are hibernate detached.
		 * If mapping has failed, the held hibernate object will not be converted to a dto.
		 * This method should only be called <i>after</i> the held hibernate object has been initialized.
		 *
		 * @see #initializeHibernateObject()
		 */
		@SuppressWarnings("unchecked")
		void convertToDto() {
			if ( !mappingAttempted ) {
				try {
					mappingAttempted = true;
					object = mapper.map( object, destinationType );
				}
				catch ( MappingException e ) {
					LOG.error( "Unexpected exception whilst creating a dto of the initialized hibernate object", e );
				}
			}
		}
	}
}
