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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.context.configurer.TransactionManagementConfigurer;
import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.test.AcrossTestContext;
import com.foreach.across.test.AcrossTestWebContext;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.Assert.*;

public class TestModuleSettings
{
	@Test
	public void defaultSettings() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				context.addModule( new AcrossHibernateModule() );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( SessionFactory.class ) );
			assertNotNull( ctx.beanRegistry().getBeanOfType( PlatformTransactionManager.class ) );
			assertEquals( 0, ctx.beanRegistry().getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					1,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( TransactionManagementConfigurer.Config.class )
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
	public void noTransactions() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.CREATE_TRANSACTION_MANAGER, false );

				context.addModule( module );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( SessionFactory.class ) );
			assertTrue( ctx.beanRegistry().getBeansOfType( PlatformTransactionManager.class ).isEmpty() );
			assertEquals( 0, ctx.beanRegistry().getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					0,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( TransactionManagementConfigurer.Config.class )
					   .size()
			);
		}
	}

	@Test
	public void noRepositoryInterceptor() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.REGISTER_REPOSITORY_INTERCEPTOR, false );

				context.addModule( module );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( SessionFactory.class ) );
			assertNotNull( ctx.beanRegistry().getBeanOfType( PlatformTransactionManager.class ) );
			assertEquals( 0, ctx.beanRegistry().getBeansOfType( UnitOfWorkFactory.class ).size() );
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
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.CREATE_UNITOFWORK_FACTORY, true );

				context.addModule( module );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( SessionFactory.class ) );
			assertNotNull( ctx.beanRegistry().getBeanOfType( PlatformTransactionManager.class ) );
			assertNotNull( ctx.beanRegistry().getBeanOfType( UnitOfWorkFactory.class ) );
		}
	}

	@Test
	public void noInterceptorOrFilterIfWebContextButNotEnabled() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestWebContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void noInterceptorIfNoWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.INTERCEPTOR );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void noFilterIfNoWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.FILTER );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void interceptorIfWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.INTERCEPTOR );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestWebContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 1, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void filterIfWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateModule module = new AcrossHibernateModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.FILTER );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestWebContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenSessionInViewInterceptor.class ).size() );
			assertEquals( 1, module.getBeansOfType( OpenSessionInViewFilter.class ).size() );
		}
	}

	@Test
	public void hibernateSessionHolderIsNormalImplementation() throws Exception {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				context.addModule( new AcrossHibernateModule() );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			HibernateSessionHolder sessionHolder = ctx.beanRegistry().getBeanOfType( HibernateSessionHolder.class );
			assertTrue( sessionHolder instanceof HibernateSessionHolderImpl );
		}
	}
}
