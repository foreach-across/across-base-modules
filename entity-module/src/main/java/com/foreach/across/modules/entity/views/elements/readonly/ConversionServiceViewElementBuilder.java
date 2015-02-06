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
package com.foreach.across.modules.entity.views.elements.readonly;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ConversionServiceViewElement;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilder;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderContext;
import org.springframework.core.convert.ConversionService;

/**
 * @author Andy Somers
 */
public class ConversionServiceViewElementBuilder implements ViewElementBuilder<ConversionServiceViewElement>
{

	private EntityMessageCodeResolver messageCodeResolver;
	private ConversionService conversionService;
	private EntityPropertyDescriptor descriptor;

	public EntityMessageCodeResolver getMessageCodeResolver() {
		return messageCodeResolver;
	}

	@Override
	public void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	public EntityPropertyDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor( EntityPropertyDescriptor descriptor ) {
		this.descriptor = descriptor;
	}

	@Override
	public ConversionServiceViewElement createViewElement( ViewElementBuilderContext builderContext ) {
		return new ConversionServiceViewElement( messageCodeResolver, conversionService, descriptor );
	}
}
