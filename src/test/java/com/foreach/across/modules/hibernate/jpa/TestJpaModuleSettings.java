package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.context.configurer.TransactionManagementConfigurer;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.config.PersistenceContextInView;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.test.AcrossTestContext;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

import static org.junit.Assert.*;

public class TestJpaModuleSettings
{
	@Test
	public void defaultSettings() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				context.addModule( new AcrossHibernateJpaModule() );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( EntityManagerFactory.class ) );
			assertNotNull( ctx.beanRegistry().getBeanOfType( PlatformTransactionManager.class ) );
			assertEquals( 0, ctx.beanRegistry().getBeansOfType( UnitOfWorkFactory.class ).size() );
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
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				module.setProperty( AcrossHibernateModuleSettings.CREATE_TRANSACTION_MANAGER, false );

				context.addModule( module );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( EntityManagerFactory.class ) );
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
	public void unitOfWorkFactory() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				module.setProperty( AcrossHibernateModuleSettings.CREATE_UNITOFWORK_FACTORY, true );

				context.addModule( module );
				context.addModule( new EmptyAcrossModule( "client" ) );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			assertNotNull( ctx.beanRegistry().getBeanOfType( EntityManagerFactory.class ) );
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
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestWebContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void noInterceptorIfNoWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.INTERCEPTOR );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void noFilterIfNoWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.FILTER );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void interceptorIfWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.INTERCEPTOR );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestWebContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 1, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}

	@Test
	public void filterIfWebContext() {
		AcrossContextConfigurer config = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext context ) {
				AcrossHibernateJpaModule module = new AcrossHibernateJpaModule();
				module.setProperty( AcrossHibernateModuleSettings.PERSISTENCE_CONTEXT_VIEW_HANDLER,
				                    PersistenceContextInView.FILTER );
				context.addModule( module );
			}
		};

		try (AcrossTestContext ctx = new AcrossTestWebContext( config )) {
			ApplicationContext module = ctx.contextInfo().getModuleInfo( AcrossHibernateJpaModule.NAME )
			                               .getApplicationContext();

			assertEquals( 0, module.getBeansOfType( OpenEntityManagerInViewInterceptor.class ).size() );
			assertEquals( 1, module.getBeansOfType( OpenEntityManagerInViewFilter.class ).size() );
		}
	}
}
