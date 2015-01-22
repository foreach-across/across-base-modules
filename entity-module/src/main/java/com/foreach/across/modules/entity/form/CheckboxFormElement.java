package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.core.convert.ConversionService;

public class CheckboxFormElement extends FormElementSupport
{
	public CheckboxFormElement( EntityMessageCodeResolver messageCodeResolver,
	                            ConversionService conversionService,
	                            EntityPropertyDescriptor descriptor ) {
		super( messageCodeResolver, conversionService, descriptor, "checkbox" );
	}

	public String getLabel() {
		return getDisplayName();
	}
}
