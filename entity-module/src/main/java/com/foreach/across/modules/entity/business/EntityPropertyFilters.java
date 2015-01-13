package com.foreach.across.modules.entity.business;

import java.util.*;

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
		return new InclusivePropertyFilter( propertyNames );
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

	public static class InclusivePropertyFilter implements EntityPropertyFilter.Inclusive
	{
		private final Set<String> propertyNames = new HashSet<>();

		public InclusivePropertyFilter( String... propertyNames ) {
			this.propertyNames.addAll( Arrays.asList( propertyNames ) );
		}

		public Set<String> getPropertyNames() {
			return propertyNames;
		}

		@Override
		public boolean include( EntityPropertyDescriptor descriptor ) {
			return propertyNames.contains( descriptor.getName() );
		}
	}

	public static class OrderedIncludingEntityPropertyFilter
			extends EntityPropertyOrder
			implements EntityPropertyFilter.Inclusive
	{
		public OrderedIncludingEntityPropertyFilter( String... ordered ) {
			super( ordered );
		}

		@Override
		public Collection<String> getPropertyNames() {
			return getOrder().keySet();
		}

		@Override
		public boolean include( EntityPropertyDescriptor descriptor ) {
			return getOrder().containsKey( descriptor.getName() );
		}
	}

}
