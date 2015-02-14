package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.registry.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = { TestProperties.Configuration.class })
public class TestProperties
{
	@Autowired
	private EntityPropertyRegistries entityPropertyRegistries;

	@Test
	public void propertiesAreDetected() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();

		assertEquals( 6, descriptors.size() );
		assertTrue( registry.contains( "id" ) );
		assertTrue( registry.contains( "name" ) );
		assertTrue( registry.contains( "displayName" ) );
		assertTrue( registry.contains( "someValue" ) );
		assertTrue( registry.contains( "class" ) );
		assertTrue( registry.contains( "address" ) );
	}

	@Test
	public void defaultOrderCanBeSpecified() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );
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
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();
		assertEquals( 6, descriptors.size() );

		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
		assertEquals( "someValue", descriptors.get( 3 ).getName() );
		assertEquals( "id", descriptors.get( 4 ).getName() );
		assertEquals( "class", descriptors.get( 5 ).getName() );
	}

	@Test
	public void customIncludeFilterKeepsTheDefaultOrder() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.include( "name", "id", "displayName" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "displayName", descriptors.get( 1 ).getName() );
		assertEquals( "id", descriptors.get( 2 ).getName() );
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
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	@Test
	public void defaultFilterIsAlwaysApplied() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );
		registry.setDefaultFilter( EntityPropertyFilters.exclude( "class" ) );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();
		assertEquals( 5, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
		assertEquals( "someValue", descriptors.get( 3 ).getName() );
		assertEquals( "id", descriptors.get( 4 ).getName() );

		descriptors = registry.getProperties( EntityPropertyFilters.exclude( "id", "displayName" ) );

		assertEquals( 3, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
	}

	@Test
	public void filterWithCustomOrder() {
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );
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
		EntityPropertyRegistry registry = new DefaultEntityPropertyRegistry( Customer.class, null );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.includeOrdered( "name", "id", "displayName" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "id", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
	}

	@Test
	public void includeNestedProperties() {
		EntityPropertyRegistry registry = entityPropertyRegistries.getRegistry( Customer.class );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.includeOrdered( "name", "address.street" )
		);

		assertEquals( 2, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address.street", descriptors.get( 1 ).getName() );
	}

	@Test
	public void valueFetchersAreCreated() {
		EntityPropertyRegistry registry = entityPropertyRegistries.getRegistry( Customer.class );

		Customer customer = new Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		Address address = new Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );

		customer.setAddress( address );

		assertEquals( "some name", fetch( registry, customer, "name" ) );
		assertEquals( "some name (123)", fetch( registry, customer, "displayName" ) );
		assertEquals( "my street", fetch( registry, customer, "address.street" ) );
		assertNull( registry.getProperty( "address.size()" ) );
	}

	@Test
	public void customPropertyAndValueFetcher() {
		EntityPropertyRegistry parent = entityPropertyRegistries.getRegistry( Customer.class );
		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry( parent );

		SimpleEntityPropertyDescriptor calculated = new SimpleEntityPropertyDescriptor();
		calculated.setName( "address.size()" );
		calculated.setValueFetcher( new SpelValueFetcher( "address.size()" ) );
		registry.register( calculated );

		Customer customer = new Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		Address address = new Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );
		customer.setAddress( address );

		assertEquals( "some name", fetch( registry, customer, "name" ) );
		assertEquals( "some name (123)", fetch( registry, customer, "displayName" ) );
		assertEquals( "my street", fetch( registry, customer, "address.street" ) );
		assertEquals( 9, fetch( registry, customer, "address.size()" ) );
	}

	@SuppressWarnings("unchecked")
	private Object fetch( EntityPropertyRegistry registry, Object entity, String propertyName ) {
		return registry.getProperty( propertyName ).getValueFetcher().getValue( entity );
	}

	public static class Address
	{
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

		public int size() {
			return street.length();
		}
	}

	public abstract static class BaseCustomer
	{
		private long id;

		public long getId() {
			return id;
		}

		public void setId( long id ) {
			this.id = id;
		}
	}

	public static class Customer extends BaseCustomer
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

	@org.springframework.context.annotation.Configuration
	public static class Configuration
	{
		@Bean
		public EntityPropertyRegistries entityPropertyRegistries() {
			return new EntityPropertyRegistries();
		}

		@Bean
		public EntityPropertyRegistryBuilder dummyBuilder() {
			return mock( EntityPropertyRegistryBuilder.class );
		}
	}
}
