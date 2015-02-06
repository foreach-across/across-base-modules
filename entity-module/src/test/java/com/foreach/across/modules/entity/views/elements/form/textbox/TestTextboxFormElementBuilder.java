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

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestTextboxFormElementBuilder
{
	@Test
	public void labelCodeOnlyResolvedIfMessageCodeResolver() {
		ValuePrinter valuePrinter = mock( ValuePrinter.class  );

		TextboxFormElementBuilder builder = new TextboxFormElementBuilder();
		builder.setName( "name" );
		builder.setLabelCode( "somePropertyName" );
		builder.setLabel( "Property name" );
		builder.setCustomTemplate( "th/test/template" );
		builder.setMaxLength( 15 );
		builder.setValuePrinter( valuePrinter );

		TextboxFormElement element = builder.createViewElement( null );
		assertNotNull( element );
		assertEquals( "name", element.getName() );
		assertEquals( "Property name", element.getLabel() );
		assertEquals( "th/test/template", element.getCustomTemplate() );
		assertEquals( Integer.valueOf( 15 ), element.getMaxLength() );
		assertSame( valuePrinter, element.getValuePrinter() );

		EntityMessageCodeResolver resolver = mock( EntityMessageCodeResolver.class );
		when( resolver.getMessageWithFallback( "somePropertyName", "Property name" ) )
				.thenReturn( "Other property name" );

		builder.setMessageCodeResolver( resolver );
		builder.setMaxLength( null );
		builder.setCustomTemplate( null );

		element = builder.createViewElement( null );
		assertNotNull( element );
		assertEquals( "name", element.getName() );
		assertEquals( "Other property name", element.getLabel() );
		assertNull( element.getCustomTemplate() );
		assertNull( element.getMaxLength() );
		assertSame( valuePrinter, element.getValuePrinter() );
	}
}
