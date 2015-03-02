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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.*;
import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public class AssociationJpaSpecificationListViewPageFetcher implements EntityListViewPageFetcher<ViewCreationContext>
{
	private final String propertyName;
	private final JpaSpecificationExecutor jpaSpecificationExecutor;

	public AssociationJpaSpecificationListViewPageFetcher( String propertyName,
	                                                       JpaSpecificationExecutor jpaSpecificationExecutor ) {
		this.propertyName = propertyName;
		this.jpaSpecificationExecutor = jpaSpecificationExecutor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page fetchPage( ViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
		final Object entity = model.getEntity();

		Specification s = new Specification<Object>()
		{
			@Override
			public Predicate toPredicate( Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder cb ) {
				Expression<Collection> candidates = root.get( propertyName );
				return cb.isMember( entity, candidates );
			}
		};

		return jpaSpecificationExecutor.findAll( s, pageable );
	}
}
