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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author Arne Vandamme
 */
public class ConversionServiceViewElement implements ViewElement
{
	private final EntityMessageCodeResolver messageCodeResolver;
	private final EntityPropertyDescriptor descriptor;
	private final ConversionService conversionService;
	private static final TypeDescriptor targetTypeDescriptor = TypeDescriptor.forObject( "" );

	private String customTemplate;

	public ConversionServiceViewElement(
			EntityMessageCodeResolver messageCodeResolver,
			ConversionService conversionService,
			EntityPropertyDescriptor descriptor ) {
		this.messageCodeResolver = messageCodeResolver;
		this.descriptor = descriptor;
		this.conversionService = conversionService;
	}

	@Override
	public boolean isField() {
		return false;
	}

	@Override
	public String getElementType() {
		return "text";
	}

	@Override
	public String getName() {
		return descriptor.getName();
	}

	@Override
	public String getLabel() {
		return messageCodeResolver.getPropertyDisplayName( descriptor );
	}

	public String getCustomTemplate() {
		return customTemplate;
	}

	public void setCustomTemplate( String customTemplate ) {
		this.customTemplate = customTemplate;
	}

	@Override
	public Object value( Object entity ) {
		return descriptor.getValueFetcher().getValue( entity );
	}

	@Override
	public String print( Object entity ) {
		TypeDescriptor propertyTypeDescriptor = descriptor.getPropertyTypeDescriptor();
		if ( propertyTypeDescriptor != null ) {
			return (String) conversionService.convert( value( entity ), propertyTypeDescriptor, targetTypeDescriptor );
		}
		return conversionService.convert( value( entity ), String.class );
	}
}
