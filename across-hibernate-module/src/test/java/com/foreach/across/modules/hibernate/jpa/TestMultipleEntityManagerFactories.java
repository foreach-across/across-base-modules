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
import com.foreach.across.modules.hibernate.jpa.services.JpaHibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * Test that HibernateSessionHolder boots with the correct entity manager.
 *
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestMultipleEntityManagerFactories.Config.class)
public class TestMultipleEntityManagerFactories
{
	@Autowired
	private HibernateSessionHolder hibernateSessionHolder;

	@PersistenceContext(unitName = AcrossHibernateJpaModule.NAME)
	private EntityManager entityManager;

	@Transactional
	@Test
	public void hibernateSessionHolderUsesLocalEntityManager() {
		assertNotNull( hibernateSessionHolder );
		assertTrue( hibernateSessionHolder instanceof JpaHibernateSessionHolderImpl );

		assertSame( entityManager.unwrap( Session.class ), hibernateSessionHolder.getCurrentSession() );
	}

	@Entity
	public static class MyEntity {
		@Id
		private Long id;
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Bean
		@Lazy
		public LocalContainerEntityManagerFactoryBean anotherEntityManagerFactory( DataSource acrossDataSource ) {
			HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
			vendorAdapter.setDatabase( Database.HSQL );

			LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
			factory.setJpaVendorAdapter( vendorAdapter );
			factory.setDataSource( acrossDataSource );
			factory.setPackagesToScan( getClass().getPackage().getName() );

			return factory;
		}

		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateJpaModule hibernateJpaModule = new AcrossHibernateJpaModule();
			hibernateJpaModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );

			context.addModule( hibernateJpaModule );

			context.addModule( new SimpleJpaModule() );
		}
	}
}
