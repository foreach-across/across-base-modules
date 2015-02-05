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
package com.foreach.across.modules.entity.views.elements.form.textbox;

import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderFactoryAssemblerSupport;
import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderFactoryAssemblerTestSupport;
import com.foreach.common.test.MockedLoader;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
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

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestTextboxFormElementBuilderFactoryAssembler.Config.class, loader = MockedLoader.class)
public class TestTextboxFormElementBuilderFactoryAssembler
		extends FormElementBuilderFactoryAssemblerTestSupport<TextboxFormElementBuilder>
{
	@Autowired
	private TextboxFormElementBuilderFactoryAssembler assembler;

	private TextboxFormElementBuilder template;

	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Override
	protected FormElementBuilderFactoryAssemblerSupport getAssembler() {
		return assembler;
	}

	@Test
	public void sizeValidator() {
		template = assembleAndVerify( "sizeValidator" );
		assertEquals( Integer.valueOf( 200 ), template.getMaxLength() );
		assertFalse( template.isRequired() );
		assertFalse( template.isMultiLine() );
	}

	@Test
	public void sizeForMultiLineValidator() {
		template = assembleAndVerify( "sizeForMultiLineValidator" );
		assertEquals( Integer.valueOf( 201 ), template.getMaxLength() );
		assertFalse( template.isRequired() );
		assertTrue( template.isMultiLine() );
	}

	@Test
	public void lengthValidator() {
		template = assembleAndVerify( "lengthValidator" );
		assertEquals( Integer.valueOf( 10 ), template.getMaxLength() );
		assertFalse( template.isRequired() );
		assertFalse( template.isMultiLine() );
	}

	@Test
	public void noValidator() {
		template = assembleAndVerify( "noValidator" );
		assertNull( template.getMaxLength() );
		assertFalse( template.isRequired() );
		assertTrue( template.isMultiLine() );
	}

	@Test
	public void noValidatorNumber() {
		template = assembleAndVerify( "noValidatorNumber" );
		assertNull( template.getMaxLength() );
		assertFalse( template.isRequired() );
		assertFalse( template.isMultiLine() );
	}

	@Test
	public void notNullValidator() {
		template = assembleAndVerify( "notNullValidator" );
		assertNull( template.getMaxLength() );
		assertTrue( template.isRequired() );
		assertTrue( template.isMultiLine() );
	}

	@Test
	public void notBlankValidator() {
		template = assembleAndVerify( "notBlankValidator" );
		assertNull( template.getMaxLength() );
		assertTrue( template.isRequired() );
		assertTrue( template.isMultiLine() );
	}

	@Test
	public void notEmptyValidator() {
		template = assembleAndVerify( "notEmptyValidator" );
		assertNull( template.getMaxLength() );
		assertTrue( template.isRequired() );
		assertTrue( template.isMultiLine() );
	}

	@Test
	public void combinedValidator() {
		template = assembleAndVerify( "combinedValidator" );
		assertEquals( Integer.valueOf( 50 ), template.getMaxLength() );
		assertTrue( template.isRequired() );
		assertFalse( template.isMultiLine() );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public TextboxFormElementBuilderFactoryAssembler textboxFormElementBuilderFactoryAssembler() {
			TextboxFormElementBuilderFactoryAssembler assembler = new TextboxFormElementBuilderFactoryAssembler();
			assembler.setMaximumSingleLineLength( 200 );
			return assembler;
		}
	}

	private static class Validators
	{
		public String noValidator;

		public int noValidatorNumber;

		@NotNull
		public String notNullValidator;

		@NotBlank
		public String notBlankValidator;

		@NotEmpty
		public String notEmptyValidator;

		@Size(min = 5, max = 200)
		public String sizeValidator;

		@Size(max = 201)
		public String sizeForMultiLineValidator;

		@Length(min = 1, max = 10)
		public String lengthValidator;

		@NotBlank
		@Size(max = 50)
		public String combinedValidator;
	}
}
