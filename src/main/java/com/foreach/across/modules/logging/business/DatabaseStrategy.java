package com.foreach.across.modules.logging.business;

public enum DatabaseStrategy
{
	/**
	 * We should not log to the database.
	 */
	NONE,

	/**
	 * We should use a rolling-table mechanism to log to the database. Relevant settings can be configured.
	 */
	ROLLING,

	/**
	 * We should use a single-table mechanism to log to the database. No periodic splits are done.
	 */
	SINGLE_TABLE
}
