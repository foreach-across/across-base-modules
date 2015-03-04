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
package com.foreach.across.modules.entity.query.querydsl;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryPageFetcher;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.mysema.query.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * @author Arne Vandamme
 */
public class EntityQueryQueryDslPageFetcher implements EntityQueryPageFetcher
{
	private final QueryDslPredicateExecutor queryDslPredicateExecutor;
	private final EntityConfiguration entityConfiguration;

	public EntityQueryQueryDslPageFetcher( QueryDslPredicateExecutor queryDslPredicateExecutor,
	                                       EntityConfiguration entityConfiguration ) {
		this.queryDslPredicateExecutor = queryDslPredicateExecutor;
		this.entityConfiguration = entityConfiguration;
	}

	@Override
	public Page fetchPage( EntityQuery query, Pageable pageable ) {
		Predicate predicate = EntityQueryQueryDslUtils.toPredicate( query, entityConfiguration );

		if ( pageable == null ) {
			return EntityUtils.createPage( queryDslPredicateExecutor.findAll( predicate ) );
		}

		return queryDslPredicateExecutor.findAll( predicate, pageable );
	}

}
