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
package com.foreach.across.modules.entity.registry.builders;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.springframework.stereotype.Component;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.util.Collections;
import java.util.List;

/**
 * @author niels
 * @since 4/02/2015
 */
@Component
public class EntityPropertyRegistryValidationMetaDataBuilder implements EntityPropertyRegistryBuilder
{
	private final List<MetaDataProvider> metaDataProvider = Collections.emptyList();

	private final BeanMetaDataManager metaDataManager = new BeanMetaDataManager(
			new ConstraintHelper(),
			metaDataProvider );

	public void buildRegistry( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
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
