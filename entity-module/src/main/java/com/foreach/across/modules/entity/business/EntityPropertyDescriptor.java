package com.foreach.across.modules.entity.business;

import java.beans.PropertyDescriptor;

public interface EntityPropertyDescriptor
{
	/**
	 * @return Property name.
	 */
	String getName();

	String getDisplayName();

	boolean isReadable();

	boolean isWritable();

	boolean isHidden();

	/**
	 * @return The backing PropertyDescriptor.
	 */
	PropertyDescriptor getPropertyDescriptor();
}
