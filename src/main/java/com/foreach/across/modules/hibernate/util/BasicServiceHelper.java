package com.foreach.across.modules.hibernate.util;

import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.springframework.beans.BeanUtils;

import javax.persistence.EntityNotFoundException;

/**
 * @author Arne Vandamme
 */
public class BasicServiceHelper
{
	private BasicServiceHelper() {
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
	public static <T> T save( IdBasedEntityDto<T> dto, Class<T> entityClass, BasicRepository<T> repository ) {
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
