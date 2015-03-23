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
package com.foreach.across.modules.hibernate;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.database.support.HikariDataSourceHelper;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.Hibernate1Module;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.Product;
import com.foreach.across.modules.hibernate.testmodules.hibernate1.ProductRepository;
import com.foreach.across.modules.hibernate.testmodules.hibernate2.Hibernate2Module;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestNoTransactionManager.Config.class)
@DirtiesContext
public class TestNoTransactionManager
{
	private int productId = 10000;

	@Autowired
	private AcrossHibernateModule hibernateModule;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UnitOfWorkFactory unitOfWork;

	@Test
	public void noTransactionManagerShouldExist() {
		assertTrue( AcrossContextUtils.getApplicationContext( hibernateModule ).getBeansOfType(
				PlatformTransactionManager.class ).isEmpty() );
	}

	@Test(expected = HibernateException.class)
	public void withoutExplicitSessionShouldFail() {
		createAndGetProduct();
	}

	@Test
	public void unitOfWorkShouldWork() {
		unitOfWork.start();

		try {
			createAndGetProduct();
		}
		finally {
			unitOfWork.stop();
		}
	}

	private void createAndGetProduct() {
		productId++;

		assertNull( productRepository.getProductWithId( productId ) );

		Product product = new Product( productId, "product " + productId );
		productRepository.save( product );

		Product other = productRepository.getProductWithId( productId );
		assertNotNull( other );
		assertEquals( product, other );
	}

	@Configuration
	static class Config
	{
		@Bean
		public DataSource dataSource() throws Exception {
			return HikariDataSourceHelper.create( "org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:acrosscore", "sa",
			                                      StringUtils.EMPTY );
		}

		@Bean
		public AcrossContext acrossContext( ConfigurableApplicationContext applicationContext ) throws Exception {
			AcrossContext acrossContext = new AcrossContext( applicationContext );
			acrossContext.setDataSource( dataSource() );
			acrossContext.addModule( acrossHibernateModule() );
			acrossContext.addModule( hibernate1Module() );
			acrossContext.addModule( hibernate2Module() );

			acrossContext.bootstrap();

			return acrossContext;
		}

		@Bean
		public AcrossHibernateModule acrossHibernateModule() {
			AcrossHibernateModule module = new AcrossHibernateModule();
			module.setProperty( AcrossHibernateModuleSettings.CREATE_UNITOFWORK_FACTORY, true );
			module.setProperty( AcrossHibernateModuleSettings.CREATE_TRANSACTION_MANAGER, false );
			module.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );

			return module;
		}

		@Bean
		public Hibernate1Module hibernate1Module() {
			return new Hibernate1Module();
		}

		@Bean
		public Hibernate2Module hibernate2Module() {
			return new Hibernate2Module();
		}
	}
}
