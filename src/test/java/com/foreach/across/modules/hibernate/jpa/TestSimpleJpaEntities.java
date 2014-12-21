package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.hibernate.testmodules.jpa.Customer;
import com.foreach.across.modules.hibernate.testmodules.jpa.CustomerRepository;
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestSimpleJpaEntities.Config.class)
public class TestSimpleJpaEntities
{
	@Autowired
	private CustomerRepository customerRepository;

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

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			context.addModule( new SimpleJpaModule() );
		}
	}
}
