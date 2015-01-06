package com.foreach.across.modules.hibernate.jpa;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.hibernate.aop.EntityInterceptor;
import com.foreach.across.modules.hibernate.testmodules.jpa.Customer;
import com.foreach.across.modules.hibernate.testmodules.jpa.CustomerRepository;
import com.foreach.across.modules.hibernate.testmodules.jpa.SimpleJpaModule;
import com.foreach.across.modules.hibernate.testmodules.springdata.Client;
import com.foreach.across.modules.hibernate.testmodules.springdata.ClientRepository;
import com.foreach.across.modules.hibernate.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestInterceptors.Config.class)
public class TestInterceptors
{
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private EntityInterceptor<Customer> customerInterceptor;

	@Autowired
	private EntityInterceptor<Client> clientInterceptor;

	@SuppressWarnings("unchecked")
	@Before
	public void resetMocks() {
		reset( customerInterceptor, clientInterceptor );

		when( customerInterceptor.getEntityClass() ).thenReturn( Customer.class );
		when( clientInterceptor.getEntityClass() ).thenReturn( Client.class );
	}

	@Test
	public void crudCustomer() {
		Customer customer = new Customer();
		customer.setName( UUID.randomUUID().toString() );

		verify( customerInterceptor, never() ).beforeCreate( any( Customer.class ) );
		verify( customerInterceptor, never() ).afterCreate( any( Customer.class ) );
		verify( customerInterceptor, never() ).beforeUpdate( any( Customer.class ) );
		verify( customerInterceptor, never() ).afterUpdate( any( Customer.class ) );
		verify( customerInterceptor, never() ).beforeDelete( any( Customer.class ), any( Boolean.class ) );
		verify( customerInterceptor, never() ).afterDelete( any( Customer.class ), any( Boolean.class ) );

		customerRepository.save( customer );

		verify( customerInterceptor, times( 1 ) ).beforeCreate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).afterCreate( any( Customer.class ) );
		verify( customerInterceptor, never() ).beforeUpdate( any( Customer.class ) );
		verify( customerInterceptor, never() ).afterUpdate( any( Customer.class ) );
		verify( customerInterceptor, never() ).beforeDelete( any( Customer.class ), any( Boolean.class ) );
		verify( customerInterceptor, never() ).afterDelete( any( Customer.class ), any( Boolean.class ) );

		Customer update = new Customer();
		update.setId( customer.getId() );
		update.setName( "updated" );

		customerRepository.save( update );

		verify( customerInterceptor, times( 1 ) ).beforeCreate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).afterCreate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).beforeUpdate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).afterUpdate( any( Customer.class ) );
		verify( customerInterceptor, never() ).beforeDelete( any( Customer.class ), any( Boolean.class ) );
		verify( customerInterceptor, never() ).afterDelete( any( Customer.class ), any( Boolean.class ) );

		customerRepository.delete( customer );

		verify( customerInterceptor, times( 1 ) ).beforeCreate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).afterCreate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).beforeUpdate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).afterUpdate( any( Customer.class ) );
		verify( customerInterceptor, times( 1 ) ).beforeDelete( any( Customer.class ), eq( Boolean.FALSE ) );
		verify( customerInterceptor, times( 1 ) ).afterDelete( any( Customer.class ), eq( Boolean.FALSE ) );

		verify( clientInterceptor, atLeastOnce() ).getEntityClass();
		verifyNoMoreInteractions( clientInterceptor );
	}

	@Test
	public void crudClient() {
		Client client = new Client();
		client.setName( UUID.randomUUID().toString() );

		verify( clientInterceptor, never() ).beforeCreate( any( Client.class ) );
		verify( clientInterceptor, never() ).afterCreate( any( Client.class ) );
		verify( clientInterceptor, never() ).beforeUpdate( any( Client.class ) );
		verify( clientInterceptor, never() ).afterUpdate( any( Client.class ) );
		verify( clientInterceptor, never() ).beforeDelete( any( Client.class ), any( Boolean.class ) );
		verify( clientInterceptor, never() ).afterDelete( any( Client.class ), any( Boolean.class ) );

		clientRepository.save( client );

		verify( clientInterceptor, times( 1 ) ).beforeCreate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).afterCreate( any( Client.class ) );
		verify( clientInterceptor, never() ).beforeUpdate( any( Client.class ) );
		verify( clientInterceptor, never() ).afterUpdate( any( Client.class ) );
		verify( clientInterceptor, never() ).beforeDelete( any( Client.class ), any( Boolean.class ) );
		verify( clientInterceptor, never() ).afterDelete( any( Client.class ), any( Boolean.class ) );

		Client update = new Client();
		update.setId( client.getId() );
		update.setName( "updated" );

		clientRepository.save( update );

		verify( clientInterceptor, times( 1 ) ).beforeCreate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).afterCreate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).beforeUpdate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).afterUpdate( any( Client.class ) );
		verify( clientInterceptor, never() ).beforeDelete( any( Client.class ), any( Boolean.class ) );
		verify( clientInterceptor, never() ).afterDelete( any( Client.class ), any( Boolean.class ) );

		clientRepository.delete( client );

		verify( clientInterceptor, times( 1 ) ).beforeCreate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).afterCreate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).beforeUpdate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).afterUpdate( any( Client.class ) );
		verify( clientInterceptor, times( 1 ) ).beforeDelete( any( Client.class ), eq( Boolean.FALSE ) );
		verify( clientInterceptor, times( 1 ) ).afterDelete( any( Client.class ), eq( Boolean.FALSE ) );

		verify( customerInterceptor, atLeastOnce() ).getEntityClass();
		verifyNoMoreInteractions( customerInterceptor );
	}

	@Configuration
	@SuppressWarnings("unchecked")
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Bean
		public EntityInterceptor<Customer> customerInterceptor() {
			return mock( EntityInterceptor.class );
		}

		@Bean
		public EntityInterceptor<Client> clientInterceptor() {
			return mock( EntityInterceptor.class );
		}

		@Override
		public void configure( AcrossContext context ) {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			context.addModule( hibernateModule );

			context.addModule( new SimpleJpaModule() );

			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.setExposeFilter( new ClassBeanFilter( ClientRepository.class ) );
			context.addModule( springDataJpaModule );
		}
	}
}
