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
package com.foreach.across.modules.entity.business;

import com.foreach.across.modules.hibernate.business.EntityWithDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Arne Vandamme
 */
public class EntityModelImpl<T, ID extends Serializable> implements EntityModel<T, ID>
{
	private CrudRepository<T, ID> repository;
	private EntityInformation<T, ID> entityInformation;

	private Constructor<T> constructor;

	public EntityModelImpl( PersistentEntity<T, ?> persistentEntity,
	                        EntityInformation<T, ID> entityInformation,
	                        CrudRepository<T, ID> repository ) {
		this.entityInformation = entityInformation;
		this.repository = repository;

		constructor = persistentEntity.getPersistenceConstructor().getConstructor();
	}

	@Override
	public T createNew( Object... args ) {
		try {
			return constructor.newInstance( args );
		}
		catch ( IllegalAccessException | InstantiationException | InvocationTargetException ie ) {
			throw new RuntimeException( ie );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T createDto( T entity ) {
		if ( entity instanceof EntityWithDto ) {
			return ( (EntityWithDto<T>) entity ).toDto();
		}

		T dto = createNew();
		BeanUtils.copyProperties( entity, dto );

		return dto;
	}

	@Override
	public T findOne( ID id ) {
		return repository.findOne( id );
	}

	@Override
	public T save( T entity ) {
		return repository.save( entity );
	}

	@Override
	public boolean isNew( T entity ) {
		return entityInformation.isNew( entity );
	}

	@Override
	public ID getId( T entity ) {
		return entityInformation.getId( entity );
	}

	@Override
	public Class<ID> getIdType() {
		return entityInformation.getIdType();
	}

	@Override
	public Class<T> getJavaType() {
		return entityInformation.getJavaType();
	}
}
