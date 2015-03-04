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

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.query.EntityQueryPageFetcher;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Arne Vandamme
 */
public class AssociationListViewPageFetcher implements EntityListViewPageFetcher<ViewCreationContext>
{
	private final EntityPropertyDescriptor property;
	private final EntityQueryPageFetcher entityQueryPageFetcher;

	public AssociationListViewPageFetcher( EntityPropertyDescriptor property,
	                                       EntityQueryPageFetcher entityQueryPageFetcher ) {
		this.property = property;
		this.entityQueryPageFetcher = entityQueryPageFetcher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page fetchPage( ViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
		EntityQuery query = EntityQuery.and( buildEqualsOrContainsCondition( model.getParentEntity() ) );

		return entityQueryPageFetcher.fetchPage( query, pageable );
	}

	private EntityQueryCondition buildEqualsOrContainsCondition( Object value ) {
		return new EntityQueryCondition(
				property.getName(),
				property.getPropertyTypeDescriptor().isCollection() ? EntityQueryOps.CONTAINS : EntityQueryOps.EQ,
				value
		);
	}
}
