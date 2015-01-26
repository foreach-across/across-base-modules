package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.core.convert.ConversionService;

public class TextFormElement extends OldFormElementSupport
{
	public TextFormElement( EntityMessageCodeResolver messageCodeResolver,
	                        ConversionService conversionService,
	                        EntityPropertyDescriptor descriptor ) {
		super( messageCodeResolver, conversionService, descriptor, "text" );
	}
}
