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
package com.foreach.across.modules.entity.views.elements.form.select;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementSupport;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public class SelectFormElement extends FormElementSupport
{
	public static final String TYPE = CommonViewElements.SELECT;

	private Collection<SelectOption> options;

	public SelectFormElement() {
		super( TYPE );
	}

	@Override
	public void setElementType( String elementType ) {
		super.setElementType( elementType );
	}

	public Collection<SelectOption> getOptions() {
		return options;
	}

	public void setOptions( Collection<SelectOption> options ) {
		this.options = options;
	}

}
