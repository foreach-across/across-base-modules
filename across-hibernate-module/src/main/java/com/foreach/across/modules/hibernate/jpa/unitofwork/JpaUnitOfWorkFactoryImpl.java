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
package com.foreach.across.modules.hibernate.jpa.unitofwork;

import com.foreach.across.modules.hibernate.unitofwork.CallableUnitOfWork;
import com.foreach.across.modules.hibernate.unitofwork.RunnableUnitOfWork;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Implementation of {@link UnitOfWorkFactory} that supports {@link EntityManagerFactory} implementations.
 * A single unit of work is defined by a single {@link EntityManager}.
 */
public class JpaUnitOfWorkFactoryImpl implements UnitOfWorkFactory
{
	private final Collection<EntityManagerFactory> entityManagerFactories;

	public JpaUnitOfWorkFactoryImpl( Collection<EntityManagerFactory> entityManagerFactories ) {
		Assert.notNull( entityManagerFactories );
		this.entityManagerFactories = entityManagerFactories;
	}

	/**
	 * Wraps a Runnable into a unit of work.
	 *
	 * @param runnable Original runnable instance.
	 * @return Wrapped Runnable.
	 */
	public Runnable create( Runnable runnable ) {
		return new RunnableUnitOfWork( this, runnable );
	}

	/**
	 * Wraps a Callable into a unit of work.
	 *
	 * @param callable Original callable instance.
	 * @return Wrapped Callable.
	 */
	public <T> Callable<T> create( Callable<T> callable ) {
		return new CallableUnitOfWork<T>( this, callable );
	}

	/**
	 * When called, this will close and reopen all Sessions attached
	 * to the current thread.
	 */
	public void restart() {
		stop();
		start();
	}

	/**
	 * Starts a new unit of work: opens all Sessions.
	 */
	public UnitOfWorkFactory start() {
		for ( EntityManagerFactory emf : entityManagerFactories ) {
			try {
				if ( !TransactionSynchronizationManager.hasResource( emf ) ) {
					LOG.trace( "Opening entity manager for {}", emf );
					EntityManager entityManager = emf.createEntityManager();

					TransactionSynchronizationManager.bindResource( emf, new EntityManagerHolder( entityManager ) );
				}
				else {
					LOG.trace( "Not opening entity manager for {} as factory is already bound", emf );
				}
			}
			catch ( Exception e ) {
				LOG.error( "Exception starting unit of work for {}", emf, e );
			}
		}
		return this;
	}

	/**
	 * Stops the unit of work: closes all Sessions.
	 */
	public void stop() {
		for ( EntityManagerFactory emf : entityManagerFactories ) {
			try {
				EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager.getResource( emf );

				if ( holder != null ) {
					// If there is still a transaction running, don't close, it should be closed then transaction finishes
					if ( !TransactionSynchronizationManager.isActualTransactionActive() ) {
						LOG.trace( "Closing entity manager for {}", emf );
						EntityManagerFactoryUtils.closeEntityManager( holder.getEntityManager() );
					}
					else {
						LOG.trace( "Not closing entity manager for {} as transaction is active", emf );
					}

					TransactionSynchronizationManager.unbindResource( emf );
				}
			}
			catch ( Exception e ) {
				LOG.error( "Exception stopping unit of work for {}", emf, e );
			}
		}
	}

	/**
	 * Stops the unit of work: closes all Sessions.
	 * delegates to {@link JpaUnitOfWorkFactoryImpl#stop()}.
	 */
	@Override
	public void close() {
		stop();
	}
}
