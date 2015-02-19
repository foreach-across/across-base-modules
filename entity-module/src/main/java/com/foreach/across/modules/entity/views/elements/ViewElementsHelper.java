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
package com.foreach.across.modules.entity.views.elements;

import java.util.Collections;

/**
 * Utility object to help identifying {@link com.foreach.across.modules.entity.views.elements.ViewElement} types.
 * By default exposed as a Thymeleaf expression object under key <strong>#elements</strong>.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.thymeleaf.EntityModuleDialect
 */
public class ViewElementsHelper
{
	/**
	 * @return True if element is a {@link com.foreach.across.modules.entity.views.elements.ViewElements} instance.
	 */
	public boolean isCollection( ViewElement element ) {
		return element instanceof ViewElements;
	}

	/**
	 * @return True if element is a {@link com.foreach.across.modules.entity.views.elements.ViewElements} instance
	 * that contains at least one item.
	 */
	public boolean hasChildren( ViewElement element ) {
		return isCollection( element ) && !( (ViewElements) element ).isEmpty();
	}

	/**
	 * @return Single element wrapped as a singleton.
	 */
	public Iterable<ViewElement> asCollection( ViewElement element ) {
		return Collections.singleton( element );
	}
}
