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
package com.foreach.across.modules.entity.views.forms.elements.textbox;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.forms.elements.CommonFormElements;
import com.foreach.across.modules.entity.views.forms.elements.FormElementBuilderFactoryAssemblerSupport;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

/**
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilderFactoryAssembler
		extends FormElementBuilderFactoryAssemblerSupport<TextboxFormElementBuilder>
{
	public TextboxFormElementBuilderFactoryAssembler() {
		super( TextboxFormElementBuilder.class, CommonFormElements.TEXTBOX );
	}

	@Override
	protected void assembleTemplate( EntityConfiguration entityConfiguration,
	                                 EntityPropertyRegistry registry,
	                                 EntityPropertyDescriptor descriptor,
	                                 TextboxFormElementBuilder template ) {
		PropertyDescriptor validationDescriptor = descriptor.getAttribute( PropertyDescriptor.class );

		if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
			for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
				handleConstraint( template, constraint );
			}
		}
	}

	private void handleConstraint( TextboxFormElementBuilder template, ConstraintDescriptor constraint ) {
		Class<?> type = constraint.getAnnotation().annotationType();

		if ( Size.class.equals( type ) || Length.class.equals( type ) ) {
			Integer max = (Integer) constraint.getAttributes().get( "max" );

			if ( max != Integer.MAX_VALUE ) {
				template.setMaxLength( max );
			}
		}
		else if ( NotBlank.class.equals( type ) || NotNull.class.equals( type ) || NotEmpty.class.equals( type ) ) {
			template.setRequired( true );
		}
	}
}
