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
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.modules.hibernate.testmodules.springdata.Client;
import com.foreach.across.modules.hibernate.testmodules.springdata.ClientRepository;
import com.foreach.across.modules.hibernate.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.hibernate.util.DtoUtils;
import com.foreach.across.test.AcrossTestConfiguration;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import org.assertj.core.api.Condition;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestDtoUtilsLazyProperties.Config.class)
public class TestDtoUtilsLazyProperties
{
	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private HibernateSessionHolder hibernateSessionHolder;

	@After
	public void cleanUp() {
		clientRepository.deleteAll();
	}

	@Test
	// todo make configurable?
	public void sessionClosedSkipsLazyPropertiesIfMarkingNonInitializedPropertiesAsMapped() {
		Client jef = initializeClientWithLazyProperty();

		Mapper build = DozerBeanMapperBuilder.create()
		                                     .withCustomFieldMapper( ( source, destination, sourceFieldValue, classMap, fieldMapping ) -> {
			                                     //if field is initialized, Dozer will continue mapping
			                                     return !Hibernate.isInitialized( sourceFieldValue );
		                                     } )
		                                     .build();
		Client dto = build.map( jef, jef.getClass() );
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( jef );
		assertThat( dto.getName() ).isEqualTo( jef.getName() );
		assertThat( dto.getLinkedClient() ).isNull();
	}

	@Test
	public void sessionClosedInitializesTargetObjectOnFirstMethodCall() {
		Client jef = initializeClientWithLazyProperty();

		Mapper build = DozerBeanMapperBuilder.create()
		                                     .withCustomFieldMapper( ( source, destination, sourceFieldValue, classMap, fieldMapping ) -> {
			                                     if ( !Hibernate.isInitialized( sourceFieldValue ) ) {
				                                     Object srcObject = sourceFieldValue;
				                                     Enhancer enhancer = new Enhancer();
				                                     enhancer.setSuperclass( destination.getClass() );

				                                     enhancer.setCallback( (MethodInterceptor) ( obj, method, args, proxy ) -> {
					                                     if ( !Hibernate.isInitialized( srcObject ) ) {
						                                     try (Session session = hibernateSessionHolder.openSession()) {
							                                     session.update( srcObject );
							                                     Hibernate.initialize( srcObject );
						                                     }
					                                     }
					                                     return method.invoke( srcObject, args );
				                                     } );
				                                     fieldMapping.writeDestValue( destination, enhancer.create() );
				                                     return true;
			                                     }
			                                     // Dozer takes over mapping
			                                     return false;
		                                     } )
		                                     .build();
		Client dto = build.map( jef, jef.getClass() );
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( jef );
		assertThat( dto.getName() ).isEqualTo( jef.getName() );
		assertThat( dto.getLinkedClient() ).isNotNull()
		                                   .isNotSameAs( jef.getLinkedClient() );
		assertThat( dto.getLinkedClient().getName() ).isEqualTo( "josh" );
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
//			if ( applicationContext instanceof GenericApplicationContext ) {
//				( (GenericApplicationContext) applicationContext ).setClassLoader( new FilteredClassLoader( "com.github.dozermapper" ) );
//			}
			context.addModule( hibernateModule );

			SimpleJpaModule simpleJpaModule = new SimpleJpaModule();
			simpleJpaModule.addApplicationContextConfigurer( TestJpaInterceptors.AllInterceptorConfig.class );
			context.addModule( simpleJpaModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.addApplicationContextConfigurer( TestJpaInterceptors.CustomerAndClientInterceptorConfig.class );
			context.addModule( springDataJpaModule );
		}
	}
}
