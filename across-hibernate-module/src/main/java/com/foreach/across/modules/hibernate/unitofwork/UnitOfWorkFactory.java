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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.Callable;

/**
 * <p>A unit of work factory facilitates managing one or more sessions, without having
 * to explicitly know about the sessions themselves.</p>
 * <p>A UnitOfWorkFactory can be used to determine the open session/entity manager demarcations.</p>
 *
 * @see org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor
 * @see org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor
 */
public interface UnitOfWorkFactory extends Closeable
{
	static final Logger LOG = LoggerFactory.getLogger( UnitOfWorkFactory.class );

	/**
	 * Wraps a Runnable into a unit of work.
	 *
	 * @param runnable Original runnable instance.
	 * @return Wrapped Runnable.
	 */
	Runnable create( Runnable runnable );

	/**
	 * Wraps a Callable into a unit of work.
	 *
	 * @param callable Original callable instance.
	 * @param <T>      the result type of the method call
	 * @return Wrapped Callable.
	 */
	<T> Callable<T> create( Callable<T> callable );

	/**
	 * Starts a new unit of work: opens all Sessions.
	 */
	UnitOfWorkFactory start();

	/**
	 * Stops the unit of work: closes all Sessions.
	 */
	void stop();

	/**
	 * When called, this will close and reopen all Sessions attached
	 * to the current thread.
	 */
	void restart();

	/**
	 * Stops the unit of work: closes all Sessions.
	 * delegates to {@link UnitOfWorkFactory#stop()}.
	 */
	void close();
}
