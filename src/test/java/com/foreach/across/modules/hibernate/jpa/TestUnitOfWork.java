package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.testmodules.jpa.CustomerRepository;
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestUnitOfWork.Config.class)
public class TestUnitOfWork
{
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UnitOfWorkFactory unitOfWork;

	@Test
	public void withinUnitOfWorkEntityManagerStaysTheSame() {
		Object beforeUnit = customerRepository.getUnwrappedEntityManager();

		unitOfWork.start();

		Object withinUnit = customerRepository.getUnwrappedEntityManager();

		assertNotSame( beforeUnit, withinUnit );
		assertSame( withinUnit, customerRepository.getUnwrappedEntityManager() );

		unitOfWork.stop();

		assertNotSame( withinUnit, customerRepository.getUnwrappedEntityManager() );
	}

	@Test
	public void withoutUnitOfWorkEntityManagerChangesOnEachCall() {
		Object instance = customerRepository.getUnwrappedEntityManager();
		Object instanceTwo = customerRepository.getUnwrappedEntityManager();
		Object instanceThree = customerRepository.getUnwrappedEntityManager();

		assertNotSame( instance, instanceTwo );
		assertNotSame( instance, instanceThree );
		assertNotSame( instanceThree, instanceTwo );
	}

	@Test
	public void restartCausesEntityManagerToChange() {
		Object beforeUnit = customerRepository.getUnwrappedEntityManager();

		unitOfWork.start();

		Object withinUnit = customerRepository.getUnwrappedEntityManager();

		assertNotSame( beforeUnit, withinUnit );
		assertSame( withinUnit, customerRepository.getUnwrappedEntityManager() );

		unitOfWork.restart();

		Object restartedUnit = customerRepository.getUnwrappedEntityManager();

		assertNotSame( withinUnit, restartedUnit );
		assertSame( restartedUnit, customerRepository.getUnwrappedEntityManager() );

		unitOfWork.stop();
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setProperty( AcrossHibernateModuleSettings.CREATE_UNITOFWORK_FACTORY, true );
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			context.addModule( new SimpleJpaModule() );
		}
	}
}
