package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.hibernate.business.EntityWithDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mapping.PersistentEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a basic {@link com.foreach.across.modules.entity.registry.EntityFactory} from
 * a {@link org.springframework.data.mapping.PersistentEntity} instance.
 */
public class PersistentEntityFactory<T> implements EntityFactory<T>
{
	private final Constructor<T> constructor;

	public PersistentEntityFactory( PersistentEntity<T, ?> persistentEntity ) {
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

	@SuppressWarnings("unchecked")
	@Override
	public T createDto( T entity ) {
		if ( entity instanceof EntityWithDto ) {
			return ( (EntityWithDto<T>) entity ).toDto();
		}

		T dto = createNew();
		BeanUtils.copyProperties( entity, dto );

		return dto;
	}
}
