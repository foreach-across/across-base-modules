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
package com.foreach.across.modules.entity.query;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction layer for *simple* query construction.  An EntityQuery is a simple structure that has the support classes
 * to be constructed from maps, de-serialized to and from JSON and later converted to specific query structures like
 * JPA or QueryDSL.
 * <p/>
 * An EntityQuery is limited in what it supports because it provides a common denominator for different query types.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.query.jpa.EntityQueryJpaUtils
 * @see com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslUtils
 */
public class EntityQuery implements EntityQueryExpression
{
	private EntityQueryOps operand = EntityQueryOps.AND;
	private List<EntityQueryExpression> expressions = new ArrayList<>();

	public EntityQuery() {
	}

	public EntityQuery( EntityQueryOps operand ) {
		setOperand( operand );
	}

	public void add( EntityQueryExpression expression ) {
		expressions.add( expression );
	}

	public EntityQueryOps getOperand() {
		return operand;
	}

	public void setOperand( EntityQueryOps operand ) {
		Assert.notNull( operand );
		if ( operand != EntityQueryOps.AND && operand != EntityQueryOps.OR ) {
			throw new IllegalArgumentException( "EntityQuery operand type must be either AND or OR" );
		}
		this.operand = operand;
	}

	public List<EntityQueryExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions( List<EntityQueryExpression> expressions ) {
		Assert.notNull( expressions );
		this.expressions = expressions;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		EntityQuery that = (EntityQuery) o;

		if ( expressions != null ? !expressions.equals( that.expressions ) : that.expressions != null ) {
			return false;
		}
		if ( operand != that.operand ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = operand != null ? operand.hashCode() : 0;
		result = 31 * result + ( expressions != null ? expressions.hashCode() : 0 );
		return result;
	}

	@Override
	public String toString() {
		return operand.toString( null, expressions.toArray() );
	}

	public static EntityQuery and( EntityQueryExpression... expressions ) {
		return create( EntityQueryOps.AND, expressions );
	}

	public static EntityQuery or( EntityQueryExpression... expressions ) {
		return create( EntityQueryOps.OR, expressions );
	}

	public static EntityQuery create( EntityQueryOps operand, EntityQueryExpression... expressions ) {
		EntityQuery query = new EntityQuery( operand );
		for ( EntityQueryExpression expression : expressions ) {
			query.add( expression );
		}
		return query;
	}
}
