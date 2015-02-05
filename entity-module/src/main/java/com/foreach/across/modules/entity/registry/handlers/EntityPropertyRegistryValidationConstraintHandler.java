package com.foreach.across.modules.entity.registry.handlers;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.springframework.stereotype.Component;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;

/**
 * @author niels
 * @since 4/02/2015
 */
@Component
public class EntityPropertyRegistryValidationConstraintHandler
{

	private final BeanMetaDataManager metaDataManager = new BeanMetaDataManager(
			new ConstraintHelper(), new ExecutableHelper( new TypeResolutionHelper() )
	);

	public void handle( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		BeanMetaData<?> metaData = metaDataManager.getBeanMetaData( entityType );
		BeanDescriptor beanDescriptor = metaData.getBeanDescriptor();

		if ( beanDescriptor != null ) {

			for ( EntityPropertyDescriptor descriptor : registry.getRegisteredDescriptors() ) {
				PropertyDescriptor validatorDescriptor
						= beanDescriptor.getConstraintsForProperty( descriptor.getName() );

				if ( validatorDescriptor != null ) {
					MutableEntityPropertyDescriptor mutable = registry.getMutableProperty( descriptor.getName() );

					if ( mutable != null ) {
						mutable.addAttribute( PropertyDescriptor.class, validatorDescriptor );
					}
				}
			}
		}
	}
}
