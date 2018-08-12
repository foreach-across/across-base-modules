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

import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import com.foreach.across.modules.hibernate.jpa.services.JpaHibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.modules.config.EnableTransactionManagementConfiguration;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;

import static com.foreach.across.test.support.AcrossTestBuilders.standard;
import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.Assert.*;

public class TestJpaModuleSettings
{
	@Test
	public void defaultSettings() {
		try (
				AcrossTestContext ctx = standard()
						.modules( AcrossHibernateJpaModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			assertNotNull( ctx.getBeanOfType( EntityManagerFactory.class ) );
			assertNotNull( ctx.getBeanOfType( PlatformTransactionManager.class ) );
			assertNotNull( ctx.getBeanOfType( TransactionTemplate.class ) );
			assertSame( ctx.getBeanOfType( PlatformTransactionManager.class ), ctx.getBean( "jpaTransactionManager" ) );
			assertSame( ctx.getBeanOfType( TransactionTemplate.class ), ctx.getBean( "jpaTransactionTemplate" ) );

			// transaction manager aliases should also be registered
			assertSame( ctx.getBeanOfType( PlatformTransactionManager.class ), ctx.getBean( "transactionManager" ) );
			assertSame( ctx.getBeanOfType( TransactionTemplate.class ), ctx.getBean( "transactionTemplate" ) );

			assertEquals( 0, ctx.getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					1,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( EnableTransactionManagementConfiguration.class )
					   .size()
			);
		}
	}

	@Test
	public void unitOfWorkFactory() {
		try (
				AcrossTestContext ctx = standard()
						.property( "acrossHibernate.create-unit-of-work-factory", true )
						.modules( AcrossHibernateJpaModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			assertNotNull( ctx.getBeanOfType( EntityManagerFactory.class ) );
			assertNotNull( ctx.getBeanOfType( PlatformTransactionManager.class ) );
			assertNotNull( ctx.getBeanOfType( UnitOfWorkFactory.class ) );
		}
	}

	@Test
	public void noInterceptorOrFilterIfWebModuleButHandlerIsNone() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.NONE )
						.modules( AcrossWebModule.NAME, AcrossHibernateJpaModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void noInterceptorIfNoAcrossWebModule() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.INTERCEPTOR )
						.modules( AcrossHibernateJpaModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void noFilterIfNoAcrossWebModule() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.FILTER )
						.modules( AcrossHibernateJpaModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void interceptorIfWebContext() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.INTERCEPTOR )
						.modules( AcrossWebModule.NAME, AcrossHibernateJpaModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 1, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void filterIfWebContext() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.FILTER )
						.modules( AcrossWebModule.NAME, AcrossHibernateJpaModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 1, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void hibernateSessionHolderIsJpaImplementation() throws Exception {
		try (
				AcrossTestContext ctx = standard()
						.modules( AcrossHibernateJpaModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			HibernateSessionHolder sessionHolder = ctx.getBeanOfType( HibernateSessionHolder.class );
			assertTrue( sessionHolder instanceof JpaHibernateSessionHolderImpl );
		}
	}
}
