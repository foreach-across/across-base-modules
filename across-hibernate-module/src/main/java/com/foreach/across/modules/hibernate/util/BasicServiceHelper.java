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

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityNotFoundException;

/**
 * @author Arne Vandamme
 */
public class BasicServiceHelper
{
	private BasicServiceHelper() {
	}

	public static <T> T save( T dto, CrudRepository<T, ?> repository ) {
		T saved = repository.save( dto );
		BeanUtils.copyProperties( saved, dto );

		return saved;
	}

	/**
	 * Creates or updates an entity based on a DTO passed in. The entity should support
	 * an {@link com.foreach.across.modules.hibernate.dto.IdBasedEntityDto} and work with a
	 * {@link com.foreach.across.modules.hibernate.repositories.BasicRepository}.
	 *
	 * @param dto         DTO for the given entity.
	 * @param entityClass User class of the final entity (must have a public parameterless constructor).
	 * @param repository  Repository in which to persist the entity.
	 * @param <T>         Entity class.
	 * @return Persisted entity.
	 * @see com.foreach.across.modules.hibernate.repositories.BasicRepository
	 * @see com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl
	 */
	public static <T extends IdBasedEntity> T save( IdBasedEntityDto<T> dto, Class<T> entityClass, BasicRepository<T> repository ) {
		T entity;

		if ( !dto.isNewEntity() ) {
			entity = repository.getById( dto.getId() );

			if ( entity == null ) {
				throw new EntityNotFoundException( String.format( "No %s with id %s", entityClass.getSimpleName(),
				                                                  dto.getId() ) );
			}
		}
		else {
			try {
				entity = entityClass.newInstance();
			}
			catch ( InstantiationException | IllegalAccessException e ) {
				throw new RuntimeException( e );
			}
		}

		BeanUtils.copyProperties( dto, entity );

		if ( dto.isNewEntity() ) {
			repository.create( entity );
		}
		else {
			repository.update( entity );
		}

		dto.copyFrom( entity );

		return entity;
	}
}
