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
package com.foreach.across.modules.entity.views.properties;

/**
 * @author Arne Vandamme
 */
public interface PrintablePropertyView
{
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

	Object value( Object entity );

	String print( Object entity );
}
