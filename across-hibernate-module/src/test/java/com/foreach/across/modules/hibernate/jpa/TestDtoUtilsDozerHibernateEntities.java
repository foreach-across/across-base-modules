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
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.testmodules.springdata.*;
import com.foreach.across.modules.hibernate.util.DozerMapperCustomizationRegistry;
import com.foreach.across.modules.hibernate.util.DtoUtils;
import com.foreach.across.modules.hibernate.util.HibernateProxyDozerFieldMapper;
import com.foreach.across.test.AcrossTestConfiguration;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static com.foreach.across.modules.hibernate.util.DozerMapperCustomizationRegistry.customFieldMapperRegistrar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestDtoUtilsDozerHibernateEntities.Config.class)
public class TestDtoUtilsDozerHibernateEntities
{
	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private HibernateSessionHolder hibernateSessionHolder;

	@Autowired
	private DozerMapperCustomizationRegistry dozerMapperCustomizationRegistry;

	@Autowired
	private Mapper mapper;

	@After
	public void cleanUp() {
		businessRepository.deleteAll();
		clientRepository.deleteAll();
	}

	@Test
	public void sessionClosedSkipsLazyPropertiesIfMarkingNonInitializedPropertiesAsMapped() {
		Client jef = initializeClientWithLazyProperty();

		dozerMapperCustomizationRegistry.register( customFieldMapperRegistrar()
				                                           .customization(
						                                           ( source, destination, sourceFieldValue, classMap, fieldMapping ) -> {
							                                           //if field is initialized, Dozer will continue mapping
							                                           return !Hibernate.isInitialized( sourceFieldValue );
						                                           } )
				                                           .name( "skipHibernateLazyProperties" ) );
		Client dto = mapper.map( jef, jef.getClass() );
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( jef );
		assertThat( dto.getName() ).isEqualTo( jef.getName() );
		assertThat( dto.getLinkedClient() ).isNull();

		dozerMapperCustomizationRegistry.removeCustomFieldMapper( "skipHibernateLazyProperties" );
	}

	@Test
	public void supportsMappingLazyHibernatePropertiesW() {
		Client jef = initializeClientWithLazyProperty();
		dozerMapperCustomizationRegistry.register( customFieldMapperRegistrar()
				                                           .customization(
						                                           new HibernateProxyDozerFieldMapper( mapper, hibernateSessionHolder ) )
				                                           .name( "fetchHibernateLazyPropertiesAndConvert" ) );

		Client dto = mapper.map( jef, jef.getClass() );
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( jef );
		assertThat( dto.getName() ).isEqualTo( jef.getName() );
		assertThat( dto.getLinkedClient() ).isNotNull()
		                                   .isNotSameAs( jef.getLinkedClient() );
		assertThat( dto.getLinkedClient().getName() ).isEqualTo( "josh" );

		dozerMapperCustomizationRegistry.removeCustomFieldMapper( "fetchHibernateLazyPropertiesAndConvert" );
	}

	@Test
	public void supportsMappingLazyHibernateCollectionProperties() {
		Business business = new Business();
		business.setName( "Bizniz" );

		Client jef = initializeClientWithLazyProperty();
		Client jane = createClient( -3L, "jane", null );
		business.setClients( Arrays.asList( jef, jane ) );
		business = businessRepository.save( business );

		dozerMapperCustomizationRegistry.register( customFieldMapperRegistrar()
				                                           .customization(
						                                           new HibernateProxyDozerFieldMapper( mapper, hibernateSessionHolder ) )
				                                           .name( "fetchHibernateLazyPropertiesAndConvert" ) );

		Business dto = mapper.map( business, Business.class );
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( business );
		assertThat( dto.getName() ).isEqualTo( business.getName() );
		assertThat( dto.getClients() ).hasSize( business.getClients().size() )
		                              .isEqualTo( business.getClients() )
		                              .isNotSameAs( business.getClients() );
		assertThat( dto.getClients() )
				.anyMatch( client -> {
					boolean isJef = StringUtils.equals( client.getName(), "jef" );
					if ( isJef ) {
						assertThat( client ).isEqualTo( jef )
						                    .isNotSameAs( jef );
						assertThat( client.getLinkedClient() ).isNotNull()
						                                      .isNotSameAs( jef.getLinkedClient() );
						assertThat( client.getLinkedClient().getName() ).isEqualTo( "josh" );
						return true;
					}
					return false;
				} )
				.anyMatch( client -> {
					boolean isJane = StringUtils.equals( client.getName(), "jane" );
					if ( isJane ) {
						assertThat( client ).isEqualTo( jane )
						                    .isNotSameAs( jane );
						assertThat( client.getLinkedClient() ).isNull();
						return true;
					}
					return false;
				} );

		dozerMapperCustomizationRegistry.removeCustomFieldMapper( "fetchHibernateLazyPropertiesAndConvert" );
	}

	@Test
	public void sessionClosedResultsInMappingExceptionDueToLazyInitializationException() {
		Client jef = initializeClientWithLazyProperty();

		Condition<Throwable> testMappingException = new Condition<>( ( t ) -> {
			Throwable cause = t.getCause();
			if ( cause instanceof InvocationTargetException ) {
				return cause.getCause() instanceof LazyInitializationException;
			}
			return false;
		}, "Mapping exception is caused by an InvocationTargetException caused by a LazyInitializationException" );
		assertThatThrownBy( () -> DtoUtils.createDto( jef ) )
				.isInstanceOf( MappingException.class )
				.hasCauseInstanceOf( InvocationTargetException.class )
				.is( testMappingException );
	}

	@Test
	@Transactional
	public void withSessionKeptOpen() {
		Client jef = initializeClientWithLazyProperty();
		Client dto = DtoUtils.createDto( jef );
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( jef );
		assertThat( dto.getName() ).isEqualTo( jef.getName() );
		assertThat( dto.getLinkedClient() ).isEqualTo( jef.getLinkedClient() )
		                                   .isNotSameAs( jef.getLinkedClient() );
	}

	private Client initializeClientWithLazyProperty() {
		Client josh = createClient( -1L, "josh", null );
		return createClient( -2L, "jef", josh );
	}

	private Client createClient( Long id, String name, Client linkedClient ) {
		Client client = new Client();
		client.setId( id );
		client.setName( name );
		client.setLinkedClient( linkedClient );
		return clientRepository.save( client );
	}

	@Configuration
	@SuppressWarnings("unchecked")
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			context.addModule( springDataJpaModule );
		}
	}
}
