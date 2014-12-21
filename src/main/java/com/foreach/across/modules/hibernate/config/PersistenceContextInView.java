package com.foreach.across.modules.hibernate.config;

public enum PersistenceContextInView
{
	/**
	 * Do not configure any persistence context tied to the view layer.
	 */
	NONE,

	/**
	 * Create a filter and register it dynamically to all servlets if possible.
	 */
	FILTER,

	/**
	 * Create an interceptor and register it on the handlermapping level.
	 */
	INTERCEPTOR
}
