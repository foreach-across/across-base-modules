package com.foreach.across.modules.entity.views.properties;

import com.foreach.across.modules.entity.views.forms.FormElement;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

public class PrintablePropertyGroup implements FormElement
{
	private String elementType = "group";

	private String name, label, customTemplate;
	private ValuePrinter valuePrinter;

	private List<PrintablePropertyView> children = new LinkedList<>();

	@Override
	public String getElementType() {
		return elementType;
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

	public List<PrintablePropertyView> getChildren() {
		return children;
	}

	public void setChildren( List<PrintablePropertyView> children ) {
		this.children = children;
	}

	public boolean isEmpty() {
		return children == null || children.isEmpty();
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
