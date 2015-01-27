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
package com.foreach.across.modules.entity.views.forms.elements.textbox;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.forms.CloningFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.support.ConversionServiceConvertingValuePrinter;
import com.foreach.common.test.MockedLoader;
import com.mysema.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestTextboxFormElementBuilderFactoryAssembler.Config.class, loader = MockedLoader.class)
public class TestTextboxFormElementBuilderFactoryAssembler
{
	@Autowired
	private TextboxFormElementBuilderFactoryAssembler assembler;

	@Autowired
	private EntityConfiguration entityConfiguration;

	@Autowired
	private EntityPropertyRegistry registry;

	private TextboxFormElementBuilder template;
	private Map<String, EntityPropertyDescriptor> properties = new HashMap<>();

	@Before
	public void before() {
		reset( entityConfiguration, registry );

		when( entityConfiguration.getEntityMessageCodeResolver() )
				.thenReturn( mock( EntityMessageCodeResolver.class ) );

		if ( properties.isEmpty() ) {
			BeanMetaDataManager manager = new BeanMetaDataManager(
					new ConstraintHelper(), new ExecutableHelper( new TypeResolutionHelper() )
			);

			BeanMetaData<Validators> metaData = manager.getBeanMetaData( Validators.class );
			BeanDescriptor beanDescriptor = metaData.getBeanDescriptor();

			for ( Field field : ReflectionUtils.getFields( Validators.class ) ) {
				String propertyName = field.getName();
				PropertyDescriptor validationDescriptor = beanDescriptor.getConstraintsForProperty( field.getName() );

				EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
				when( descriptor.getName() ).thenReturn( propertyName );
				when( descriptor.getDisplayName() ).thenReturn( StringUtils.lowerCase( propertyName ) );
				when( descriptor.getAttribute( PropertyDescriptor.class ) ).thenReturn( validationDescriptor );

				properties.put( propertyName, descriptor );
			}
		}
	}

	@Test
	public void sizeValidator() {
		template = assembleAndVerify( "sizeValidator" );
		assertEquals( Integer.valueOf( 200 ), template.getMaxLength() );
		assertFalse( template.isRequired() );
	}

	@Test
	public void lengthValidator() {
		template = assembleAndVerify( "lengthValidator" );
		assertEquals( Integer.valueOf( 10 ), template.getMaxLength() );
		assertFalse( template.isRequired() );
	}

	@Test
	public void createWithoutValidators() {
		template = assembleAndVerify( "noValidator" );
		assertNull( template.getMaxLength() );
		assertFalse( template.isRequired() );
	}

	@Test
	public void notNullValidator() {
		template = assembleAndVerify( "notNullValidator" );
		assertNull( template.getMaxLength() );
		assertTrue( template.isRequired() );
	}

	@Test
	public void notBlankValidator() {
		template = assembleAndVerify( "notBlankValidator" );
		assertNull( template.getMaxLength() );
		assertTrue( template.isRequired() );
	}

	@Test
	public void notEmptyValidator() {
		template = assembleAndVerify( "notEmptyValidator" );
		assertNull( template.getMaxLength() );
		assertTrue( template.isRequired() );
	}

	@Test
	public void combinedValidator() {
		template = assembleAndVerify( "combinedValidator" );
		assertEquals( Integer.valueOf( 50 ), template.getMaxLength() );
		assertTrue( template.isRequired() );
	}

	private TextboxFormElementBuilder assembleAndVerify( String propertyName ) {
		TextboxFormElementBuilder template = assemble( properties.get( propertyName ) );

		assertEquals( propertyName, template.getName() );
		assertEquals( StringUtils.lowerCase( propertyName ), template.getLabel() );
		assertEquals( "properties." + propertyName, template.getLabelCode() );
		assertNull( template.getCustomTemplate() );
		assertNotNull( template.getMessageCodeResolver() );
		assertNotNull( template.getValuePrinter() );
		assertTrue( template.getValuePrinter() instanceof ConversionServiceConvertingValuePrinter );

		return template;
	}

	private TextboxFormElementBuilder assemble( EntityPropertyDescriptor descriptor ) {
		CloningFormElementBuilderFactory builderFactory =
				(CloningFormElementBuilderFactory) assembler.createBuilderFactory(
						entityConfiguration, registry, descriptor
				);
		assertNotNull( builderFactory );

		return (TextboxFormElementBuilder) builderFactory.getBuilderTemplate();
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public TextboxFormElementBuilderFactoryAssembler textboxFormElementBuilderFactoryAssembler() {
			return new TextboxFormElementBuilderFactoryAssembler();
		}
	}

	private static class Validators
	{
		public String noValidator;

		@NotNull
		public String notNullValidator;

		@NotBlank
		public String notBlankValidator;

		@NotEmpty
		public String notEmptyValidator;

		@Size(min = 5, max = 200)
		public String sizeValidator;

		@Length(min = 1, max = 10)
		public String lengthValidator;

		@NotBlank
		@Size(max = 50)
		public String combinedValidator;
	}
}
