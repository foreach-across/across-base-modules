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
package com.foreach.across.modules.entity.views.fetchers;

import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fetches the collection as a property of the original entity passed in.
 *
 * @author Arne Vandamme
 */
public class AssociationPropertyListViewPageFetcher implements EntityListViewPageFetcher<ViewCreationContext>
{
	private final String propertyName;

	public AssociationPropertyListViewPageFetcher( String propertyName ) {
		this.propertyName = propertyName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page fetchPage( ViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
		BeanWrapper beanWrapper = new BeanWrapperImpl( model.getParentEntity() );
		Object itemsValue = beanWrapper.getPropertyValue( propertyName );

		List items = Collections.emptyList();

		if ( itemsValue != null ) {
			if ( itemsValue instanceof Collection ) {
				items = new ArrayList( (Collection) itemsValue );
			}
			else {
				throw new IllegalArgumentException(
						"Property " + propertyName + " was expected to be a collection type but is a " + itemsValue
								.getClass().getName() );
			}
		}

		return new PageImpl( items, pageable, items.size() );
	}
}
