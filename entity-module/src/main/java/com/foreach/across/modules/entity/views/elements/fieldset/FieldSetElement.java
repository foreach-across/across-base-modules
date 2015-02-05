package com.foreach.across.modules.entity.views.elements.fieldset;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.ViewElement;
import com.foreach.across.modules.entity.views.elements.ViewElements;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.springframework.util.Assert;

/**
 * Represents a described group of other {@link com.foreach.across.modules.entity.views.elements.ViewElement}
 * instances.
 */
public class FieldSetElement extends ViewElements implements ViewElement
{
	public static final String TYPE = CommonViewElements.FIELDSET;

	private String name, label, customTemplate;
	private ValuePrinter valuePrinter;

	@Override
	public String getElementType() {
		return TYPE;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	@Override
	public String getCustomTemplate() {
		return customTemplate;
	}

	public void setCustomTemplate( String customTemplate ) {
		this.customTemplate = customTemplate;
	}

	public ValuePrinter getValuePrinter() {
		return valuePrinter;
	}

	public void setValuePrinter( ValuePrinter valuePrinter ) {
		Assert.notNull( valuePrinter );
		this.valuePrinter = valuePrinter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object value( Object entity ) {
		return valuePrinter.getValue( entity );
	}

	@Override
	@SuppressWarnings("unchecked")
	public String print( Object entity ) {
		return valuePrinter.print( entity );
	}
}
