package com.foreach.across.modules.entity.views.processors;

import java.util.Map;

/**
 * @author niels
 * @since 6/02/2015
 */
public interface RowProcessor<T>
{
	Map<String, String> attributes( T entity );
}
