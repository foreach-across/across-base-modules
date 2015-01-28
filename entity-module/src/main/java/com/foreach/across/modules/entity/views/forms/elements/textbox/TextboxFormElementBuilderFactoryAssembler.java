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

import com.foreach.across.modules.entity.views.forms.elements.CommonFormElements;
import com.foreach.across.modules.entity.views.forms.elements.FormElementBuilderFactoryAssemblerSupport;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;

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
	protected void handleConstraint( TextboxFormElementBuilder template,
	                                 Class<? extends Annotation> type,
	                                 ConstraintDescriptor constraint ) {
		if ( Size.class.equals( type ) || Length.class.equals( type ) ) {
			Integer max = (Integer) constraint.getAttributes().get( "max" );

			if ( max != Integer.MAX_VALUE ) {
				template.setMaxLength( max );
			}
		}
	}
}
