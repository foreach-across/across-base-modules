package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.properties.ConversionServicePrintablePropertyView;
import org.springframework.core.convert.ConversionService;

public class OldFormElementSupport extends ConversionServicePrintablePropertyView implements FormElement
{
	private String elementType;

	protected OldFormElementSupport( EntityMessageCodeResolver messageCodeResolver,
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
