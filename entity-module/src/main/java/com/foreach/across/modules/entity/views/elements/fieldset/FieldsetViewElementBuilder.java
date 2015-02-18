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

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilder;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderContext;
import com.foreach.across.modules.entity.views.elements.form.dependencies.Qualifiers;
import com.foreach.across.modules.entity.views.support.ValuePrinter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class FieldsetViewElementBuilder implements ViewElementBuilder<FieldsetViewElement>
{
	private String name, label, labelCode, customTemplate;

	private EntityMessageCodeResolver messageCodeResolver;
	private ValuePrinter valuePrinter;

	private Collection<String> properties = new ArrayList<>();

	private Map<String, Qualifiers> dependencies = new HashMap<>();

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

	public Collection<String> getProperties() {
		return properties;
	}

	public void setProperties( Collection<String> properties ) {
		this.properties = properties;
	}

	public Map<String, Qualifiers> getDependencies() {
		return dependencies;
	}

	public void setDependencies( Map<String, Qualifiers> dependencies ) {
		this.dependencies = dependencies;
	}

	@Override
	public FieldsetViewElement createViewElement( ViewElementBuilderContext builderContext ) {
		// Create the fieldset element
		FieldsetViewElement fieldset = new FieldsetViewElement();
		BeanUtils.copyProperties( this, fieldset, "label" );
		fieldset.setLabel( resolve( getLabelCode(), getLabel() ) );

		// Add all children
		for ( String property : properties ) {
			ViewElementBuilder builder = builderContext.getBuilder( property );
			builder.setMessageCodeResolver( messageCodeResolver );

			fieldset.add( builder.createViewElement( builderContext ) );
		}

		return fieldset;
	}

	protected String resolve( String code, String defaultMessage ) {
		if ( messageCodeResolver != null && code != null ) {
			return messageCodeResolver.getMessageWithFallback( code, defaultMessage );
		}

		return defaultMessage;
	}
}
