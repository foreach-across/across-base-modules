package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.registry.support.AttributeSupport;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.MethodValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.thymeleaf.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class SimpleEntityPropertyDescriptor extends AttributeSupport implements MutableEntityPropertyDescriptor
{
	private String name, displayName;
	private boolean readable, writable, hidden;

	private ValueFetcher valueFetcher;
	private Class<?> propertyType;
	private TypeDescriptor propertyTypeDescriptor;

	public SimpleEntityPropertyDescriptor() {
	}

	public SimpleEntityPropertyDescriptor( String name ) {
		this.name = name;
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

	public TypeDescriptor getPropertyTypeDescriptor() {
		return propertyTypeDescriptor;
	}

	public void setPropertyTypeDescriptor( TypeDescriptor propertyTypeDescriptor ) {
		this.propertyTypeDescriptor = propertyTypeDescriptor;
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

		if ( other.getPropertyType() != null ) {
			descriptor.setPropertyType( other.getPropertyType() );
		}
		if ( other.getPropertyTypeDescriptor() != null ) {
			descriptor.setPropertyTypeDescriptor( other.getPropertyTypeDescriptor() );
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

	public static SimpleEntityPropertyDescriptor forProperty( Property property ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor();
		descriptor.setName( property.getName() );

		descriptor.setDisplayName( EntityUtils.generateDisplayName( property.getName() ) );

		descriptor.setWritable( property.getWriteMethod() != null );
		descriptor.setReadable( property.getReadMethod() != null );
		descriptor.setPropertyType( property.getType() );
		descriptor.setPropertyTypeDescriptor( new TypeDescriptor( property ) );

		if ( descriptor.isReadable() ) {
			descriptor.setValueFetcher( new MethodValueFetcher( property.getReadMethod() ) );
		}

		return descriptor;

	}

	public static SimpleEntityPropertyDescriptor forPropertyDescriptor( PropertyDescriptor prop, Class<?> entityType ) {
		Method writeMethod = prop.getWriteMethod();
		Method readMethod = prop.getReadMethod();
		Property property = new Property( entityType, readMethod, writeMethod, prop.getName() );
		SimpleEntityPropertyDescriptor descriptor = forProperty( property );

		if ( StringUtils.equals( prop.getName(), prop.getDisplayName() ) ) {
			descriptor.setDisplayName( EntityUtils.generateDisplayName( prop.getName() ) );
		}
		else {
			descriptor.setDisplayName( prop.getDisplayName() );
		}
		return descriptor;
	}
}
