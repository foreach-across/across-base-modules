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

import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.modules.config.EnableTransactionManagementConfiguration;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestContext;
import com.foreach.across.test.AcrossTestWebContext;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static com.foreach.across.test.support.AcrossTestBuilders.standard;
import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.Assert.*;

public class TestModuleSettings
{
	@Test
	public void defaultSettings() {
		try (
				AcrossTestContext ctx = standard()
						.modules( AcrossHibernateModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			assertNotNull( ctx.getBeanOfType( SessionFactory.class ) );
			assertNotNull( ctx.getBeanOfType( PlatformTransactionManager.class ) );
			assertNotNull( ctx.getBeanOfType( TransactionTemplate.class ) );
			assertSame( ctx.getBeanOfType( PlatformTransactionManager.class ), ctx.getBean( "transactionManager" ) );
			assertSame( ctx.getBeanOfType( TransactionTemplate.class ), ctx.getBean( "transactionTemplate" ) );
			assertEquals( 0, ctx.getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					1,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( EnableTransactionManagementConfiguration.class )
					   .size()
			);
			assertEquals(
					1,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( ModuleBasicRepositoryInterceptorConfiguration.class )
					   .size()
			);
		}
	}

	@Test
	public void noRepositoryInterceptor() {
		try (
				AcrossTestContext ctx = standard()
						.property( AcrossHibernateModuleSettings.REGISTER_REPOSITORY_INTERCEPTOR, false )
						.modules( AcrossHibernateModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			assertNotNull( ctx.getBeanOfType( SessionFactory.class ) );
			assertNotNull( ctx.getBeanOfType( PlatformTransactionManager.class ) );
			assertEquals( 0, ctx.getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					0,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( ModuleBasicRepositoryInterceptorConfiguration.class )
					   .size()
			);
		}
	}

	@Test
	public void unitOfWorkFactory() {
		try (
				AcrossTestContext ctx = standard()
						.property( AcrossHibernateModuleSettings.CREATE_UNITOFWORK_FACTORY, true )
						.modules( AcrossHibernateModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			assertNotNull( ctx.getBeanOfType( SessionFactory.class ) );
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
						.modules( AcrossWebModule.NAME, AcrossHibernateModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void noInterceptorIfNoAcrossWebModule() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.INTERCEPTOR )
						.modules( AcrossHibernateModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void noFilterIfNoAcrossWebModule() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.FILTER )
						.modules( AcrossHibernateModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void interceptorIfWebContext() {
		try (
				AcrossTestContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.INTERCEPTOR )
						.modules( AcrossWebModule.NAME, AcrossHibernateModule.NAME )
						.build()
		) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 1, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void filterIfWebContext() {
		try (
				AcrossTestWebContext ctx = web()
						.property( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
						           PersistenceContextInView.FILTER )
						.modules( AcrossWebModule.NAME, AcrossHibernateModule.NAME )
						.build()
		) {

			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 1, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
			assertNotNull( ctx.getServletContext().getFilterRegistration( AcrossHibernateModule.NAME + ".OpenSessionInViewFilter" ) );
		}
	}

	@Test
	public void hibernateSessionHolderIsNormalImplementation() throws Exception {
		try (
				AcrossTestContext ctx = standard()
						.modules( AcrossHibernateModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			HibernateSessionHolder sessionHolder = ctx.getBeanOfType( HibernateSessionHolder.class );
			assertTrue( sessionHolder instanceof HibernateSessionHolderImpl );
		}
	}
}
