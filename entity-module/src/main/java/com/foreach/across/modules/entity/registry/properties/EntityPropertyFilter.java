package com.foreach.across.modules.entity.registry.properties;

import java.util.Collection;

public interface EntityPropertyFilter
{
	boolean include( EntityPropertyDescriptor descriptor );

	/**
	 * Sub interface stating that the property filter declares all included properties explicitly
	 */
	static interface Inclusive extends EntityPropertyFilter {
		Collection<String> getPropertyNames();
	}
}
