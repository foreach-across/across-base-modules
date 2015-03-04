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
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.*;
import com.mysema.query.types.path.PathBuilder;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

/**
 * @author Arne Vandamme
 */
public abstract class EntityQueryQueryDslUtils
{
	private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

	private EntityQueryQueryDslUtils() {
	}

	public static <V> Predicate toPredicate( EntityQuery query, EntityConfiguration entityConfiguration ) {
		return toPredicate( query, entityConfiguration.getEntityType(), entityConfiguration.getName() );
	}

	public static <V> Predicate toPredicate( EntityQuery query, Class<V> entityType ) {
		return toPredicate( query, DEFAULT_ENTITY_PATH_RESOLVER.createPath( entityType ) );
	}

	public static <V> Predicate toPredicate( EntityQuery query, EntityPath<V> rootPath ) {
		return toPredicate( query, rootPath.getType(), rootPath.getMetadata().getName() );
	}

	public static <V> Predicate toPredicate( EntityQuery query, Class<V> entityType, String root ) {
		PathBuilder pathBuilder = new PathBuilder<>( entityType, root );
		return buildQueryPredicate( query, pathBuilder );
	}

	private static Predicate buildPredicate( EntityQueryExpression expression, PathBuilder pathBuilder ) {
		if ( expression instanceof EntityQueryCondition ) {
			return buildConditionPredicate( (EntityQueryCondition) expression, pathBuilder );
		}
		else {
			return buildQueryPredicate( (EntityQuery) expression, pathBuilder );
		}
	}

	private static Predicate buildConditionPredicate( EntityQueryCondition condition, PathBuilder pathBuilder ) {
		switch ( condition.getOperand() ) {
			case EQ: {
				Path property = pathBuilder.get( condition.getProperty() );
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.EQ, property, constant );
			}
			case NEQ: {
				Path property = pathBuilder.get( condition.getProperty() );
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.NE, property, constant );
			}
			case CONTAINS: {
				Path property = pathBuilder.get( condition.getProperty() );
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.CONTAINS_VALUE, property, constant );
			}
		}

		throw new IllegalArgumentException( "Unsupported operand for JPA query: " + condition.getOperand() );
	}

	private static Predicate buildQueryPredicate( EntityQuery query, PathBuilder pathBuilder ) {
		BooleanBuilder builder = new BooleanBuilder();

		for ( EntityQueryExpression expression : query.getExpressions() ) {
			if ( query.getOperand() == EntityQueryOps.AND ) {
				builder.and( buildPredicate( expression, pathBuilder ) );
			}
			else {
				builder.or( buildPredicate( expression, pathBuilder ) );
			}
		}

		return builder;
	}
}
