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

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;

/**
 * Base interface to create a single {@link ViewElement} instance.
 *
 * @author Arne Vandamme
 */
public interface ViewElementBuilder<T extends ViewElement>
{
	/**
	 * The resolver that should be used for all message codes once the view element
	 * is being created.
	 *
	 * @param messageCodeResolver instance, can be null if codes should not be resolved.
	 */
	void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver );

	/**
	 * Builds the actual element.  This will resolve all configured messages - if any.
	 *
	 * @param builderContext that provides access to other builders
	 * @return instance to render the element.
	 */
	T createViewElement( ViewElementBuilderContext builderContext );
}
