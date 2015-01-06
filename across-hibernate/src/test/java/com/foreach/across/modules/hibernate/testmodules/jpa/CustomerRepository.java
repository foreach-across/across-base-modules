package com.foreach.across.modules.hibernate.testmodules.jpa;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Exposed
@Repository
@Transactional(value = HibernateJpaConfiguration.TRANSACTION_MANAGER, readOnly = true)
public class CustomerRepository
{
	@PersistenceContext
	private EntityManager em;

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public Customer save( Customer customer ) {
		if ( customer.getId() == null ) {
			em.persist( customer );
			return customer;
		}
		else {
			return em.merge( customer );
		}
	}

	@SuppressWarnings("unchecked")
	public List<Customer> getAll() {
		return em.createQuery( "select c from Customer c" ).getResultList();
	}

	public Object getUnwrappedEntityManager() {
		return em.getDelegate();
	}

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public void delete( Customer customer ) {
		em.remove( em.find( Customer.class, customer.getId() ) );
	}
}
