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
package com.foreach.across.modules.hibernate.jpa.services;

import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Andy Somers
 */
@Component
public class JpaHibernateSessionHolderImpl implements HibernateSessionHolder
{
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	@Autowired
	public void setEntityManagerFactory( EntityManagerFactory entityManagerFactory ) {
		this.entityManagerFactory = entityManagerFactory;

		// Create a shared entity manager, based on behavior of PersistenceAnnotationBeanPostProcessor
		// does not use a @PersistenceContext annotation as to ensure use of the local entity manager from the module,
		// where the persistence unit name can be configured
		this.entityManager = SharedEntityManagerCreator.createSharedEntityManager( entityManagerFactory, null, true );
	}

	@Override
	public Session getCurrentSession() {
		return entityManager.unwrap( Session.class );
	}

	@Override
	public Session openSession() {
		return entityManagerFactory.createEntityManager().unwrap( Session.class );
	}
}
