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

/**
 * Simple interface representing a displayable value for an entity.
 * The value is usually associated with something like a property, but not necessarily so.
 *
 * @author Arne Vandamme
 */
public interface ViewElement
{
	/**
	 * @return Type id of this view element.
	 */
	String getElementType();

	/**
	 * @return Unique name of the property.
	 */
	String getName();

	/**
	 * @return Label to show for the property.
	 */
	String getLabel();

	/**
	 * @return Custom template to use when rendering this property (null for default template).
	 */
	String getCustomTemplate();

	/**
	 * If a view element represents a field, the name is expected to be resolvable on the command object.
	 *
	 * @return True if the view element represent a command object field.
	 */
	boolean isField();

	/**
	 * Retrieve the raw value of this view element for a given entity.
	 *
	 * @param entity Entity for which to retrieve the value.
	 * @return Value - can be null.
	 */
	Object value( Object entity );

	/**
	 * Print out the value for this view element.
	 *
	 * @param entity Entity for which to retrieve the value.
	 * @return String version of the value - can be null.
	 */
	String print( Object entity );
}
