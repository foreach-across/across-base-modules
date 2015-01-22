package com.foreach.across.modules.entity.registry.properties;

import java.util.*;

public final class EntityPropertyFilters
{
	public static EntityPropertyFilter NoOp = new EntityPropertyFilter.Exclusive()
	{
		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return true;
		}

		@Override
		public Collection<String> getPropertyNames() {
			return Collections.emptyList();
		}
	};

	private EntityPropertyFilters() {
	}

	public static EntityPropertyFilter include( String... propertyNames ) {
		return new InclusivePropertyFilter( Arrays.asList( propertyNames ) );
	}

	public static EntityPropertyFilter include( Collection<String> propertyNames ) {
		return new InclusivePropertyFilter( propertyNames );
	}

	public static EntityPropertyFilter includeOrdered( String... propertyNames ) {
		return new OrderedIncludingEntityPropertyFilter( propertyNames );
	}

	public static EntityPropertyFilter includeOrdered( Collection<String> propertyNames ) {
		return new OrderedIncludingEntityPropertyFilter( propertyNames );
	}

	public static EntityPropertyFilter exclude( String... propertyNames ) {
		return exclude( Arrays.asList( propertyNames ) );
	}

	public static EntityPropertyFilter exclude( Collection<String> propertyNames ) {
		final Set<String> excluded = new HashSet<>( propertyNames );

		return new EntityPropertyFilter.Exclusive()
		{
			@Override
			public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
				return !excluded.contains( descriptor.getName() );
			}

			@Override
			public Collection<String> getPropertyNames() {
				return excluded;
			}
		};
	}

	public static Comparator<EntityPropertyDescriptor> order( String... propertyNames ) {
		return new EntityPropertyOrder( propertyNames );
	}

	public static class InclusivePropertyFilter implements EntityPropertyFilter.Inclusive
	{
		private final Set<String> propertyNames = new HashSet<>();

		public InclusivePropertyFilter( Collection<String> propertyNames ) {
			this.propertyNames.addAll( propertyNames );
		}

		public Set<String> getPropertyNames() {
			return propertyNames;
		}

		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
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

		public OrderedIncludingEntityPropertyFilter( Collection<String> ordered ) {
			super( ordered );
		}

		@Override
		public Collection<String> getPropertyNames() {
			return getOrder().keySet();
		}

		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return getOrder().containsKey( descriptor.getName() );
		}
	}

}
