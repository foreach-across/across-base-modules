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
package com.foreach.across.modules.entity.views.elements.form.checkbox;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementSupport;

/**
 * Represents a HTML "checkbox" input type.
 */
public class CheckboxFormElement extends FormElementSupport
{
	public static final String TYPE = CommonViewElements.CHECKBOX;

	public CheckboxFormElement() {
		super( TYPE );
	}

	public boolean isChecked( Object entity ) {
		Object booleanValue = value( entity );

		return booleanValue != null && booleanValue instanceof Boolean && (Boolean) booleanValue;
	}
}
