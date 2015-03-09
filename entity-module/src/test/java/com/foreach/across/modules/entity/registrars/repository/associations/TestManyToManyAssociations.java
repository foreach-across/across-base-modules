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
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.testmodules.springdata.business.Representative;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.RepresentativeRepository;
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
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestManyToManyAssociations
{
	private static boolean inserted = false;

	private static Company one, two, three;
	private static Representative john, joe, peter;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private RepresentativeRepository representativeRepository;

	@Autowired
	private CompanyRepository companyRepository;

	private EntityListViewPageFetcher pageFetcher;

	@Before
	public void insertTestData() {
		pageFetcher = null;

		if ( !inserted ) {
			inserted = true;

			john = new Representative( "john", "John" );
			joe = new Representative( "joe", "Joe" );
			peter = new Representative( "peter", "Peter" );

			representativeRepository.save( Arrays.asList( john, joe, peter ) );

			one = new Company( "one" );
			two = new Company( "two" );
			three = new Company( "three" );
			one.setRepresentatives( Collections.singleton( john ) );
			two.setRepresentatives( new HashSet<>( Arrays.asList( john, joe, peter ) ) );
			three.setRepresentatives( Collections.singleton( peter ) );

			companyRepository.save( Arrays.asList( one, two, three ) );
		}
	}

	@Test
	public void companyHasRepresentatives() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "company.representatives" );

		assertNotNull( association );
		assertEquals(
				"Association name should be source entity name joined with source property name",
				"company.representatives", association.getName()
		);

		EntityListViewFactory listViewFactory = association.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( listViewFactory );

		pageFetcher = listViewFactory.getPageFetcher();

		verifyRepresentatives( one, john );
		verifyRepresentatives( two, john, joe, peter );
		verifyRepresentatives( three, peter );
	}

	@Test
	public void companyRepresentativesShouldBeHiddenByDefault() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "company.representatives" );

		assertNotNull( association );
		assertTrue( association.isHidden() );
	}

	@Test
	public void representativeHasCompanies() {
		EntityConfiguration representative = entityRegistry.getEntityConfiguration( Representative.class );
		EntityAssociation association = representative.association( "company.representatives" );

		assertNotNull( association );
		assertEquals(
				"Association name should be the reverse source entity name joined with source property name",
				"company.representatives", association.getName()
		);

		EntityListViewFactory listViewFactory = association.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( listViewFactory );

		pageFetcher = listViewFactory.getPageFetcher();

		verifyCompanies( john, one, two );
		verifyCompanies( joe, two );
		verifyCompanies( peter, two, three );
	}

	@Test
	public void representativeCompaniesShouldNotBeHidden() {
		EntityConfiguration representative = entityRegistry.getEntityConfiguration( Representative.class );
		EntityAssociation association = representative.association( "company.representatives" );

		assertNotNull( association );
		assertFalse( association.isHidden() );
	}

	@SuppressWarnings("unchecked")
	private void verifyCompanies( Representative representative, Company... companies ) {
		assertNotNull( pageFetcher );

		ViewCreationContext cc = mock( ViewCreationContext.class );
		EntityView ev = new EntityListView( new ModelMap() );
		ev.setParentEntity( representative );

		Page page = pageFetcher.fetchPage( cc, null, ev );
		assertNotNull( page );
		assertEquals( companies.length, page.getTotalElements() );
		assertTrue( page.getContent().containsAll( Arrays.asList( companies ) ) );
	}

	@SuppressWarnings("unchecked")
	private void verifyRepresentatives( Company company, Representative... reps ) {
		assertNotNull( pageFetcher );

		ViewCreationContext cc = mock( ViewCreationContext.class );
		EntityView ev = new EntityListView( new ModelMap() );
		ev.setParentEntity( company );

		Page page = pageFetcher.fetchPage( cc, null, ev );
		assertNotNull( page );
		assertEquals( reps.length, page.getTotalElements() );
		assertTrue( page.getContent().containsAll( Arrays.asList( reps ) ) );
	}
}
