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
package com.foreach.across.modules.entity.views.elements.form.select;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEnumSelectOptionGenerator
{
	static enum EnumWithoutName
	{
		ValueOne,
		IN_BUSINESS
	}

	@Test
	public void optionsWithoutName() {
		MessageSource messageSource = new ResourceBundleMessageSource();
		EntityMessageCodeResolver codeResolver = new EntityMessageCodeResolver();
		codeResolver.setMessageSource( messageSource );

		EnumSelectOptionGenerator generator = new EnumSelectOptionGenerator( EnumWithoutName.class );

		List<SelectOption> options = new ArrayList<>( generator.generateOptions( codeResolver ) );
		assertEquals( 2, options.size() );

		assertOption( options.get( 0 ), "Value one", EnumWithoutName.ValueOne );
		assertOption( options.get( 1 ), "In business", EnumWithoutName.IN_BUSINESS );
	}

	@Test
	public void optionsCanBeSorted() throws Exception {
		MessageSource messageSource = new ResourceBundleMessageSource();
		EntityMessageCodeResolver codeResolver = new EntityMessageCodeResolver();
		codeResolver.setMessageSource( messageSource );

		EnumSelectOptionGenerator generator = new EnumSelectOptionGenerator( EnumWithoutName.class );
		generator.setShouldBeSorted( true );

		List<SelectOption> options = new ArrayList<>( generator.generateOptions( codeResolver ) );
		assertEquals( 2, options.size() );

		assertOption( options.get( 0 ), "In business", EnumWithoutName.IN_BUSINESS );
		assertOption( options.get( 1 ), "Value one", EnumWithoutName.ValueOne );
	}

	@Test
	public void optionsWithMessageCodes() {
		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.getMessageWithFallback( "enums.EnumWithoutName.ValueOne", "Value one" ) )
				.thenReturn( "Optie 1" );
		when( codeResolver.getMessageWithFallback( "enums.EnumWithoutName.IN_BUSINESS", "In business" ) )
				.thenReturn( "Optie 2" );

		EnumSelectOptionGenerator generator = new EnumSelectOptionGenerator( EnumWithoutName.class );

		List<SelectOption> options = new ArrayList<>( generator.generateOptions( codeResolver ) );
		assertEquals( 2, options.size() );

		assertOption( options.get( 0 ), "Optie 1", EnumWithoutName.ValueOne );
		assertOption( options.get( 1 ), "Optie 2", EnumWithoutName.IN_BUSINESS );
	}

	private void assertOption( SelectOption option, String label, Object value ) {
		assertEquals( value, option.getRawValue() );
		assertEquals( label, option.getLabel() );
	}
}
