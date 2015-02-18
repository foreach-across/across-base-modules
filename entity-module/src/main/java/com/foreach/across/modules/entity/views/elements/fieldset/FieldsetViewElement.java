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
public class FieldsetViewElement extends ViewElements implements ViewElement
{
	public static final String TYPE = CommonViewElements.FIELDSET;

	private String name, label, customTemplate;
	private ValuePrinter valuePrinter;

	private boolean field;

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
	public boolean isField() {
		return field;
	}

	public void setField( boolean field ) {
		this.field = field;
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
