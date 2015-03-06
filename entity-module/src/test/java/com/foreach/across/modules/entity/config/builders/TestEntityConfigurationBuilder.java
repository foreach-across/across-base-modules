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
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityConfigurationBuilder
{
	private EntitiesConfigurationBuilder entities;
	private MutableEntityRegistry entityRegistry;
	private AutowireCapableBeanFactory beanFactory;

	private EntityConfigurationBuilder<Client> builder;
	private MutableEntityConfiguration client, company;

	@Before
	public void before() {
		entities = new EntitiesConfigurationBuilder();

		entityRegistry = new EntityRegistryImpl();
		beanFactory = mock( AutowireCapableBeanFactory.class );

		client = mock( MutableEntityConfiguration.class );
		when( client.getEntityType() ).thenReturn( Client.class );
		when( client.getName() ).thenReturn( "client" );

		company = mock( MutableEntityConfiguration.class );
		when( company.getEntityType() ).thenReturn( Company.class );
		when( company.getName() ).thenReturn( "company" );

		entityRegistry.register( client );
		entityRegistry.register( company );

		builder = entities.entity( Client.class );

		reset( company );
	}

	@After
	public void after() {
		verifyZeroInteractions( company );
	}

	@Test
	public void andReturnsParent() {
		assertSame( entities, builder.and() );
	}

	@Test
	public void attributesAreAdded() {
		Company companyAttribute = new Company();

		builder.attribute( Company.class, companyAttribute );
		builder.attribute( "attributeKey", 123 );

		builder.apply( entityRegistry, beanFactory );

		verify( client ).addAttribute( Company.class, companyAttribute );
		verify( client ).addAttribute( "attributeKey", 123 );
	}

	@Test
	public void postProcessorsAreAppliedInOrder() {
		final List<String> processors = new ArrayList<>( 2 );

		builder.addPostProcessor( new PostProcessor<MutableEntityConfiguration<Client>>()
		{
			@Override
			public void process( MutableEntityConfiguration<Client> configuration ) {
				assertSame( client, configuration );
				processors.add( "one" );
			}
		} );

		builder.addPostProcessor( new PostProcessor<MutableEntityConfiguration<Client>>()
		{
			@Override
			public void process( MutableEntityConfiguration<Client> configuration ) {
				assertSame( client, configuration );
				processors.add( "two" );
			}
		} );

		builder.postProcess( entityRegistry );

		assertEquals( Arrays.asList( "one", "two" ), processors );
	}

	@Test
	public void hidden() {
		builder.hidden( true );
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( true );
	}

	@Test
	public void show() {
		builder.show();
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( false );
	}

	@Test
	public void hide() {
		builder.hide();
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( true );
	}

	@Test
	public void viewBuildersAreSpecificType() {
		AbstractEntityViewBuilder one = builder.view( "someView" );
		assertNotNull( one );

		AbstractEntityViewBuilder listOne = builder.listView();
		assertNotNull( listOne );

		AbstractEntityViewBuilder listTwo = builder.listView( EntityListView.VIEW_NAME );
		assertSame( listOne, listTwo );

		listTwo = builder.listView( "someListView" );
		assertNotNull( listTwo );
		assertNotSame( listOne, listTwo );

		one = builder.createFormView();
		assertNotNull( one );
		assertSame( one, builder.formView( EntityFormView.CREATE_VIEW_NAME ) );

		one = builder.updateFormView();
		assertNotNull( one );
		assertSame( one, builder.formView( EntityFormView.UPDATE_VIEW_NAME ) );
	}
}
