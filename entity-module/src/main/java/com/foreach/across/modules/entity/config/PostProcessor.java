package com.foreach.across.modules.entity.config;

public interface PostProcessor<T>
{
	void process( T configuration );
}
