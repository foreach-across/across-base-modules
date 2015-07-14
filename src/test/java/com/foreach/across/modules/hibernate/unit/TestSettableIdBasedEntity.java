package com.foreach.across.modules.hibernate.unit;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSettableIdBasedEntity
{
	@Test
	public void entityIsNewIfNoRegularIdSet() {
		Entity e = new Entity();
		assertTrue( e.isNew() );

		e.setId( 10L );
		assertFalse( e.isNew() );

		e.setNewEntityId( 10L );
		assertTrue( e.isNew() );

		e.setId( 11L );
		assertFalse( e.isNew() );
	}

	@Test
	public void idResetsNewEntityId() {
		Entity e = new Entity();
		e.setNewEntityId( 10L );

		assertNull( e.getId() );
		assertEquals( Long.valueOf( 10 ), e.getNewEntityId() );

		e.setId( 11L );
		assertEquals( Long.valueOf( 11 ), e.getId() );
		assertNull( e.getNewEntityId() );
	}

	@Test
	public void newEntityIdResetsRegularId() {
		Entity e = new Entity();
		assertNull( e.getId() );
		assertNull( e.getNewEntityId() );

		e.setId( 10L );
		assertEquals( Long.valueOf( 10L ), e.getId() );
		assertNull( e.getNewEntityId() );

		e.setNewEntityId( 10L );
		assertNull( e.getId() );
		assertEquals( Long.valueOf( 10L ), e.getNewEntityId() );
	}

	@Test
	@SuppressWarnings("all")
	public void newEntitiesEqualIfSame() {
		Entity one = new Entity( "one" );
		Entity two = new Entity( "two" );
		Entity same = one;

		assertFalse( one.equals( two ) );
		assertFalse( two.equals( one ) );

		assertTrue( same.equals( one ) );
		assertTrue( one.equals( same ) );
	}

	@Test
	public void entityEqualityBasedOnId() {
		Entity one = new Entity( 1L, "one" );
		Entity two = new Entity( 2L, "two" );
		Entity three = new Entity( 1L, "two" );

		assertFalse( one.equals( two ) );
		assertFalse( two.equals( one ) );

		assertFalse( two.equals( three ) );
		assertFalse( three.equals( two ) );

		assertTrue( one.equals( three ) );
		assertTrue( three.equals( one ) );
	}

	@Test
	public void newAndNonNewEntityEquality() {
		Entity one = new Entity( "one" );
		Entity two = new Entity( 1L, "one" );

		assertFalse( one.equals( two ) );
		assertFalse( two.equals( one ) );
	}

	private static class Entity extends SettableIdBasedEntity<Entity>
	{
		private Long id;
		private String name;

		public Entity() {
		}

		public Entity( String name ) {
			this.name = name;
		}

		public Entity( Long id, String name ) {
			this.id = id;
			this.name = name;
		}

		@Override
		public void setId( Long id ) {
			this.id = id;
		}

		@Override
		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
