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
package com.foreach.across.modules.entity.query.jpa;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryPageFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Arne Vandamme
 */
public class EntityQueryJpaPageFetcher implements EntityQueryPageFetcher
{
	private final JpaSpecificationExecutor jpaSpecificationExecutor;

	public EntityQueryJpaPageFetcher( JpaSpecificationExecutor jpaSpecificationExecutor ) {
		this.jpaSpecificationExecutor = jpaSpecificationExecutor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page fetchPage( EntityQuery query, Pageable pageable ) {
		if ( pageable == null ) {
			return new PageImpl( jpaSpecificationExecutor.findAll( EntityQueryJpaUtils.toSpecification( query ) ) );
		}

		return jpaSpecificationExecutor.findAll( EntityQueryJpaUtils.toSpecification( query ), pageable );
	}
}
