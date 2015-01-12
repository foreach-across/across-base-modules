package com.foreach.across.modules.entity.business;

import com.foreach.across.modules.entity.util.EntityUtils;

import java.beans.PropertyDescriptor;

public class SimpleEntityPropertyDescriptor implements EntityPropertyDescriptor
{
	private String name, displayName;
	private boolean readable, writable, hidden;

	private PropertyDescriptor descriptor;

	public SimpleEntityPropertyDescriptor( PropertyDescriptor descriptor ) {
		this.descriptor = descriptor;

		name = descriptor.getName();
		displayName = descriptor.getDisplayName();
		writable = descriptor.getWriteMethod() != null;
		readable = descriptor.getReadMethod() != null;
		hidden = descriptor.isHidden();
	}

	/**
	 * @return Property name.
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor() {
		return descriptor;
	}

	public void setDescriptor( PropertyDescriptor descriptor ) {
		this.descriptor = descriptor;
	}

	@Override
	public boolean isReadable() {
		return readable;
	}

	public void setReadable( boolean readable ) {
		this.readable = readable;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	public void setWritable( boolean writable ) {
		this.writable = writable;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden( boolean hidden ) {
		this.hidden = hidden;
	}

	@Override
	public Object getValue( Object entity ) {
		return EntityUtils.getPropertyValue( getPropertyDescriptor(), entity );
	}
}
