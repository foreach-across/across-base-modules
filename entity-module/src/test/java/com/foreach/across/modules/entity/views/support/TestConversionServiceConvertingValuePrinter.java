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
package com.foreach.across.modules.entity.views.support;

import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@SuppressWarnings("unchecked")
public class TestConversionServiceConvertingValuePrinter
{
	@Test
	public void valueFetcherIsUsedForTheValue() {
		ValueFetcher valueFetcher = mock( ValueFetcher.class );
		ConversionServiceConvertingValuePrinter valuePrinter
				= new ConversionServiceConvertingValuePrinter( valueFetcher, null );

		when( valueFetcher.getValue( "test" ) ).thenReturn( 123 );

		assertEquals( 123, valuePrinter.getValue( "test" ) );
	}

	@Test
	public void conversionServiceIsCalledForConverting() {
		ValueFetcher valueFetcher = mock( ValueFetcher.class );
		ConversionService conversionService = mock( ConversionService.class );

		ConversionServiceConvertingValuePrinter valuePrinter
				= new ConversionServiceConvertingValuePrinter( valueFetcher, conversionService );

		when( conversionService.convert( 123, String.class ) ).thenReturn( "converted" );
		when( valueFetcher.getValue( "test" ) ).thenReturn( 123 );

		assertEquals( 123, valuePrinter.getValue( "test" ) );
		assertEquals( "converted", valuePrinter.print( "test" ) );
	}
}
