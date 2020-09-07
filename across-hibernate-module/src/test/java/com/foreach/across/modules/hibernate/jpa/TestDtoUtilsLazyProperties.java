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
import com.github.dozermapper.core.builder.BeanMappingsBuilder;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.BeanCreationStrategy;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import org.assertj.core.api.Condition;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
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
import java.util.Collections;
import java.util.List;

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
	public void sessionClosedSkipsLazyProperties() {
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
		assertThat( dto.getLinkedClient() ).isSameAs( jef.getLinkedClient() );
	}
//
//	public static class ProxyConverter implements CustomConverter, MapperAware
//	{
//
//		@Override
//		public Object convert( Object existingDestinationFieldValue,
//		                       Object sourceFieldValue,
//		                       Class<?> destinationClass,
//		                       Class<?> sourceClass ) {
//			return existingDestinationFieldValue;
//		}
//	}

	@Test
	public void initializeViaProxyUponCall() {
		Client jef = initializeClientWithLazyProperty();

		Mapper build = DozerBeanMapperBuilder.create()
//		                                     .withMappingBuilder( new BeanMappingBuilder()
//		                                     {
//			                                     @Override
//			                                     protected void configure() {
//			                                     	Client.class.getFields()
//				                                     mapping( type(HibernateProxy.class) ).exclude(  )
//			                                     }
//		                                     } )
//		                                     .withCustomFieldMapper( ( source, destination, sourceFieldValue, classMap, fieldMapping ) -> {
//			                                     if ( Hibernate.isInitialized( sourceFieldValue ) ) {
//			                                     	fieldMapping.setType( MappingDirection.ONE_WAY );
//				                                     fieldMapping.setCustomConverter( ProxyConverter.class.getName() );
//				                                     return true;
//			                                     }
//			                                     //if field is initialized, Dozer will continue mapping
//			                                     return false;
//		                                     } )

                                             .withBeanMappingsBuilders( new BeanMappingsBuilder()
                                             {

	                                             @Override
	                                             public List<MappingFileData> build( BeanContainer beanContainer,
	                                                                                 DestBeanCreator destBeanCreator,
	                                                                                 PropertyDescriptorFactory propertyDescriptorFactory ) {
		                                             destBeanCreator.addPluggedStrategy( new BeanCreationStrategy()
		                                             {
			                                             @Override
			                                             public boolean isApplicable( BeanCreationDirective directive ) {
				                                             return HibernateProxy.class.isAssignableFrom(
						                                             directive.getSrcObject().getClass() );
			                                             }

			                                             @Override
			                                             public Object create( BeanCreationDirective directive ) {
				                                             Object srcObject = directive.getSrcObject();
				                                             Enhancer enhancer = new Enhancer();
				                                             enhancer.setSuperclass( directive.getTargetClass() );

				                                             enhancer.setCallback( (MethodInterceptor) ( obj, method, args, proxy ) -> {
					                                             if ( !Hibernate.isInitialized( srcObject ) ) {
						                                             try (Session session = hibernateSessionHolder.openSession()) {
							                                             session.update( srcObject );
							                                             Hibernate.initialize( srcObject );
						                                             }
					                                             }
					                                             return method.invoke( srcObject, args );
				                                             } );
				                                             return enhancer;
			                                             }
		                                             } );
		                                             return Collections.emptyList();
	                                             }
                                             } )
                                             .build();
		Client dto = build.map( jef, jef.getClass() );

		HibernateProxy proxy = (HibernateProxy) jef.getLinkedClient();
		if ( !Hibernate.isInitialized( proxy ) ) {
			try (Session session = hibernateSessionHolder.openSession()) {
				session.update( proxy );
				Hibernate.initialize( proxy );
			}
		}
		assertThat( dto )
				.isNotNull()
				.isNotSameAs( jef );
		assertThat( dto.getName() ).isEqualTo( jef.getName() );
		assertThat( dto.getLinkedClient() ).isEqualTo( jef.getLinkedClient() )
		                                   .isNotSameAs( jef.getLinkedClient() );
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

			SimpleJpaModule simpleJpaModule = new SimpleJpaModule();
			simpleJpaModule.addApplicationContextConfigurer( TestJpaInterceptors.AllInterceptorConfig.class );
			context.addModule( simpleJpaModule );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.addApplicationContextConfigurer( TestJpaInterceptors.CustomerAndClientInterceptorConfig.class );
			context.addModule( springDataJpaModule );
		}
	}
}
