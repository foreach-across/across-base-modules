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
package com.foreach.across.modules.entity.views.elements.table;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.elements.ViewElement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class SortableTableHeaderCellProcessor implements TableHeaderCellProcessor
{
	private Collection<String> sortableProperties;
	private Map<ViewElement, EntityPropertyDescriptor> viewElementDescriptorMap = Collections.emptyMap();

	public void setSortableProperties( Collection<String> sortableProperties ) {
		this.sortableProperties = sortableProperties;
	}

	public Collection<String> getSortableProperties() {
		return sortableProperties;
	}

	public void setViewElementDescriptorMap( Map<ViewElement, EntityPropertyDescriptor> viewElementDescriptorMap ) {
		this.viewElementDescriptorMap = viewElementDescriptorMap;
	}

	public Map<ViewElement, EntityPropertyDescriptor> getViewElementDescriptorMap() {
		return viewElementDescriptorMap;
	}

	@Override
	public Map<String, String> attributes( ViewElement column ) {
		Map<String, String> attributes = new HashMap<>();

		String sortableProperty = determineSortableProperty( column );

		if ( sortableProperty != null ) {
			attributes.put( "class", "sortable" );
			attributes.put( "data-tbl-sort-property", sortableProperty );
			attributes.put( "data-tbl", "entity-list" );
		}

		return attributes;
	}

	private String determineSortableProperty( ViewElement viewElement ) {
		EntityPropertyDescriptor descriptor = viewElementDescriptorMap.get( viewElement );

		if ( descriptor != null ) {
			String sortableProperty = descriptor.getAttribute( EntityAttributes.SORTABLE_PROPERTY, String.class );

			if ( sortableProperties != null && !sortableProperties.contains( descriptor.getName() ) ) {
				sortableProperty = null;
			}

			return sortableProperty;
		}

		return null;
	}
}
