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
package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.forms.elements.CommonFormElements;

/**
 * @author Arne Vandamme
 */
public class CommonFormElementTypeLookupStrategy implements FormElementTypeLookupStrategy
{
	@Override
	public String findElementType( EntityConfiguration entityConfiguration, EntityPropertyDescriptor descriptor ) {
		if ( descriptor.isHidden() ) {
			return CommonFormElements.HIDDEN;
		}

		if ( descriptor.isWritable() ) {
			return CommonFormElements.TEXTBOX;
		}

		return null;
	}
}
