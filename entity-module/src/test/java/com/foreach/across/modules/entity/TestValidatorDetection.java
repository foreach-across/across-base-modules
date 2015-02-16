/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity;

import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.junit.Test;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestValidatorDetection
{
	@Test
	public void validatorDescriptionDetector() {
		BeanMetaDataManager manager = new BeanMetaDataManager(
				new ConstraintHelper(), Collections.<MetaDataProvider>emptyList()
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
				new ConstraintHelper(), Collections.<MetaDataProvider>emptyList()		);

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
