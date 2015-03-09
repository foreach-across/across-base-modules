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
package com.foreach.across.modules.entity.registrars.repository.associations;

import com.foreach.across.modules.entity.registrars.repository.TestRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.business.*;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientGroupRepository;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.GroupRepository;
import com.foreach.across.modules.entity.views.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Verifies that a @ManyToOne is registered as a @OneToMany on the source entity.
 * If entity Client refers to a single Company, then an association should be created on Company that represents
 * all clients linked to that Company.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestOneToManyAssociations
{
	private static boolean inserted = false;

	private static Group groupOne, groupTwo;
	private static Company one, two, three;
	private static Client john, joe, peter;
	private static ClientGroup clientGroup;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ClientGroupRepository clientGroupRepository;

	@Autowired
	private CompanyRepository companyRepository;

	private EntityListViewPageFetcher pageFetcher;

	@Before
	public void insertTestData() {
		pageFetcher = null;

		if ( !inserted ) {
			inserted = true;

			groupOne = new Group( "groupOne" );
			groupTwo = new Group( "groupTwo" );
			groupRepository.save( Arrays.asList( groupOne, groupTwo ) );

			one = new Company( "one" );
			two = new Company( "two" );
			three = new Company( "three" );

			companyRepository.save( Arrays.asList( one, two, three ) );

			john = new Client( "john", one );
			joe = new Client( "joe", two );
			peter = new Client( "peter", two );

			clientRepository.save( Arrays.asList( john, joe, peter ) );

			ClientGroupId clientGroupId = new ClientGroupId();
			clientGroupId.setGroup( groupOne );
			clientGroupId.setClient( john );

			clientGroup = new ClientGroup();
			clientGroup.setId( clientGroupId );

			clientGroupRepository.save( clientGroup );

			john.setGroups( new HashSet<>( Arrays.asList( clientGroup ) ) );
			clientRepository.save( john );
		}
	}

	@Test
	public void clientHasAssociationToClientGroups() {
		EntityConfiguration clientGroup = entityRegistry.getEntityConfiguration( ClientGroup.class );
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );

		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );
		assertEquals(
				"Association name should be source entity name joined with target property name",
				"client.groups", association.getName()
		);

		assertSame( client, association.getSourceEntityConfiguration() );
		assertSame( clientGroup, association.getTargetEntityConfiguration() );

		assertNotNull( "OneToMany should have both source and target property set", association.getSourceProperty() );
		assertNotNull( "OneToMany should have both source and target property set", association.getTargetProperty() );

		assertSame( client.getPropertyRegistry().getProperty( "groups" ), association.getSourceProperty() );
		assertEquals( "id.client", association.getTargetProperty().getName() );

		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}

	@Test
	public void clientGroupsShouldBeHiddenByDefault() {
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );
		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );
		assertTrue( association.isHidden() );
	}

	@Test
	public void clientHasGroup() {
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );
		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );

		EntityListViewFactory listViewFactory = association.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( listViewFactory );

		pageFetcher = listViewFactory.getPageFetcher();

		verifyClientGroups( john, clientGroup );
	}

	@SuppressWarnings("unchecked")
	private void verifyClientGroups( Client client, ClientGroup... groups ) {
		assertNotNull( pageFetcher );

		ViewCreationContext cc = mock( ViewCreationContext.class );
		EntityView ev = new EntityListView( new ModelMap() );
		ev.setParentEntity( client );

		Page page = pageFetcher.fetchPage( cc, null, ev );
		assertNotNull( page );
		assertEquals( groups.length, page.getTotalElements() );
		assertTrue( page.getContent().containsAll( Arrays.asList( groups ) ) );
	}
}
