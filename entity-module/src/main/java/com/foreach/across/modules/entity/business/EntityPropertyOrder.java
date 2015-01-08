package com.foreach.across.modules.entity.business;

import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EntityPropertyOrder implements Comparator<EntityPropertyDescriptor>
{
	private final Map<String, Integer> order;

	public EntityPropertyOrder( String... ordered ) {
		order = new HashMap<>();

		for ( int i = 0; i < ordered.length; i++ ) {
			order.put( ordered[i], i + 1 );
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
		return fixed != null ? fixed : Ordered.LOWEST_PRECEDENCE;
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
