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
package com.foreach.across.modules.entity.views.elements.form;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.CloningViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.support.ConversionServiceConvertingValuePrinter;
import com.mysema.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public abstract class FormElementBuilderFactoryAssemblerTestSupport<T extends FormElementBuilderSupport>
{
	@Autowired
	protected EntityConfiguration entityConfiguration;

	@Autowired
	protected EntityPropertyRegistry registry;

	@Autowired
	protected EntityRegistry entityRegistry;

	protected Map<String, EntityPropertyDescriptor> properties = new HashMap<>();

	protected T template;

	@Before
	@SuppressWarnings("unchecked")
	public void before() {
		reset( entityConfiguration, registry );

		when( entityConfiguration.getEntityMessageCodeResolver() )
				.thenReturn( mock( EntityMessageCodeResolver.class ) );

		if ( properties.isEmpty() ) {
			BeanMetaDataManager manager = new BeanMetaDataManager(
					new ConstraintHelper(), Collections.<MetaDataProvider>emptyList()
			);

			BeanMetaData<?> metaData = manager.getBeanMetaData( getTestClass() );
			BeanDescriptor beanDescriptor = metaData.getBeanDescriptor();

			for ( Field field : ReflectionUtils.getFields( getTestClass() ) ) {
				String propertyName = field.getName();
				PropertyDescriptor validationDescriptor = beanDescriptor.getConstraintsForProperty( field.getName() );

				EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
				when( descriptor.getName() ).thenReturn( propertyName );
				when( descriptor.getDisplayName() ).thenReturn( StringUtils.lowerCase( propertyName ) );
				when( descriptor.getAttribute( PropertyDescriptor.class ) ).thenReturn( validationDescriptor );
				when( descriptor.getPropertyType() ).thenReturn( (Class) field.getType() );
				TypeDescriptor typeDescriptor = new TypeDescriptor( field );
				when( descriptor.getPropertyTypeDescriptor() ).thenReturn( typeDescriptor );

				properties.put( propertyName, descriptor );
			}
		}
	}

	protected abstract Class getTestClass();

	protected T assembleAndVerify( String propertyName ) {
		T template = assemble( properties.get( propertyName ) );

		assertEquals( propertyName, template.getName() );
		assertEquals( StringUtils.lowerCase( propertyName ), template.getLabel() );
		assertEquals( "properties." + propertyName, template.getLabelCode() );
		assertNull( template.getCustomTemplate() );
		assertNotNull( template.getMessageCodeResolver() );
		assertNotNull( template.getValuePrinter() );
		assertTrue( template.getValuePrinter() instanceof ConversionServiceConvertingValuePrinter );

		return template;
	}

	@SuppressWarnings("unchecked")
	protected T assemble( EntityPropertyDescriptor descriptor ) {
		CloningViewElementBuilderFactory builderFactory =
				(CloningViewElementBuilderFactory) getAssembler().createBuilderFactory(
						entityConfiguration, registry, descriptor
				);
		assertNotNull( builderFactory );

		return (T) builderFactory.getBuilderTemplate();
	}

	protected abstract FormElementBuilderFactoryAssemblerSupport getAssembler();
}
