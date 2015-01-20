package com.foreach.across.modules.entity.registry;

public interface EntityFactory<T>
{
	T createNew( Object... args );

	T createDto( T entity );
}
