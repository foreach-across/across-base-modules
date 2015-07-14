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
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.registry.IncrementalRefreshableRegistry;
import com.foreach.across.core.registry.RefreshableRegistry;
import com.foreach.across.core.transformers.PrimaryBeanTransformer;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.aop.EntityInterceptor;
import com.foreach.across.modules.hibernate.config.HibernateConfiguration;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.Hibernate1Module;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.Product;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.ProductRepository;
import com.foreach.across.modules.hibernate.testmodules.jpa.Customer;
import com.foreach.across.modules.hibernate.testmodules.jpa.CustomerRepository;
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Test combining JPA and non-JPA hibernate module,
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestMixedPersistence.Config.class)
public class TestMixedPersistence
{

	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	public void singleRefreshableRegistryShouldBeIncremental() {
		Map<String, RefreshableRegistry> registries
				= beanRegistry.getBeansOfTypeAsMap(
				TypeDescriptor.collection( RefreshableRegistry.class,
				                           TypeDescriptor.valueOf( EntityInterceptor.class ) )
				              .getResolvableType(),
				true );

		assertEquals( 1, registries.size() );
		assertTrue( beanRegistry.getBean( "entityInterceptors" ) instanceof IncrementalRefreshableRegistry );
	}

	@Test
	public void crudCustomer() {
		List<Customer> customers = customerRepository.getAll();
		assertTrue( customers.isEmpty() );

		Customer customer = new Customer();
		customer.setName( UUID.randomUUID().toString() );

		customerRepository.save( customer );
		assertNotNull( customer.getId() );

		customers = customerRepository.getAll();
		assertEquals( 1, customers.size() );
		assertTrue( customers.contains( customer ) );
	}

	@Test
	public void singleModuleTransactional() {
		assertNull( productRepository.getProductWithId( 1 ) );

		Product product = new Product( 1, "product 1" );
		productRepository.save( product );

		Product other = productRepository.getProductWithId( 1 );
		assertNotNull( other );
		assertEquals( product, other );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateModule hibernateModule = new AcrossHibernateModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			hibernateModule.setExposeTransformer(
					new PrimaryBeanTransformer( Arrays.asList( HibernateConfiguration.TRANSACTION_MANAGER,
					                                           HibernateConfiguration.SESSION_HOLDER ) )
			);
			context.addModule( hibernateModule );

			AcrossHibernateJpaModule hibernateJpaModule = new AcrossHibernateJpaModule();
			hibernateJpaModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateJpaModule );

			context.addModule( new Hibernate1Module() );
			context.addModule( new SimpleJpaModule() );
		}
	}
}
