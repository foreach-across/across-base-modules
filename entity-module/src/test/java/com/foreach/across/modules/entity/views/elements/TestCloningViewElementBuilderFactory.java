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
package com.foreach.across.modules.entity.views.elements;

import com.foreach.across.modules.entity.views.elements.form.textbox.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
public class TestCloningViewElementBuilderFactory
{
	@Test
	public void emptyInstanceIsReturnedWithoutTemplate() {
		ViewElementBuilderFactory<TextboxFormElementBuilder> factory
				= new CloningViewElementBuilderFactory<>( TextboxFormElementBuilder.class );

		TextboxFormElementBuilder builder = factory.createBuilder();
		assertNotNull( builder );
		assertEquals( new TextboxFormElementBuilder(), builder );
	}

	@Test
	public void templateIsDuplicated() {
		CloningViewElementBuilderFactory<TextboxFormElementBuilder> factory
				= new CloningViewElementBuilderFactory<>( TextboxFormElementBuilder.class );

		ValuePrinter valuePrinter = mock( ValuePrinter.class );

		TextboxFormElementBuilder template = new TextboxFormElementBuilder();
		template.setName( "someName" );
		template.setLabel( "display label" );
		template.setLabelCode( "hobbahobba" );
		template.setCustomTemplate( "th/test" );
		template.setMaxLength( 123 );
		template.setValuePrinter( valuePrinter );

		factory.setBuilderTemplate( template );

		TextboxFormElementBuilder builder = factory.createBuilder();
		assertNotNull( builder );
		assertEquals( template, builder );
		assertNotSame( template, builder );
	}
}
