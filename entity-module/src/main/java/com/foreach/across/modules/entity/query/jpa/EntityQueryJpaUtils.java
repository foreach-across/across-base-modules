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
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public abstract class EntityQueryJpaUtils
{
	private EntityQueryJpaUtils() {
	}

	public static <V> Specification<V> toSpecification( final EntityQuery query ) {
		return new Specification<V>()
		{
			@Override
			public Predicate toPredicate( Root<V> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb ) {
				return EntityQueryJpaUtils.buildPredicate( query, root, cb );
			}
		};
	}

	private static <V> Predicate buildPredicate( EntityQueryExpression expression, Root<V> root, CriteriaBuilder cb ) {
		if ( expression instanceof EntityQueryCondition ) {
			return buildConditionPredicate( (EntityQueryCondition) expression, root, cb );
		}
		else {
			return buildQueryPredicate( (EntityQuery) expression, root, cb );
		}
	}

	private static <V> Predicate buildConditionPredicate( EntityQueryCondition condition,
	                                                      Root<V> root,
	                                                      CriteriaBuilder cb ) {
		switch ( condition.getOperand() ) {
			case EQ:
				return cb.equal( root.get( condition.getProperty() ), condition.getFirstArgument() );
			case NEQ:
				return cb.notEqual( root.get( condition.getProperty() ), condition.getFirstArgument() );
			case CONTAINS:
				Expression<Collection> collection = root.get( condition.getProperty() );
				return cb.isMember( condition.getFirstArgument(), collection );
		}

		throw new IllegalArgumentException( "Unsupported operand for JPA query: " + condition.getOperand() );
	}

	private static <V> Predicate buildQueryPredicate( EntityQuery query, Root<V> root, CriteriaBuilder cb ) {
		List<Predicate> predicates = new ArrayList<>();

		for ( EntityQueryExpression expression : query.getExpressions() ) {
			predicates.add( buildPredicate( expression, root, cb ) );
		}

		return query.getOperand() == EntityQueryOps.AND
				? cb.and( predicates.toArray( new Predicate[predicates.size()] ) )
				: cb.or( predicates.toArray( new Predicate[predicates.size()] ) );
	}
}
