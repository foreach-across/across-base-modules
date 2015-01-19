package com.foreach.across.modules.entity.converters;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * {@link org.springframework.core.convert.converter.Converter} to convert arbitrary input to an entity
 * that is fully registered with the {@link com.foreach.across.modules.entity.registry.EntityRegistry}.
 */
public class EntityConverter<T extends ConversionService & ConverterRegistry> implements
		ConditionalGenericConverter
{
	private final T conversionService;
	private final EntityRegistry entityRegistry;

	public EntityConverter( T conversionService, EntityRegistry entityRegistry ) {
		this.conversionService = conversionService;
		this.entityRegistry = entityRegistry;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
	 */
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton( new ConvertiblePair( Object.class, Object.class ) );
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	@SuppressWarnings("unchecked")
	public Object convert( Object source, TypeDescriptor sourceType, TypeDescriptor targetType ) {
		if ( source == null || !StringUtils.hasText( source.toString() ) ) {
			return null;
		}

		if ( sourceType.equals( targetType ) ) {
			return source;
		}

		EntityModel<Object, Serializable> entityModel = findEntityModel( targetType.getType() );

		if ( entityModel != null ) {
			return entityModel.findOne( conversionService.convert( source, entityModel.getIdType() ) );
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.ConditionalGenericConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	public boolean matches( TypeDescriptor sourceType, TypeDescriptor targetType ) {
		EntityModel<Object, Serializable> entityModel = findEntityModel( targetType.getType() );

		if ( entityModel == null ) {
			return false;
		}

		if ( sourceType.equals( targetType ) ) {
			return true;
		}

		return conversionService.canConvert( sourceType.getType(), entityModel.getIdType() );
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
