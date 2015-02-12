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
package com.foreach.across.modules.entity.registrars.repository.handlers;

import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Andy Somers
 */
@Component
public class ManyToOneAssociationJpaSpecificationExecutorListViewBuilder implements AssociationListViewBuilder
{
	@Override
	public boolean supports( Class<?> clazz, Repository repository ) {
		return clazz == ManyToOne.class && repository instanceof JpaSpecificationExecutor;
	}

	@Override
	public void handle( EntityListViewFactory viewFactory, Repository repository, final PersistentProperty property ) {
		final JpaSpecificationExecutor jpa = (JpaSpecificationExecutor) repository;
		viewFactory.setPageFetcher( new EntityListViewPageFetcher<WebViewCreationContext>()
		{
			@Override
			public Page fetchPage( WebViewCreationContext viewCreationContext,
			                       Pageable pageable,
			                       final EntityView model ) {
				Specification s = new Specification<Object>()
				{
					@Override
					public Predicate toPredicate( Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder cb ) {
						return cb.equal( root.get( property.getName() ), model.getEntity() );
					}
				};

				return jpa.findAll( s, pageable );
			}
		} );

	}
}
