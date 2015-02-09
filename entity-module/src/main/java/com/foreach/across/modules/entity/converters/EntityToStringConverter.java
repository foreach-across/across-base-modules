package com.foreach.across.modules.entity.converters;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * @author niels
 * @since 9/02/2015
 */
public class EntityToStringConverter implements ConditionalGenericConverter
{
	private final EntityRegistry entityRegistry;

	public EntityToStringConverter( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton( new ConvertiblePair( Object.class, String.class ) );
	}

	@Override
	public Object convert( Object source, TypeDescriptor sourceType, TypeDescriptor targetType ) {
		if ( source == null ) {
			return null;
		}

		EntityModel<Object, Serializable> entityModel = findEntityModel( sourceType.getType() );

		if ( entityModel != null ) {
			return entityModel.getLabel( source );
		}

		return null;
	}

	@Override
	public boolean matches( TypeDescriptor sourceType, TypeDescriptor targetType ) {
		EntityModel<Object, Serializable> entityModel = findEntityModel( sourceType.getType() );
		return entityModel != null;
	}

	@SuppressWarnings("unchecked")
	private EntityModel<Object, Serializable> findEntityModel( Class<?> entityType ) {
		EntityConfiguration<?> entityConfiguration = entityRegistry.getEntityConfiguration( entityType );

		if ( entityConfiguration != null ) {
			return (EntityModel<Object, Serializable>) entityConfiguration.getEntityModel();
		}

		return null;
	}

}
