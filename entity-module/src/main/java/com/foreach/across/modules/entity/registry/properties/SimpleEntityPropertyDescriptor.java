package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.registry.support.AttributeSupport;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.PropertyDescriptorValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.beans.BeanUtils;
import org.thymeleaf.util.StringUtils;

import java.beans.PropertyDescriptor;

public class SimpleEntityPropertyDescriptor extends AttributeSupport implements MutableEntityPropertyDescriptor
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

	@Override
	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	@Override
	public boolean isReadable() {
		return readable;
	}

	@Override
	public void setReadable( boolean readable ) {
		this.readable = readable;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public void setWritable( boolean writable ) {
		this.writable = writable;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden( boolean hidden ) {
		this.hidden = hidden;
	}

	@Override
	public void setValueFetcher( ValueFetcher<?> valueFetcher ) {
		this.valueFetcher = valueFetcher;

		if ( valueFetcher != null ) {
			readable = true;
		}
	}

	@Override
	public Class<?> getPropertyType() {
		return propertyType;
	}

	@Override
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

		descriptor.addAllAttributes( other.getAttributes() );

		return descriptor;
	}

	public static SimpleEntityPropertyDescriptor forPropertyDescriptor( PropertyDescriptor prop ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor();

		descriptor.setName( prop.getName() );

		if ( StringUtils.equals( prop.getName(), prop.getDisplayName() ) ) {
			descriptor.setDisplayName( EntityUtils.generateDisplayName( prop.getName() ) );
		}
		else {
			descriptor.setDisplayName( prop.getDisplayName() );
		}

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
