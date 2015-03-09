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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.testmodules.springdata.business.Representative;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import javax.validation.metadata.PropertyDescriptor;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestRepositoryEntityRegistrar
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ClientRepository clientRepository;

	@EntityValidator
	private SmartValidator entityValidator;

	@Test
	public void clientShouldBeRegisteredWithRepositoryInformation() {
		//assertEquals( 4, entityRegistry.getEntities().size() );
		assertTrue( entityRegistry.contains( Client.class ) );

		EntityConfiguration<?> configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertNotNull( configuration );

		CrudRepository<Client, Long> repository = configuration.getAttribute( Repository.class );
		assertNotNull( repository );

		RepositoryFactoryInformation<Client, Long> repositoryFactoryInformation
				= configuration.getAttribute( RepositoryFactoryInformation.class );
		assertNotNull( repositoryFactoryInformation );

		EntityModel model = configuration.getEntityModel();
		assertNotNull( model );

		EntityViewFactory viewFactory = configuration.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( viewFactory );
	}

	@Ignore
	@Test
	public void companyShouldHaveAnAssociationToItsRepresentatives() throws Exception {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = configuration.association( "representatives" );

		assertNotNull( association );
		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}

	@Ignore
	@Test
	public void representativeShouldHaveAnAssociationToItsCompanies() throws Exception {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Representative.class );
		EntityAssociation association = configuration.association( "company.representatives" );

		assertNotNull( association );
		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}

	@Test
	public void verifyPropertyRegistry() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		EntityPropertyRegistry registry = configuration.getPropertyRegistry();

		assertProperty( registry, "name", "Name", true, true );
		assertProperty( registry, "id", "Id", true, false );
		assertProperty( registry, "company", "Company", true, false );
		assertProperty( registry, "newEntityId", "New entity id", false, false );
		assertProperty( registry, "nameWithId", "Name with id", false, false );
		assertProperty( registry, "class", "Class", false, false );
	}

	@Test
	public void validatorShouldBeRegistered() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		Validator validator = configuration.getAttribute( Validator.class );

		assertNotNull( validator );
		assertSame( entityValidator, validator );

		configuration = entityRegistry.getEntityConfiguration( Company.class );
		assertNotNull( configuration );

		validator = configuration.getAttribute( Validator.class );
		assertNotNull( validator );
		assertNotSame( entityValidator, validator );

		validator.validate( new Company(), null );
	}

	private void assertProperty( EntityPropertyRegistry registry,
	                             String propertyName,
	                             String displayName,
	                             boolean sortable,
	                             boolean hasValidators ) {
		EntityPropertyDescriptor descriptor = registry.getProperty( propertyName );
		assertNotNull( propertyName );
		assertEquals( propertyName, descriptor.getName() );
		assertEquals( displayName, descriptor.getDisplayName() );

		if ( sortable ) {
			assertEquals( propertyName, descriptor.getAttribute( EntityAttributes.SORTABLE_PROPERTY ) );
		}
		else {
			assertFalse( descriptor.hasAttribute( EntityAttributes.SORTABLE_PROPERTY ) );
		}

		if ( hasValidators ) {
			assertNotNull( descriptor.getAttribute( PropertyDescriptor.class ) );
		}
		else {
			assertFalse( descriptor.hasAttribute( PropertyDescriptor.class ) );
		}
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
		EntityConfiguration<Client> configuration = entityRegistry.getEntityConfiguration( Client.class );
		EntityModel<Client, Long> model = (EntityModel<Client, Long>) configuration.getEntityModel();

		Client existing = model.findOne( 10L );
		assertNull( existing );

		Client created = model.createNew();
		assertNotNull( created );
		assertTrue( model.isNew( created ) );

		created.setNewEntityId( 10L );
		created.setName( "Some name" );

		assertEquals( "Some name", model.getLabel( created ) );

		created = model.save( created );
		assertEquals( Long.valueOf( 10 ), created.getId() );
		assertFalse( model.isNew( created ) );

		existing = model.findOne( 10L );
		assertNotNull( existing );
		assertEquals( "Some name", existing.getName() );
		assertEquals( "Some name", model.getLabel( created ) );

		Client dto = model.createDto( created );
		assertNotSame( created, dto );
		assertEquals( created.getId(), dto.getId() );
		assertEquals( created.getName(), dto.getName() );

		dto.setName( "Modified name" );
		model.save( dto );

		existing = model.findOne( 10L );
		assertNotNull( existing );
		assertEquals( "Modified name", existing.getName() );
		assertEquals( "Modified name", model.getLabel( existing ) );
	}

	@Test
	public void verifyListView() {
		EntityConfiguration<Client> configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertTrue( configuration.hasView( EntityListView.VIEW_NAME ) );

		EntityListViewFactory viewFactory = configuration.getViewFactory( EntityListView.VIEW_NAME );
		assertNotNull( viewFactory );

		assertNotNull( viewFactory.getPageFetcher() );
		assertEquals( 50, viewFactory.getPageSize() );
		assertNull( viewFactory.getSortableProperties() );
		assertEquals( new Sort( "name" ), viewFactory.getDefaultSort() );
	}

	@Test
	public void verifyCreateView() {
		EntityConfiguration<Client> configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertTrue( configuration.hasView( EntityFormView.CREATE_VIEW_NAME ) );

		EntityFormViewFactory viewFactory = configuration.getViewFactory( EntityFormView.CREATE_VIEW_NAME );
		assertNotNull( viewFactory );
	}

	@Configuration
	@AcrossTestWebConfiguration
	public static class Config implements AcrossContextConfigurer
	{
		@Bean
		public ConversionService conversionService() {
			return new DefaultFormattingConversionService( true );
		}

		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityModule() );
			context.addModule( new AdminWebModule() );
			context.addModule( new EntityModule() );
			context.setDevelopmentMode( true );

			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.setExposeFilter( new ClassBeanFilter( Repository.class ) );
			context.addModule( springDataJpaModule );
		}
	}
}
