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

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Uses a separate {@link com.foreach.across.modules.entity.views.support.ValueFetcher} for getting
 * the value, and uses an attached {@link org.springframework.core.convert.ConversionService} to convert
 * the value to string.
 *
 * @author Arne Vandamme
 */
public class ConversionServiceConvertingValuePrinter<T> implements ValuePrinter<T>
{
	private final ValueFetcher<T> valueFetcher;
	private final ConversionService conversionService;
	private final TypeDescriptor sourceTypeDescriptor;
	private final static TypeDescriptor targetTypeDescriptor = TypeDescriptor.forObject( "" );

	public ConversionServiceConvertingValuePrinter( ValueFetcher<T> valueFetcher,
	                                                ConversionService conversionService ) {
		this( valueFetcher, null, conversionService );
	}

	public ConversionServiceConvertingValuePrinter( ValueFetcher<T> valueFetcher,
	                                                TypeDescriptor sourceTypeDescriptor,
	                                                ConversionService conversionService ) {
		this.valueFetcher = valueFetcher;
		this.conversionService = conversionService;
		this.sourceTypeDescriptor = sourceTypeDescriptor;
	}

	@Override
	public String print( T object ) {
		if ( sourceTypeDescriptor != null ) {
			return (String) conversionService.convert( getValue( object ), sourceTypeDescriptor, targetTypeDescriptor );
		}
		return conversionService.convert( getValue( object ), String.class );
	}

	@Override
	public Object getValue( T entity ) {
		return valueFetcher.getValue( entity );
	}
}
