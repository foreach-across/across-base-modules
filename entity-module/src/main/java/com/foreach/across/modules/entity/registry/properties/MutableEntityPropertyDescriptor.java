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
package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.registry.support.WritableAttributes;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author Arne Vandamme
 */
public interface MutableEntityPropertyDescriptor extends EntityPropertyDescriptor, WritableAttributes
{
	void setDisplayName( String displayName );

	void setReadable( boolean readable );

	void setWritable( boolean writable );

	void setHidden( boolean hidden );

	void setPropertyType( Class<?> propertyType );

	void setPropertyTypeDescriptor( TypeDescriptor typeDescriptor );

	void setValueFetcher( ValueFetcher<?> valueFetcher );
}
