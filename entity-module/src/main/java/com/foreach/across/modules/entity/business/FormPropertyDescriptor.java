package com.foreach.across.modules.entity.business;

import org.springframework.core.ResolvableType;

@Deprecated
public class FormPropertyDescriptor
{
	private String name, displayName;
	private boolean readable = true, writable = true;
	private Class<?> propertyType;
	private ResolvableType propertyResolvableType;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable( boolean readable ) {
		this.readable = readable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable( boolean writable ) {
		this.writable = writable;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}

	public void setPropertyType( Class<?> propertyType ) {
		this.propertyType = propertyType;
	}

	public ResolvableType getPropertyResolvableType() {
		return propertyResolvableType;
	}

	public void setPropertyResolvableType( ResolvableType propertyResolvableType ) {
		this.propertyResolvableType = propertyResolvableType;
	}
}
