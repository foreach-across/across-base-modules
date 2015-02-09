package com.foreach.across.modules.entity.views.processors;

import java.util.Map;

/**
 * @author niels
 * @since 6/02/2015
 */
public class NoOpRowProcessor<T> implements RowProcessor<T>
{
	@Override
	public Map<String, String> attributes( T entity ) {
		return null;
	}
}
