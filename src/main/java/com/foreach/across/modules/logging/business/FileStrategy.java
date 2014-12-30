package com.foreach.across.modules.logging.business;

public enum FileStrategy
{
	/**
	 * We should not log to the filesystem.
	 */
	NONE,

	/**
	 * We should rely on the project providing logback configuration.
	 */
	LOGBACK
}
