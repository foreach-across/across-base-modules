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

	public Customer getByName( String name ) {
		List list = em.createQuery( "select c from Customer c where name = :customerName" )
		              .setParameter( "customerName", name )
		              .getResultList();

		return (Customer) ( list.isEmpty() ? null : list.get( 0 ) );
	}

	public Object getUnwrappedEntityManager() {
		return em.getDelegate();
	}

	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public void delete( Customer customer ) {
		em.remove( em.find( Customer.class, customer.getId() ) );
	}
}
