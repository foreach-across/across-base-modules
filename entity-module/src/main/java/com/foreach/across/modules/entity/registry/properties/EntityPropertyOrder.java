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
package com.foreach.across.modules.entity.registry.properties;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Undefined entities are assumed to have an order index of 0.
 */
public class EntityPropertyOrder implements Comparator<EntityPropertyDescriptor>
{
	private final Map<String, Integer> order;

	public EntityPropertyOrder( String... ordered ) {
		order = new LinkedHashMap<>();

		for ( int i = 0; i < ordered.length; i++ ) {
			order.put( ordered[i], -ordered.length + i );
		}
	}

	public EntityPropertyOrder( Collection<String> ordered ) {
		order = new LinkedHashMap<>();

		int i = 0;
		for ( String item : ordered ) {
			order.put( item, ++i );
		}
	}

	public EntityPropertyOrder( Map<String, Integer> order ) {
		Assert.notNull( order );
		this.order = order;
	}

	protected Map<String, Integer> getOrder() {
		return order;
	}

	@Override
	public int compare( EntityPropertyDescriptor left, EntityPropertyDescriptor right ) {
		Integer orderLeft = applyDefault( order.get( left.getName() ) );
		Integer orderRight = applyDefault( order.get( right.getName() ) );

		return orderLeft.compareTo( orderRight );
	}

	private Integer applyDefault( Integer fixed ) {
		return fixed != null ? fixed : 0;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		EntityPropertyOrder that = (EntityPropertyOrder) o;

		if ( order != null ? !order.equals( that.order ) : that.order != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return order != null ? order.hashCode() : 0;
	}

	public static Comparator<EntityPropertyDescriptor> composite( final Comparator<EntityPropertyDescriptor> first,
	                                                              final Comparator<EntityPropertyDescriptor> fallback ) {
		return new Comparator<EntityPropertyDescriptor>()
		{
			@Override
			public int compare( EntityPropertyDescriptor left, EntityPropertyDescriptor right ) {
				int comparison = first.compare( left, right );

				return comparison == 0 ? fallback.compare( left, right ) : comparison;
			}
		};
	}
}
