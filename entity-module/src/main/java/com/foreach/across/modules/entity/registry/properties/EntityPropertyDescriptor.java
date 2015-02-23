package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.registry.support.ReadableAttributes;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.core.convert.TypeDescriptor;

public interface EntityPropertyDescriptor extends ReadableAttributes
{
	/**
	 * @return Property name.
	 */
	String getName();

	String getDisplayName();

	boolean isReadable();

	boolean isWritable();

	boolean isHidden();

	Class<?> getPropertyType();

	/**
	 * @return more detailed information about the property type (supporting generics)
	 */
	TypeDescriptor getPropertyTypeDescriptor();

	/**
	 * @return Associated instance that can fetch the property value from an instance.
	 */
	ValueFetcher getValueFetcher();

	/**
	 * Creates a new instance that is the result of merging the other descriptor into this one:
	 * properties set on the other descriptor will override this one.
	 *
	 * @param other EntityPropertyDescriptor to be merged into this one.
	 * @return New descriptor representing the merged instance.
	 */
	EntityPropertyDescriptor merge( EntityPropertyDescriptor other );
}
