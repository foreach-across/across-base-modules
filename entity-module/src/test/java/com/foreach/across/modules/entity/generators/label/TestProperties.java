package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.business.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.business.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.business.EntityPropertyFilters;
import com.foreach.across.modules.entity.business.EntityPropertyRegistry;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestProperties
{
	@Test
	public void propertiesAreDetected() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );

		assertEquals( 6, registry.size() );
		assertTrue( registry.contains( "id" ) );
		assertTrue( registry.contains( "name" ) );
		assertTrue( registry.contains( "displayName" ) );
		assertTrue( registry.contains( "someValue" ) );
		assertTrue( registry.contains( "class" ) );
		assertTrue( registry.contains( "address" ) );
	}

	@Test
	public void defaultOrderCanBeSpecified() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		registry.setDefaultOrder( "name", "displayName", "someValue", "class", "id" );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();

		assertEquals( 6, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "displayName", descriptors.get( 1 ).getName() );
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
		assertEquals( "id", descriptors.get( 4 ).getName() );
		assertEquals( "address", descriptors.get( 5 ).getName() );
	}

	@Test
	public void defaultOrderIsAccordingToDeclarationIfNotSpecified() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();
		assertEquals( 6, descriptors.size() );
		assertEquals( "id", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "address", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
		assertEquals( "displayName", descriptors.get( 4 ).getName() );
		assertEquals( "someValue", descriptors.get( 5 ).getName() );
	}

	@Test
	public void customIncludeFilterKeepsTheDefaultOrder() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.include( "name", "id", "displayName" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "id", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
	}

	@Test
	public void customExcludeFilterKeepsTheDefaultOrder() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.exclude( "id", "displayName" )
		);

		assertEquals( 4, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "class", descriptors.get( 2 ).getName() );
		assertEquals( "someValue", descriptors.get( 3 ).getName() );
	}

	@Test
	public void defaultFilterIsAlwaysApplied() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		registry.setDefaultFilter( EntityPropertyFilters.exclude( "class" ) );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();
		assertEquals( 5, descriptors.size() );
		assertEquals( "id", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "address", descriptors.get( 2 ).getName() );
		assertEquals( "displayName", descriptors.get( 3 ).getName() );
		assertEquals( "someValue", descriptors.get( 4 ).getName() );

		descriptors = registry.getProperties( EntityPropertyFilters.exclude( "id", "displayName" ) );

		assertEquals( 3, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
	}

	@Test
	public void filterWithCustomOrder() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.include( "name", "id", "displayName" ),
		        EntityPropertyFilters.order( "displayName", "id", "name" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "displayName", descriptors.get( 0 ).getName() );
		assertEquals( "id", descriptors.get( 1 ).getName() );
		assertEquals( "name", descriptors.get( 2 ).getName() );
	}

	@Test
	public void orderedIncludeFilter() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.includeOrdered( "name", "id", "displayName" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "id", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
	}

	@Ignore
	@Test
	public void includeNestedProperties() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.includeOrdered( "name", "address.street" )
		);
	}

	private static class Address {
		private String street;
		private int number;

		public String getStreet() {
			return street;
		}

		public void setStreet( String street ) {
			this.street = street;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber( int number ) {
			this.number = number;
		}
	}
	private abstract static class BaseCustomer
	{
		private long id;

		public long getId() {
			return id;
		}

		public void setId( long id ) {
			this.id = id;
		}
	}

	private static class Customer extends BaseCustomer
	{
		private String name;
		private Address address;
		private Object value;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress( Address address ) {
			this.address = address;
		}

		public String getDisplayName() {
			return String.format( "%s (%s)", getName(), getId() );
		}

		public void setSomeValue( String someValue ) {
			value = someValue;
		}
	}
}
