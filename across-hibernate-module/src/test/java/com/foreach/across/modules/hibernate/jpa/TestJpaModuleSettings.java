package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.context.configurer.TransactionManagementConfigurer;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import com.foreach.across.modules.hibernate.jpa.services.JpaHibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;

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
			assertEquals( 0, ctx.getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					1,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( TransactionManagementConfigurer.Config.class )
					   .size()
			);
		}
	}

	@Test
	public void noTransactions() {
		try (
				AcrossTestContext ctx = standard()
						.property( AcrossHibernateModuleSettings.CREATE_TRANSACTION_MANAGER, false )
						.modules( AcrossHibernateJpaModule.NAME )
						.modules( new EmptyAcrossModule( "client" ) )
						.build()
		) {
			assertNotNull( ctx.getBeanOfType( EntityManagerFactory.class ) );
			assertTrue( ctx.getBeansOfType( PlatformTransactionManager.class ).isEmpty() );
			assertEquals( 0, ctx.getBeansOfType( UnitOfWorkFactory.class ).size() );
			assertEquals(
					0,
					ctx.contextInfo().getModuleInfo( "client" ).getApplicationContext()
					   .getBeansOfType( TransactionManagementConfigurer.Config.class )
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
