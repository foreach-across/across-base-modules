package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.business.FormElement;
import com.foreach.across.modules.entity.business.FormPropertyDescriptor;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;

public abstract class MultiCheckboxFormElement implements FormElement
{
	private final EntityRegistryImpl registry;
	private Object entity;
	private Collection<?> possibleValues;

	private String name, label;
	private Object value;

	public MultiCheckboxFormElement( EntityRegistryImpl registry,
	                                 PropertyDescriptor propertyDescriptor,
	                                 Collection<?> possibleValues ) {
		this.registry = registry;
		this.possibleValues = possibleValues;

		setName( propertyDescriptor.getName() );
		setLabel( propertyDescriptor.getDisplayName() );
	}

	public MultiCheckboxFormElement( EntityRegistryImpl registry,
	                                 FormPropertyDescriptor propertyDescriptor,
	                                 Collection<?> possibleValues ) {
		this.registry = registry;
		this.possibleValues = possibleValues;

		setName( propertyDescriptor.getName() );
		setLabel( propertyDescriptor.getDisplayName() );
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String getElementType() {
		return "multi-checkbox";
	}

	public String entityLabel( Object entity ) {
		return registry.wrap( entity ).getEntityLabel();
	}

	public Serializable entityId( Object entity ) {
		return registry.wrap( entity ).getId();
	}

	public Collection<?> getPossibleValues() {
		return possibleValues;
	}
}
