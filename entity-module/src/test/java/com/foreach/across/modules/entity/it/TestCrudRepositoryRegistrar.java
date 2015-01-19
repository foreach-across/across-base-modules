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
package com.foreach.across.modules.entity.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.Client;
import com.foreach.across.modules.entity.testmodules.springdata.ClientRepository;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestCrudRepositoryRegistrar.Config.class)
public class TestCrudRepositoryRegistrar
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ClientRepository clientRepository;

	@Test
	public void clientShouldBeRegistered() {
		assertEquals( 1, entityRegistry.getEntities().size() );
		assertTrue( entityRegistry.contains( Client.class ) );

		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertNotNull( configuration );

		EntityModel model = configuration.getEntityModel();
		assertNotNull( model );

		EntityViewFactory viewFactory = configuration.getViewFactory( "crud-list" );
		assertNotNull( viewFactory );
	}

	@Test
	public void entityConfigurationFromConversionService() {
		EntityConfiguration clientConfiguration = conversionService.convert( "client", EntityConfiguration.class );

		assertNotNull( clientConfiguration );
		assertEquals( Client.class, clientConfiguration.getEntityType() );

		EntityConfiguration notExisting = conversionService.convert( "someUnexistingEntity",
		                                                             EntityConfiguration.class );
		assertNull( notExisting );
	}

	@Test
	public void entityConverter() {
		Client client = new Client();
		client.setNewEntityId( 123L );
		client.setName( "Known client name" );

		clientRepository.save( client );

		Client converted = conversionService.convert( "", Client.class );
		assertNull( converted );

		converted = conversionService.convert( 123, Client.class );
		assertEquals( client, converted );

		converted = conversionService.convert( "123", Client.class );
		assertEquals( client, converted );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void verifyEntityModel() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		EntityModel<Client, Long> model = (EntityModel<Client, Long>) configuration.getEntityModel();

		Client existing = model.findOne( 10L );
		assertNull( existing );

		Client created = model.createNew();
		assertNotNull( created );
		assertTrue( model.isNew( created ) );

		created.setNewEntityId( 10L );
		created.setName( "Some name" );

		created = model.save( created );
		assertEquals( Long.valueOf( 10 ), created.getId() );
		assertFalse( model.isNew( created ) );

		existing = model.findOne( 10L );
		assertNotNull( existing );
		assertEquals( "Some name", existing.getName() );

		Client dto = model.createDto( created );
		assertNotSame( created, dto );
		assertEquals( created.getId(), dto.getId() );
		assertEquals( created.getName(), dto.getName() );

		dto.setName( "Modified name" );
		model.save( dto );

		existing = model.findOne( 10L );
		assertNotNull( existing );
		assertEquals( "Modified name", existing.getName() );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Bean
		public ConversionService conversionService() {
			return new DefaultFormattingConversionService( true );
		}

		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new EntityModule() );

			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.setExposeFilter( new ClassBeanFilter( ClientRepository.class ) );
			context.addModule( springDataJpaModule );
		}
	}
}
