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
package com.foreach.across.modules.entity.views.forms.elements;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.forms.FormElementBuilder;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * @author Arne Vandamme
 */
public abstract class FormElementBuilderSupport<T extends FormElementSupport> implements FormElementBuilder<T>
{
	private final Class<T> builderClass;

	private boolean required;
	private String name, label, labelCode, customTemplate;

	private EntityMessageCodeResolver messageCodeResolver;
	private ValuePrinter valuePrinter;

	protected FormElementBuilderSupport( Class<T> builderClass ) {
		this.builderClass = builderClass;
	}

	public EntityMessageCodeResolver getMessageCodeResolver() {
		return messageCodeResolver;
	}

	@Override
	public void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	public ValuePrinter getValuePrinter() {
		return valuePrinter;
	}

	public void setValuePrinter( ValuePrinter valuePrinter ) {
		this.valuePrinter = valuePrinter;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public String getLabelCode() {
		return labelCode;
	}

	public void setLabelCode( String labelCode ) {
		this.labelCode = labelCode;
	}

	public String getCustomTemplate() {
		return customTemplate;
	}

	public void setCustomTemplate( String customTemplate ) {
		this.customTemplate = customTemplate;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired( boolean required ) {
		this.required = required;
	}

	protected String resolve( String code, String defaultMessage ) {
		if ( messageCodeResolver != null && code != null ) {
			return messageCodeResolver.getMessageWithFallback( code, defaultMessage );
		}

		return defaultMessage;
	}

	@Override
	public T createFormElement() {
		T element = newInstance();
		BeanUtils.copyProperties( this, element, "label" );

		element.setLabel( resolve( getLabelCode(), getLabel() ) );

		buildCustomProperties( element );

		return element;
	}

	protected void buildCustomProperties( T element ) {
	}

	protected T newInstance() {
		try {
			return builderClass.newInstance();
		}
		catch ( IllegalAccessException | InstantiationException iae ) {
			throw new RuntimeException(
					getClass().getSimpleName() + " requires the template to have a parameterless constructor", iae
			);
		}
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof FormElementBuilderSupport ) ) {
			return false;
		}

		FormElementBuilderSupport that = (FormElementBuilderSupport) o;

		return Objects.equals( name, that.name )
				&& Objects.equals( customTemplate, that.customTemplate )
				&& Objects.equals( label, that.label )
				&& Objects.equals( labelCode, that.labelCode )
				&& Objects.equals( messageCodeResolver, that.messageCodeResolver )
				&& Objects.equals( valuePrinter, that.valuePrinter )
				&& Objects.equals( required, that.required );
	}

	@Override
	public int hashCode() {
		return Objects.hash( name, customTemplate, label, labelCode, messageCodeResolver, valuePrinter, required );
	}

}
