package com.foreach.across.modules.entity;

import com.foreach.across.modules.entity.testmodules.springdata.Client;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.junit.Test;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestValidatorDetection
{
	@Test
	public void validatorDescriptionDetector() {
		BeanMetaDataManager manager = new BeanMetaDataManager(
				new ConstraintHelper(), new ExecutableHelper( new TypeResolutionHelper() )
		);

		BeanMetaData<Client> metaData = manager.getBeanMetaData( Client.class );
		assertNotNull( metaData );

		BeanDescriptor beanDescriptor = metaData.getBeanDescriptor();
		assertNotNull( beanDescriptor );

		// non existing property
		PropertyDescriptor descriptor = beanDescriptor.getConstraintsForProperty( "unknown" );
		assertNull( descriptor );

		// property without constraints
		descriptor = beanDescriptor.getConstraintsForProperty( "id" );
		assertNull( descriptor );

		descriptor = beanDescriptor.getConstraintsForProperty( "name" );
		assertEquals( 1, descriptor.getConstraintDescriptors().size() );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertValidatorsToMapForJson() {
		BeanMetaDataManager manager = new BeanMetaDataManager(
				new ConstraintHelper(), new ExecutableHelper( new TypeResolutionHelper() )
		);

		BeanMetaData<Client> metaData = manager.getBeanMetaData( Client.class );
		BeanDescriptor beanDescriptor = metaData.getBeanDescriptor();

		Map<String, Object> validators = new HashMap<>();

		for ( ConstraintDescriptor descriptor : beanDescriptor.getConstraintsForProperty( "name" )
		                                                      .getConstraintDescriptors() ) {
			Map<String, Object> parameters = new HashMap<>();
			parameters.putAll( descriptor.getAttributes() );
			parameters.remove( "groups" );
			parameters.remove( "payload" );

			validators.put( descriptor.getAnnotation().annotationType().getName(), parameters );
		}

		assertEquals( 1, validators.size() );
	}
}
