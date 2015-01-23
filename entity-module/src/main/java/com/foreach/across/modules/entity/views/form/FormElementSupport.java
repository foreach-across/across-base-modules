package com.foreach.across.modules.entity.views.form;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.properties.ConversionServicePrintablePropertyView;
import org.springframework.core.convert.ConversionService;

public class FormElementSupport extends ConversionServicePrintablePropertyView implements FormElement
{
	private String elementType;

	protected FormElementSupport( EntityMessageCodeResolver messageCodeResolver,
	                              ConversionService conversionService,
	                              EntityPropertyDescriptor descriptor,
	                              String elementType ) {
		super( messageCodeResolver, conversionService, descriptor );
		this.elementType = elementType;
	}

	@Override
	public String getElementType() {
		return elementType;
	}
}
