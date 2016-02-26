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

package com.foreach.across.modules.logging.request;

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Marc Vanbrabant
 */
public class TestDatabaseConnectionCounter
{
	private DataSource dataSource = new ConnectionLoggingDataSource( mock( DataSource.class ) );

	@Test
	public void testThatSimpleDatabaseConnectionCounterDoesNotCount() throws Exception {
		DatabaseConnectionCounter.logConnection( mock( DataSource.class ) );
		assertEquals( 0, DatabaseConnectionCounter.getTotalConnectionCount() );
		DatabaseConnectionCounter.stopRequest();
	}

	@Test
	public void testThatThreadLocalDatabaseConnectionCounterDoesCountThreadsThatAreNotPassedByFilter() throws Exception {
		DatabaseConnectionCounter.injectThreadLocalLogger();

		final CountDownLatch countDownLatch = new CountDownLatch( 1 );
		final AtomicInteger count = new AtomicInteger();
		Thread t1 = new Thread( new Runnable()
		{
			@Override
			public void run() {
				try {
					dataSource.getConnection();
					dataSource.getConnection( "username", "password" );
					count.set( DatabaseConnectionCounter.getTotalConnectionCount() );
					countDownLatch.countDown();
				}
				catch ( SQLException e ) {
					e.printStackTrace();
				}

			}
		} );

		t1.start();
		countDownLatch.await( 2, TimeUnit.SECONDS );
		assertEquals( 0, count.get() );

		DatabaseConnectionCounter.destroyThreadLocalLogger();

		assertEquals( 0, DatabaseConnectionCounter.getTotalConnectionCount() );
	}

	@Test
	public void testThatThreadLocalDatabaseConnectionCounterIncrementsForFilter() throws Exception {
		DatabaseConnectionCounter.injectThreadLocalLogger();

		final CountDownLatch countDownLatch = new CountDownLatch( 2 );
		final AtomicInteger countForRequest1 = new AtomicInteger();
		final AtomicInteger countForRequest2 = new AtomicInteger();

		Thread t1 = new Thread( new Runnable()
		{
			@Override
			public void run() {
				DatabaseConnectionCounter.startRequest();

				try {
					dataSource.getConnection();
					dataSource.getConnection();
					dataSource.getConnection();
				}
				catch ( SQLException e ) {
					e.printStackTrace();
				}
				countForRequest1.set( DatabaseConnectionCounter.getTotalConnectionCount() );
				countDownLatch.countDown();
			}
		} );

		Thread t2 = new Thread( new Runnable()
		{
			@Override
			public void run() {
				DatabaseConnectionCounter.startRequest();
				try {
					dataSource.getConnection();
				}
				catch ( SQLException e ) {
					e.printStackTrace();
				}
				countForRequest2.set( DatabaseConnectionCounter.getTotalConnectionCount() );
				countDownLatch.countDown();
				DatabaseConnectionCounter.stopRequest();
			}
		} );
		t1.start();
		t2.start();

		countDownLatch.await( 2, TimeUnit.SECONDS );
		assertEquals( 3, countForRequest1.get() );
		assertEquals( 1, countForRequest2.get() );
	}
}
