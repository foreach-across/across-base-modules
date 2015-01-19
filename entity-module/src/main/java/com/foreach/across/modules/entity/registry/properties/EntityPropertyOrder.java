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
