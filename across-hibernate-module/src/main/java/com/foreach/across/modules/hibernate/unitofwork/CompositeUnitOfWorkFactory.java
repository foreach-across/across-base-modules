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
package com.foreach.across.modules.hibernate.unitofwork;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Implementation of {@link UnitOfWorkFactory} that combines multiple instances to behave as a single.
 *
 * @author Arne Vandamme
 * @see UnitOfWorkFactoryImpl
 * @see com.foreach.across.modules.hibernate.jpa.unitofwork.JpaUnitOfWorkFactoryImpl
 */
public class CompositeUnitOfWorkFactory implements UnitOfWorkFactory
{
	private final Collection<UnitOfWorkFactory> members;

	public CompositeUnitOfWorkFactory( Collection<UnitOfWorkFactory> members ) {
		this.members = members;
	}

	/**
	 * Wraps a Runnable into a unit of work.
	 *
	 * @param runnable Original runnable instance.
	 * @return Wrapped Runnable.
	 */
	@Override
	public Runnable create( Runnable runnable ) {
		return new RunnableUnitOfWork( this, runnable );
	}

	/**
	 * Wraps a Callable into a unit of work.
	 *
	 * @param callable Original callable instance.
	 * @return Wrapped Callable.
	 */
	@Override
	public <T> Callable<T> create( Callable<T> callable ) {
		return new CallableUnitOfWork<T>( this, callable );
	}

	/**
	 * When called, this will close and restart all units of work attached to the current thread.
	 */
	@Override
	public void restart() {
		stop();
		start();
	}

	/**
	 * Starts a new unit of work.
	 */
	@Override
	public UnitOfWork start() {
		for ( UnitOfWorkFactory unitOfWorkFactory : members ) {
			if ( unitOfWorkFactory != this ) {
				unitOfWorkFactory.start();
			}
		}
		return new UnitOfWork( this );
	}

	/**
	 * Stops a new unit of work.
	 */
	@Override
	public void stop() {
		for ( UnitOfWorkFactory unitOfWorkFactory : members ) {
			if ( unitOfWorkFactory != this ) {
				unitOfWorkFactory.stop();
			}
		}
	}
}

