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
package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.forms.elements.textbox.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.views.forms.elements.textbox.TextboxFormElementBuilderFactoryAssembler;
import com.foreach.across.modules.entity.views.support.ConversionServiceConvertingValuePrinter;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

	@Autowired
	private EntityPropertyDescriptor descriptor;

	private TextboxFormElementBuilder template;

	@Before
	public void before() {
		reset( entityConfiguration, registry, descriptor );
	}

	@Test
	public void createWithoutValidators() {
		when( entityConfiguration.getEntityMessageCodeResolver() ).thenReturn( mock(
				EntityMessageCodeResolver.class ) );

		when( descriptor.getName() ).thenReturn( "title" );
		when( descriptor.getDisplayName() ).thenReturn( "Title" );

		template = assemble();

		assertEquals( "title", template.getName() );
		assertEquals( "Title", template.getLabel() );
		assertEquals( "properties.title", template.getLabelCode() );
		assertNull( template.getCustomTemplate() );
		assertNull( template.getMaxLength() );
		assertNotNull( template.getMessageCodeResolver() );
		assertNotNull( template.getValuePrinter() );
		assertTrue( template.getValuePrinter() instanceof ConversionServiceConvertingValuePrinter );
	}

	private TextboxFormElementBuilder assemble() {
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
}
