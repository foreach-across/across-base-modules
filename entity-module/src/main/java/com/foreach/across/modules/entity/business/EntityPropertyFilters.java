package com.foreach.across.modules.entity.business;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public final class EntityPropertyFilters
{
	public static EntityPropertyFilter NoOp = new EntityPropertyFilter()
	{
		@Override
		public boolean include( EntityPropertyDescriptor descriptor ) {
			return true;
		}
	};

	private EntityPropertyFilters() {
	}

	public static EntityPropertyFilter include( String... propertyNames ) {
		final Set<String> allowed = new HashSet<>( Arrays.asList( propertyNames ) );

		return new EntityPropertyFilter()
		{
			@Override
			public boolean include( EntityPropertyDescriptor descriptor ) {
				return allowed.contains( descriptor.getName() );
			}
		};
	}

	public static EntityPropertyFilter includeOrdered( String... propertyNames ) {
		return new OrderedIncludingEntityPropertyFilter( propertyNames );
	}

	public static EntityPropertyFilter exclude( String... propertyNames ) {
		final Set<String> excluded = new HashSet<>( Arrays.asList( propertyNames ) );

		return new EntityPropertyFilter()
		{
			@Override
			public boolean include( EntityPropertyDescriptor descriptor ) {
				return !excluded.contains( descriptor.getName() );
			}
		};
	}

	public static Comparator<EntityPropertyDescriptor> order( String... propertyNames ) {
		return new EntityPropertyOrder( propertyNames );
	}

	public static class OrderedIncludingEntityPropertyFilter extends EntityPropertyOrder implements EntityPropertyFilter
	{
		public OrderedIncludingEntityPropertyFilter( String... ordered ) {
			super( ordered );
		}

		@Override
		public boolean include( EntityPropertyDescriptor descriptor ) {
			return getOrder().containsKey( descriptor.getName() );
		}
	}
}
