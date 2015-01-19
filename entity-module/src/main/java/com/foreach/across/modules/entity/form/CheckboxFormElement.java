package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.business.FormElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;

public class CheckboxFormElement implements FormElement
{
	private Object entity, value;

	private String name, label;

	public CheckboxFormElement( EntityPropertyDescriptor propertyDescriptor ) {
		setName( propertyDescriptor.getName() );
		setLabel( propertyDescriptor.getDisplayName() );
	}

	@Override
	public void setEntity( Object entity ) {
		this.entity = entity;
	}

	@Override
	public String getElementType() {
		return "checkbox";
	}

	public void setName( String name ) {
		this.name = name;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public void setValue( Object value ) {
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public Object getValue() {
		return value;
	}

	//	public Object getValue() throws Exception {
	//return entity != null ? propertyDescriptor.getReadMethod().invoke( entity ) : null;
	//}
}
