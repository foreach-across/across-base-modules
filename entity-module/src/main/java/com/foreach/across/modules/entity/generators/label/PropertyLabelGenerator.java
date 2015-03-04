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
package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.generators.EntityLabelGenerator;
import com.foreach.across.modules.entity.util.EntityUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;

/**
 * Use the property value as a label.
 */
@Deprecated
public class PropertyLabelGenerator implements EntityLabelGenerator
{
	private PropertyDescriptor propertyDescriptor;

	public PropertyLabelGenerator( PropertyDescriptor propertyDescriptor ) {
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	public String getLabel( Object entity ) {
		Object value = EntityUtils.getPropertyValue( propertyDescriptor, entity );
		return value != null ? value.toString() : "";
	}

	public static PropertyLabelGenerator forProperty( Class<?> clazz, String propertyName ) {
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor( clazz, propertyName );

		if ( descriptor != null ) {
			return new PropertyLabelGenerator( descriptor );
		}

		return null;
	}
}
