package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.registry.support.AttributeSupport;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.PropertyDescriptorValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;
import org.thymeleaf.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SimpleEntityPropertyDescriptor extends AttributeSupport implements MutableEntityPropertyDescriptor
{
	private String name, displayName;
	private boolean readable, writable, hidden;

	private ValueFetcher valueFetcher;
	private Class<?> propertyType;
	private ResolvableType propertyResolvableType;
	private Field field;

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

	public ResolvableType getPropertyResolvableType() {
		return propertyResolvableType;
	}

	public void setPropertyResolvableType( ResolvableType propertyResolvableType ) {
		this.propertyResolvableType = propertyResolvableType;
	}

	@Override
	public ValueFetcher getValueFetcher() {
		return valueFetcher;
	}

	@Override
	public Field getField() {
		return field;
	}

	public void setField( Field field ) {
		this.field = field;
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
		if ( other.getPropertyResolvableType() != null ) {
			descriptor.setPropertyResolvableType( other.getPropertyResolvableType() );
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

	public static SimpleEntityPropertyDescriptor forPropertyDescriptor( PropertyDescriptor prop, Class<?> entityType ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor();
		descriptor.setName( prop.getName() );

		if ( StringUtils.equals( prop.getName(), prop.getDisplayName() ) ) {
			descriptor.setDisplayName( EntityUtils.generateDisplayName( prop.getName() ) );
		}
		else {
			descriptor.setDisplayName( prop.getDisplayName() );
		}

		Method writeMethod = prop.getWriteMethod();
		Method readMethod = prop.getReadMethod();

		descriptor.setWritable( writeMethod != null );
		descriptor.setReadable( readMethod != null );
		descriptor.setHidden( prop.isHidden() );
		descriptor.setPropertyType( prop.getPropertyType() );
		Field field = ReflectionUtils.findField( entityType, prop.getName() );
		descriptor.setField( field );

		if ( descriptor.isReadable() ) {
			descriptor.setPropertyResolvableType( ResolvableType.forMethodReturnType( readMethod ) );
			descriptor.setValueFetcher( new PropertyDescriptorValueFetcher( prop ) );
		}
		else if ( descriptor.isWritable() ) {
			descriptor.setPropertyResolvableType( ResolvableType.forMethodParameter( writeMethod, 0 ) );
		}
		else {
			descriptor.setPropertyResolvableType( ResolvableType.forType( prop.getPropertyType() ) );
		}

		return descriptor;
	}
}
