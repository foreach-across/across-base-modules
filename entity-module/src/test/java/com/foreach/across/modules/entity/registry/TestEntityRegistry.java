package com.foreach.across.modules.entity.registry;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEntityRegistry
{
	private MutableEntityRegistry registry;
	private MutableEntityConfiguration entityConfiguration;

	@Before
	public void reset() {
		registry = new EntityRegistryImpl();
		entityConfiguration = mock( MutableEntityConfiguration.class );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullEntityConfigurationThrowsException() {
		registry.register( null );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nameIsRequiredOnAnEntityConfiguration() {
		registry.register( entityConfiguration );
	}

	@Test(expected = IllegalArgumentException.class)
	public void typeIsRequiredOnAnEntityConfiguration() {
		when( entityConfiguration.getName() ).thenReturn( "entityName" );
		registry.register( entityConfiguration );
	}

	@Test
	public void registeringEntityConfiguration() {
		when( entityConfiguration.getEntityType() ).thenReturn( BigDecimal.class );
		when( entityConfiguration.getName() ).thenReturn( "entityName" );

		assertFalse( registry.contains( BigDecimal.class ) );
		assertFalse( registry.contains( "entityName" ) );
		assertNull( registry.getEntityConfiguration( BigDecimal.class ) );
		assertNull( registry.getEntityConfiguration( "entityName" ) );

		registry.register( entityConfiguration );
		assertEquals( 1, registry.getEntities().size() );
		assertThat( registry.getEntities(), hasItem( entityConfiguration ) );

		MutableEntityConfiguration other = mock( MutableEntityConfiguration.class );
		when( other.getEntityType() ).thenReturn( String.class );
		when( other.getName() ).thenReturn( "otherEntity" );

		assertFalse( registry.contains( String.class ) );
		assertFalse( registry.contains( "otherEntity" ) );
		assertNull( registry.getEntityConfiguration( String.class ) );
		assertNull( registry.getEntityConfiguration( "otherEntity" ) );

		registry.register( other );

		assertTrue( registry.contains( BigDecimal.class ) );
		assertTrue( registry.contains( "entityName" ) );
		assertSame( entityConfiguration, registry.getEntityConfiguration( BigDecimal.class ) );
		assertSame( entityConfiguration, registry.getEntityConfiguration( "entityName" ) );

		assertTrue( registry.contains( String.class ) );
		assertTrue( registry.contains( "otherEntity" ) );
		assertSame( other, registry.getEntityConfiguration( String.class ) );
		assertSame( other, registry.getEntityConfiguration( "otherEntity" ) );

		assertEquals( 2, registry.getEntities().size() );
		assertThat( registry.getEntities(),
		            hasItems( (EntityConfiguration) entityConfiguration, other ) );
	}

	@Test(expected = IllegalArgumentException.class)
	public void registeringSecondEntityWithSameNameIsNotAllowed() {
		when( entityConfiguration.getEntityType() ).thenReturn( BigDecimal.class );
		when( entityConfiguration.getName() ).thenReturn( "entityName" );

		registry.register( entityConfiguration );

		MutableEntityConfiguration other = mock( MutableEntityConfiguration.class );
		when( other.getEntityType() ).thenReturn( BigDecimal.class );
		when( other.getName() ).thenReturn( "entityName" );

		registry.register( other );
	}

	@Test(expected = IllegalArgumentException.class)
	public void registeringSecondEntityWithSameTypeIsNotAllowed() {
		when( entityConfiguration.getEntityType() ).thenReturn( BigDecimal.class );
		when( entityConfiguration.getName() ).thenReturn( "entityName" );

		registry.register( entityConfiguration );

		MutableEntityConfiguration other = mock( MutableEntityConfiguration.class );
		when( other.getEntityType() ).thenReturn( BigDecimal.class );
		when( other.getName() ).thenReturn( "otherEntity" );

		registry.register( other );
	}

	@Test
	public void reRegisteringEntityConfiguration() {
		when( entityConfiguration.getEntityType() ).thenReturn( BigDecimal.class );
		when( entityConfiguration.getName() ).thenReturn( "entityName" );

		registry.register( entityConfiguration );
		registry.register( entityConfiguration );

		assertEquals( 1, registry.getEntities().size() );
	}

	@Test
	public void removingAndReRegisteringEntityConfiguration() {
		when( entityConfiguration.getEntityType() ).thenReturn( BigDecimal.class );
		when( entityConfiguration.getName() ).thenReturn( "entityName" );

		registry.register( entityConfiguration );

		assertSame( entityConfiguration, registry.remove( BigDecimal.class ) );
		assertTrue( registry.getEntities().isEmpty() );
		assertFalse( registry.contains( "entityName" ) );
		assertFalse( registry.contains( BigDecimal.class ) );

		MutableEntityConfiguration other = mock( MutableEntityConfiguration.class );
		when( other.getEntityType() ).thenReturn( BigDecimal.class );
		when( other.getName() ).thenReturn( "entityName" );

		registry.register( other );
		assertTrue( registry.contains( BigDecimal.class ) );

		assertSame( other, registry.remove( "entityName" ) );
		assertTrue( registry.getEntities().isEmpty() );
		assertFalse( registry.contains( "entityName" ) );
		assertFalse( registry.contains( BigDecimal.class ) );

		registry.register( entityConfiguration );
		assertTrue( registry.contains( "entityName" ) );
	}

	@Test
	public void entityListIsSortedAccordingToDisplayName() {
		when( entityConfiguration.getEntityType() ).thenReturn( BigDecimal.class );
		when( entityConfiguration.getName() ).thenReturn( "entity1" );
		when( entityConfiguration.getDisplayName() ).thenReturn( "Second entity registered first" );

		registry.register( entityConfiguration );

		MutableEntityConfiguration other = mock( MutableEntityConfiguration.class );
		when( other.getEntityType() ).thenReturn( String.class );
		when( other.getName() ).thenReturn( "entity2" );
		when( other.getDisplayName() ).thenReturn( "First entity registered second" );

		registry.register( other );

		assertEquals( registry.getEntities(), (Collection) Arrays.asList( other, entityConfiguration ) );
	}
}
