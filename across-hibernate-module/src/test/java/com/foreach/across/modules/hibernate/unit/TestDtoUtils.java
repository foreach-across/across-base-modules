/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.hibernate.unit;

import com.foreach.across.modules.hibernate.business.EntityWithDto;
import com.foreach.across.modules.hibernate.util.DtoUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestDtoUtils
{
	public static class Entity
	{
		private String name;
		private Set<String> values;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public Set<String> getValues() {
			return values;
		}

		public void setValues( Set<String> values ) {
			this.values = values;
		}
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	public static class HierarchicalEntity implements EntityWithDto<HierarchicalEntity>
	{
		private String name;
		private HierarchicalEntity parent;

		@Override
		public HierarchicalEntity toDto() {
			return DtoUtils.createDto( this );
		}
	}

	@Setter
	@Getter
	@EqualsAndHashCode
	public static class CollectionEntity implements EntityWithDto<CollectionEntity>
	{
		private List<HierarchicalEntity> values;

		@Override
		public CollectionEntity toDto() {
			return DtoUtils.createDto( this );
		}
	}

	@Setter
	@Getter
	public static class ArrayEntity implements EntityWithDto<ArrayEntity>
	{
		private HierarchicalEntity[] values;

		@Override
		public ArrayEntity toDto() {
			return DtoUtils.createDto( this );
		}
	}

	@Test
	public void collectionsAreNotCloned() {
		Entity one = new Entity();
		one.setName( "one" );
		one.setValues( Collections.singleton( "test" ) );

		Entity dto = DtoUtils.createDto( one );
		assertNotNull( dto );
		assertNotSame( one, dto );
		assertEquals( one.getName(), dto.getName() );
		assertEquals( one.getValues(), dto.getValues() );
	}

	@Test
	public void nestedEntitiesAreCloned() {
		HierarchicalEntity child = new HierarchicalEntity();
		child.setName( "child" );

		HierarchicalEntity parent = new HierarchicalEntity();
		parent.setName( "parent" );
		child.setParent( parent );

		HierarchicalEntity dto = DtoUtils.createDto( child );
		assertNotNull( dto );
		assertNotSame( child, dto );
		assertEquals( child.getName(), dto.getName() );
		assertNotSame( child.getParent(), dto.getParent() );
		assertEquals( child.getParent().getName(), dto.getParent().getName() );
		assertNull( child.getParent().getParent() );
		assertNull( dto.getParent().getParent() );
		assertEquals( child.getParent(), dto.getParent() );
	}

	@Test
	public void arraysAreDeepCloned() {
		HierarchicalEntity child = new HierarchicalEntity();
		child.setName( "child" );

		HierarchicalEntity parent = new HierarchicalEntity();
		parent.setName( "parent" );
		child.setParent( parent );

		ArrayEntity collection = new ArrayEntity();
		collection.setValues( new HierarchicalEntity[] { child } );

		ArrayEntity dto = DtoUtils.createDto( collection );
		assertNotNull( dto );
		assertNotSame( collection, dto );
		assertEquals( collection.getValues(), dto.getValues() );
		assertEquals( collection.getValues().length, dto.getValues().length );
		assertEquals( 1, collection.getValues().length );

		HierarchicalEntity collectionItem = collection.getValues()[0];
		HierarchicalEntity dtoCollectionItem = dto.getValues()[0];
		assertNotSame( collectionItem, dtoCollectionItem );
		assertNotSame( collectionItem.getParent(), dtoCollectionItem.getParent() );
		assertEquals( collectionItem.getParent().getName(), dtoCollectionItem.getParent().getName() );
		assertNull( collectionItem.getParent().getParent() );
		assertNull( dtoCollectionItem.getParent().getParent() );
		assertEquals( collectionItem.getParent(), dtoCollectionItem.getParent() );
	}

	@Test
	public void collectionsOfEntitiesAreDeepCloned() {
		HierarchicalEntity child = new HierarchicalEntity();
		child.setName( "child" );

		HierarchicalEntity parent = new HierarchicalEntity();
		parent.setName( "parent" );
		child.setParent( parent );

		CollectionEntity collection = new CollectionEntity();
		collection.setValues( new ArrayList<>( Collections.singletonList( child ) ) );

		CollectionEntity dto = DtoUtils.createDto( collection );
		assertNotNull( dto );
		assertNotSame( collection, dto );
		assertEquals( collection.getValues(), dto.getValues() );
		assertEquals( collection.getValues().size(), dto.getValues().size() );
		assertEquals( 1, collection.getValues().size() );

		HierarchicalEntity collectionItem = collection.getValues().get( 0 );
		HierarchicalEntity dtoCollectionItem = dto.getValues().get( 0 );
		assertNotSame( collectionItem, dtoCollectionItem );
		assertNotSame( collectionItem.getParent(), dtoCollectionItem.getParent() );
		assertEquals( collectionItem.getParent().getName(), dtoCollectionItem.getParent().getName() );
		assertNull( collectionItem.getParent().getParent() );
		assertNull( dtoCollectionItem.getParent().getParent() );
		assertEquals( collectionItem.getParent(), dtoCollectionItem.getParent() );
	}
}
