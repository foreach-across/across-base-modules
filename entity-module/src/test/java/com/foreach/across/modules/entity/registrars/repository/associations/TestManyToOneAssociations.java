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
import com.foreach.across.modules.entity.testmodules.springdata.business.Car;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.CompanyRepository;
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
public class TestManyToOneAssociations
{
	private static boolean inserted = false;

	private static Company one, two, three;
	private static Client john, joe, peter;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ClientRepository clientRepository;

	private EntityListViewPageFetcher pageFetcher;

	@Before
	public void insertTestData() {
		pageFetcher = null;

		if ( !inserted ) {
			inserted = true;

			one = new Company( "one" );
			two = new Company( "two" );
			three = new Company( "three" );

			companyRepository.save( Arrays.asList( one, two, three ) );

			john = new Client( "john", one );
			joe = new Client( "joe", two );
			peter = new Client( "peter", two );

			clientRepository.save( Arrays.asList( john, joe, peter ) );
		}
	}

	@Test
	public void companyShouldHaveAnAssociationToItsClients() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );

		EntityAssociation association = company.association( "client.company" );

		assertNotNull( association );
		assertEquals(
				"Association name should be target entity name joined with target property name",
				"client.company", association.getName()
		);

		assertSame( company, association.getSourceEntityConfiguration() );
		assertSame( client, association.getTargetEntityConfiguration() );

		assertNull(
				"Regular ManyToOne should not have a source property as the association starts at the other end",
				association.getSourceProperty()
		);
		assertNotNull( association.getTargetProperty() );
		assertSame( client.getPropertyRegistry().getProperty( "company" ), association.getTargetProperty() );

		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}

	// todo: implement once supporting MappingContext items
	/*
	@Test
	public void companyShouldHaveAnAssociationToItsClientGroups() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityConfiguration clientGroup = entityRegistry.getEntityConfiguration( ClientGroup.class );

		EntityAssociation association = company.association( "clientGroup.id.company" );

		assertNotNull( association );
		assertEquals(
				"Association name should be target entity name joined with target property name",
				"clientGroup.id.company", association.getName()
		);

		assertSame( company, association.getSourceEntityConfiguration() );
		assertSame( clientGroup, association.getTargetEntityConfiguration() );

		assertNull(
				"Regular ManyToOne should not have a source property as the association starts at the other end",
				association.getSourceProperty()
		);
		assertNotNull( association.getTargetProperty() );
		assertSame( clientGroup.getPropertyRegistry().getProperty( "company" ), association.getTargetProperty() );

		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}
	*/

	@Test
	public void companyShouldNotHaveAssociationToItsCarsAsTheRepositoryDoesNotSupportSpecifications() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityConfiguration car = entityRegistry.getEntityConfiguration( Car.class );

		assertNotNull( car );

		EntityAssociation association = company.association( "car.company" );
		assertNull( association );
	}

	@Test
	public void companyHasClients() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "client.company" );

		assertNotNull( association );

		EntityListViewFactory listViewFactory = association.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( listViewFactory );

		pageFetcher = listViewFactory.getPageFetcher();

		verifyClients( one, john );
		verifyClients( two, joe, peter );
		verifyClients( three );
	}

	@SuppressWarnings("unchecked")
	private void verifyClients( Company company, Client... clients ) {
		assertNotNull( pageFetcher );

		ViewCreationContext cc = mock( ViewCreationContext.class );
		EntityView ev = new EntityListView( new ModelMap() );
		ev.setParentEntity( company );

		Page page = pageFetcher.fetchPage( cc, null, ev );
		assertNotNull( page );
		assertEquals( clients.length, page.getTotalElements() );
		assertTrue( page.getContent().containsAll( Arrays.asList( clients ) ) );
	}
}
