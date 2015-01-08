package com.foreach.across.modules.entity.business;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultEntityPropertyRegistry implements EntityPropertyRegistry
{
	private final Class<?> entityType;

	private final Map<String, EntityPropertyDescriptor> descriptorMap = new HashMap<>();

	private EntityPropertyFilter defaultFilter;

	private EntityPropertyOrder declarationOrder = null;
	private Comparator<EntityPropertyDescriptor> defaultOrder = null;

	public DefaultEntityPropertyRegistry( Class<?> entityType ) {
		this.entityType = entityType;

		for ( PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors( entityType ) ) {
			descriptorMap.put( descriptor.getName(), new SimpleEntityPropertyDescriptor( descriptor ) );
		}

		declarationOrder = buildDeclarationOrder();
		defaultOrder = declarationOrder;
	}

	private EntityPropertyOrder buildDeclarationOrder() {
		final Map<String, Integer> order = new HashMap<>();

		ReflectionUtils.doWithFields( entityType, new ReflectionUtils.FieldCallback()
		{
			private Class declaringClass;
			private int declaringClassOffset = 10000;

			@Override
			public void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException {
				if ( !field.getDeclaringClass().equals( declaringClass ) ) {
					declaringClass = field.getDeclaringClass();
					declaringClassOffset -= 1000;
				}

				if ( descriptorMap.containsKey( field.getName() ) ) {
					order.put( field.getName(), declaringClassOffset + order.size() + 1 );
				}
			}
		} );

		// Determine method indices
		final Map<Method, Integer> methodIndex = new HashMap<>();

		ReflectionUtils.doWithMethods( entityType, new ReflectionUtils.MethodCallback()
		{
			private Class declaringClass;
			private int declaringClassOffset = 1000000;

			@Override
			public void doWith( Method method ) throws IllegalArgumentException, IllegalAccessException {
				if ( !method.getDeclaringClass().equals( declaringClass ) ) {
					declaringClass = method.getDeclaringClass();
					declaringClassOffset -= 1000;
				}

				if ( !methodIndex.containsKey( method ) ) {
					methodIndex.put( method, declaringClassOffset + methodIndex.size() + 1 );
				}
			}
		} );

		// For every property without declared order, use the read method first, write method second to determine order
		for ( EntityPropertyDescriptor entityPropertyDescriptor : descriptorMap.values() ) {
			if ( !order.containsKey( entityPropertyDescriptor.getName() ) ) {
				PropertyDescriptor propertyDescriptor = entityPropertyDescriptor.getPropertyDescriptor();

				if ( propertyDescriptor != null ) {
					Method lookupMethod = propertyDescriptor.getReadMethod();

					if ( lookupMethod != null ) {
						order.put( entityPropertyDescriptor.getName(), methodIndex.get( lookupMethod ) );
					}
					else {
						lookupMethod = propertyDescriptor.getWriteMethod();

						if ( lookupMethod != null ) {
							order.put( entityPropertyDescriptor.getName(), methodIndex.get( lookupMethod ) );
						}
					}
				}
			}
		}

		return new EntityPropertyOrder( order );
	}

	@Override
	public void setDefaultFilter( EntityPropertyFilter filter ) {
		defaultFilter = filter;
	}

	@Override
	public void setDefaultOrder( String... propertyNames ) {
		defaultOrder = EntityPropertyOrder.composite( new EntityPropertyOrder( propertyNames ), declarationOrder );
	}

	@Override
	public List<EntityPropertyDescriptor> getProperties() {
		return getProperties( EntityPropertyFilters.NoOp );
	}

	@Override
	public List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter ) {
		Assert.notNull( filter );

		if ( filter instanceof EntityPropertyFilters.OrderedIncludingEntityPropertyFilter ) {
			return getProperties( filter, (EntityPropertyFilters.OrderedIncludingEntityPropertyFilter) filter );
		}

		return fetchProperties( filter, defaultOrder );
	}

	@Override
	public List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter,
	                                                     Comparator<EntityPropertyDescriptor> comparator ) {
		Assert.notNull( filter );
		Assert.notNull( comparator );

		return fetchProperties( filter, EntityPropertyOrder.composite( comparator, defaultOrder ) );
	}

	private List<EntityPropertyDescriptor> fetchProperties( EntityPropertyFilter filter,
	                                                        Comparator<EntityPropertyDescriptor> comparator ) {
		List<EntityPropertyDescriptor> filtered = new ArrayList<>();

		EntityPropertyFilter first = defaultFilter != null ? defaultFilter : EntityPropertyFilters.NoOp;

		for ( EntityPropertyDescriptor candidate : descriptorMap.values() ) {
			if ( first.include( candidate ) && filter.include( candidate ) ) {
				filtered.add( candidate );
			}
		}

		Collections.sort( filtered, comparator );

		return filtered;
	}

	@Override
	public boolean contains( String propertyName ) {
		return descriptorMap.containsKey( propertyName );
	}

	/**
	 * @return Number of properties in the registry.
	 */
	@Override
	public int size() {
		return descriptorMap.size();
	}
}
