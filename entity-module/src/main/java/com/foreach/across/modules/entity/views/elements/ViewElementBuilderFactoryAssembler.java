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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;

/**
 * All naming aside, this is the base interface for constructing a
 * {@link ViewElementBuilderFactory} for a
 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor} instance.
 *
 * @author Arne Vandamme
 */
public interface ViewElementBuilderFactoryAssembler
{
	/**
	 * View element type that this assembler supports.  It can create builder factories for these types.
	 *
	 * @param viewElementType Unique type string.
	 * @return True if it can create a builder factory for this element type.
	 */
	boolean supports( String viewElementType );

	ViewElementBuilderFactory createBuilderFactory(
			EntityConfiguration entityConfiguration,
			EntityPropertyRegistry entityPropertyRegistry,
			EntityPropertyDescriptor propertyDescriptor
	);
}
