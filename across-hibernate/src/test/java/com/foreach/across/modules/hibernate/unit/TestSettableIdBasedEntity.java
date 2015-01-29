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

	private static class Entity extends SettableIdBasedEntity<Entity>
	{
		private Long id;

		@Override
		public void setId( Long id ) {
			this.id = id;
		}

		@Override
		public Long getId() {
			return id;
		}
	}
}
