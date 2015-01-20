package com.foreach.across.modules.entity.config;

/**
 * If a different instance is returned, it should replace the existing.
 */
public interface PostProcessor<T>
{
	T process( T configuration );
}
