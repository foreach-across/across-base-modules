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
package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.modules.hibernate.testmodules.springdata.Client;
import com.foreach.across.modules.hibernate.testmodules.springdata.ClientRepository;
import com.foreach.across.modules.hibernate.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestSettableIdEntityEquality.Config.class)
public class TestSettableIdEntityEquality
{
	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private UnitOfWorkFactory unitOfWork;

	@Test
	public void equalsWhenProxied() {
		unitOfWork.start();

		List<Client> clients = clientRepository.findAll();
		assertTrue( clients.isEmpty() );

		Client autoId = new Client( "one" );
		assertTrue( autoId.isNew() );

		Client fixedId = new Client( "two" );
		fixedId.setNewEntityId( -10L );
		assertTrue( fixedId.isNew() );
		// this is lazy -> will get proxied
		fixedId.setLinkedClient( autoId );

		Client savedAuto = clientRepository.save( autoId );
		assertFalse( savedAuto.isNew() );
		assertTrue( savedAuto.getId() > 0 );

		Client savedFixed = clientRepository.save( fixedId );
		assertFalse( savedFixed.isNew() );
		assertEquals( Long.valueOf( -10L ), savedFixed.getId() );

		unitOfWork.restart();

		// typical equals of non-proxied objects
		Client oneretrieved = clientRepository.findOne( -10L );
		assertTrue( fixedId.equals( oneretrieved ) );
		assertTrue( oneretrieved.equals( fixedId ) );

		// equals between non-proxied and proxied object
		Client linkedClient = clientRepository.findOne( -10L ).getLinkedClient();
		assertTrue( autoId.equals( linkedClient ) );
		assertTrue( linkedClient.equals( autoId ) );

		unitOfWork.stop();
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			hibernateModule.setProperty( AcrossHibernateModuleSettings.CREATE_UNITOFWORK_FACTORY, true );
			context.addModule( hibernateModule );

			context.addModule( new SimpleJpaModule() );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.setExposeFilter( new ClassBeanFilter( ClientRepository.class ) );
			context.addModule( springDataJpaModule );
		}
	}
}
