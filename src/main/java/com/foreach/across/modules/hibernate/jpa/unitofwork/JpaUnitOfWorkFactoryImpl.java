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
	public void start() {
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
}
