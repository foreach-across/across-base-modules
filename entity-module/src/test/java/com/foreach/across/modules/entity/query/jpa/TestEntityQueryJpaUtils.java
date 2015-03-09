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
package com.foreach.across.modules.entity.query.jpa;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registrars.repository.TestRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.testmodules.springdata.business.Representative;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.RepresentativeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestEntityQueryJpaUtils
{
	private static boolean inserted = false;

	private static Company one, two, three;
	private static Representative john, joe, peter;

	@Autowired
	private RepresentativeRepository representativeRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Before
	public void insertTestData() {
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
	public void eq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.EQ, "two" ) );
		Company found = companyRepository.findOne( EntityQueryJpaUtils.<Company>toSpecification( query ) );

		assertNotNull( found );
		assertEquals( two, found );
	}

	@Test
	public void neq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.<Company>toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, three ) ) );
	}

	@Test
	public void contains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.<Company>toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two ) ) );
	}
}
