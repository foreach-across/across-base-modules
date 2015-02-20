package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyOrder;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestEntityPropertyOrder
{
	private List<EntityPropertyDescriptor> descriptors;

	@Before
	public void reset() {
		descriptors = new ArrayList<>();

		for ( PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors( Customer.class ) ) {
			descriptors.add( SimpleEntityPropertyDescriptor.forPropertyDescriptor( descriptor, Customer.class ) );
		}
	}

	@Test
	public void singleFilter() {
		Collections.sort( descriptors, new EntityPropertyOrder( null, "id", "name", "value", "class" ) );

		assertEquals( "id", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "value", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	@Test
	public void compositeFilter() {
		EntityPropertyOrder defaultOrder =  new EntityPropertyOrder( null, "id", "name", "value", "class" );
		Collections.sort( descriptors, EntityPropertyOrder.composite( new EntityPropertyOrder( "value", "name" ), defaultOrder ) );

		assertEquals( "value", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "id", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	private static class Customer
	{
		private long id;
		private String name;
		private Object value;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId( long id ) {
			this.id = id;
		}

		public Object getValue() {
			return value;
		}

		public void setValue( Object value ) {
			this.value = value;
		}
	}
}
