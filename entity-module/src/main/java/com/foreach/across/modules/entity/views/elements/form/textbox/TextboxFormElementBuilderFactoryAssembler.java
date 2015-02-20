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
package com.foreach.across.modules.entity.views.elements.form.textbox;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementBuilderFactoryAssemblerSupport;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;

/**
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilderFactoryAssembler
		extends FormElementBuilderFactoryAssemblerSupport<TextboxFormElementBuilder>
{
	private int maximumSingleLineLength = 300;

	public TextboxFormElementBuilderFactoryAssembler() {
		super( TextboxFormElementBuilder.class, CommonViewElements.TEXTBOX );
	}

	public void setMaximumSingleLineLength( int maximumSingleLineLength ) {
		this.maximumSingleLineLength = maximumSingleLineLength;
	}

	@Override
	protected void assembleTemplate( EntityConfiguration entityConfiguration,
	                                 EntityPropertyRegistry registry,
	                                 EntityPropertyDescriptor descriptor,
	                                 TextboxFormElementBuilder template ) {
		if ( !descriptor.getPropertyType().equals( String.class ) ) {
			template.setMultiLine( false );
		}
	}

	@Override
	protected void handleConstraint( TextboxFormElementBuilder template,
	                                 Class<? extends Annotation> type,
	                                 ConstraintDescriptor constraint ) {
		if ( Size.class.equals( type ) || Length.class.equals( type ) ) {
			Integer max = (Integer) constraint.getAttributes().get( "max" );

			if ( max != Integer.MAX_VALUE ) {
				template.setMaxLength( max );
				template.setMultiLine( max > maximumSingleLineLength );
			}
		}
		if ( URL.class.equals( type ) ) {
			template.setUrl( true );
		}
	}
}
