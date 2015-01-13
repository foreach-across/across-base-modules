package com.foreach.across.modules.entity.business;

import com.foreach.across.modules.entity.views.helpers.PropertyDescriptorValueFetcher;
import com.foreach.across.modules.entity.views.helpers.ValueFetcher;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;

public class SimpleEntityPropertyDescriptor implements EntityPropertyDescriptor
{
	private String name, displayName;
	private boolean readable, writable, hidden;

	private ValueFetcher valueFetcher;
	private Class<?> propertyType;

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

	public void setValueFetcher( ValueFetcher valueFetcher ) {
		this.valueFetcher = valueFetcher;

		if ( valueFetcher != null ) {
			readable = true;
		}
	}

	@Override
	public Class<?> getPropertyType() {
		return propertyType;
	}

	public void setPropertyType( Class<?> propertyType ) {
		this.propertyType = propertyType;
	}

	@Override
	public ValueFetcher getValueFetcher() {
		return valueFetcher;
	}

	@Override
	public EntityPropertyDescriptor merge( EntityPropertyDescriptor other ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor();
		BeanUtils.copyProperties( this, descriptor );

		descriptor.setWritable( other.isWritable() );
		descriptor.setReadable( other.isReadable() );
		descriptor.setHidden( other.isHidden() );

		if ( other.getPropertyType() != null ) {
			descriptor.setPropertyType( other.getPropertyType() );
		}
		if ( other.getValueFetcher() != null ) {
			descriptor.setValueFetcher( other.getValueFetcher() );
		}
		if ( other.getName() != null ) {
			descriptor.setName( other.getName() );
		}
		if ( other.getDisplayName() != null ) {
			descriptor.setDisplayName( other.getDisplayName() );
		}

		return null;
	}

	public static SimpleEntityPropertyDescriptor forPropertyDescriptor( PropertyDescriptor prop ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor();

		descriptor.setName( prop.getName() );
		descriptor.setDisplayName( prop.getDisplayName() );
		descriptor.setWritable( prop.getWriteMethod() != null );
		descriptor.setReadable( prop.getReadMethod() != null );
		descriptor.setHidden( prop.isHidden() );
		descriptor.setPropertyType( prop.getPropertyType() );

		if ( descriptor.isReadable() ) {
			descriptor.setValueFetcher( new PropertyDescriptorValueFetcher( prop ) );
		}

		return descriptor;
	}
}
