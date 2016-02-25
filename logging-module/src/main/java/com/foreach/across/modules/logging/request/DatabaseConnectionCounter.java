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

import javax.sql.DataSource;

/**
 * @author Marc Vanbrabant
 */
public class DatabaseConnectionCounter
{
	// We use an inner variable, because we can then replace it by a non thread local instance
	// which would improve performance mightily, since it would disable the actual logging...
	private static SimpleDatabaseConnectionCounter debugLog = new SimpleDatabaseConnectionCounter();

	public static void logConnection( DataSource dataSource ) {
		try {
			debugLog.logConnection( dataSource );
		}
		catch ( Exception t ) {
			// Do nothing
		}
	}

	/**
	 * Start a new request debugLog.
	 */
	public static void startRequest() {
		try {
			debugLog.initialize();
		}
		catch ( Exception t ) {
			// Do nothing
		}
	}

	/**
	 * A request has finished executing.
	 */
	public static void stopRequest() {
		try {
			debugLog.destroy();
		}
		catch ( Exception t ) {
			// Do nothing
		}
	}

	/**
	 * Adds a thread local logger, starts intercepting calls.
	 */
	public static void injectThreadLocalLogger() {
		debugLog = new ThreadLocalDebug();
	}

	/**
	 * Stops thread local logger, recreates empty implementation.
	 */
	public static void destroyThreadLocalLogger() {
		debugLog = new SimpleDatabaseConnectionCounter();
	}

	public static int getTotalConnectionCount() {
		return debugLog.getStatistics().getTotalConnectionCount();
	}

	/**
	 * Simple (empty) implementation, does nothing.
	 */
	private static class SimpleDatabaseConnectionCounter
	{
		protected ThreadLocalDebug.Statistics EMPTY_STATISTICS = new ThreadLocalDebug.Statistics();

		public void initialize() {
		}

		public void destroy() {
		}

		public void logConnection( DataSource dataSource ) {
		}

		public ThreadLocalDebug.Statistics getStatistics() {
			return EMPTY_STATISTICS;
		}
	}

	/**
	 * Thread local implementation, keeps counts etc.
	 */
	private static class ThreadLocalDebug extends SimpleDatabaseConnectionCounter
	{
		private ThreadLocal<Statistics> stats = new ThreadLocal<>();

		@Override
		public void initialize() {
			// Perhaps we should implement a check for re-entrance here?
			stats.set( new Statistics() );
		}

		@Override
		public void destroy() {
			stats.remove();
		}

		@Override
		public void logConnection( DataSource dataSource ) {
			Statistics s = stats.get();
			if ( s != null ) {
				s.connectionUp( dataSource );
			}
		}

		@Override
		public Statistics getStatistics() {
			Statistics s = stats.get();
			return s == null ? EMPTY_STATISTICS : s;
		}

		private static class Statistics
		{
			private int connections;

			public void connectionUp( DataSource dataSource ) {
				connections++;
			}

			public int getTotalConnectionCount() {
				return connections;
			}
		}
	}
}
