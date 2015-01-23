package com.foreach.across.modules.entity.views.form;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.core.convert.ConversionService;

public class TextboxFormElement extends FormElementSupport
{
	public TextboxFormElement( EntityMessageCodeResolver messageCodeResolver,
	                           ConversionService conversionService,
	                           EntityPropertyDescriptor descriptor ) {
		super( messageCodeResolver, conversionService, descriptor, "textbox" );
	}

	public String getLabel() {
		return getDisplayName();
	}
}
