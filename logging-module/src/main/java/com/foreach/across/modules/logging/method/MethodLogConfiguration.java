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
package com.foreach.across.modules.logging.method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
public class MethodLogConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( MethodLogConfiguration.class );

	public static final String WILDCARD = "*";

	private boolean enabled;
	private int defaultMinimumDuration = 75;
	private final Map<String, Boolean> loggerStatus = new HashMap<>();
	private final Map<String, Integer> durationForLogger = new HashMap<>();

	private Set<MethodLogger> loggers = new HashSet<>();

	/**
	 * @return true if method logging in general is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
		updateAllConfigurations();
	}

	public int getDefaultMinimumDuration() {
		return defaultMinimumDuration;
	}

	public void setDefaultMinimumDuration( int defaultMinimumDuration ) {
		this.defaultMinimumDuration = defaultMinimumDuration;
		updateAllConfigurations();
	}

	/**
	 * Set enabled status for a specific logger.  Only if the global {@link #isEnabled()} is {@code true} can
	 * any logger be enabled.  If you want the default status for unspecified loggers to be set, use the
	 * {@link #WILDCARD} token as logger name.
	 *
	 * @param loggerName of the specific logger
	 * @param enabled    true if logging is active
	 */
	public void setEnabled( String loggerName, boolean enabled ) {
		loggerStatus.put( loggerName, enabled );
		updateConfiguration( loggerName );
	}

	public void setMinimumDuration( String loggerName, int minimumDuration ) {
		durationForLogger.put( loggerName, minimumDuration );
		updateConfiguration( loggerName );
	}

	public boolean isEnabled( String loggerName ) {
		return enabled &&
				loggerStatus.containsKey( loggerName ) ? loggerStatus.get( loggerName ) : loggerStatus.get( WILDCARD );
	}

	public int getMinimumDuration( String loggerName ) {
		return durationForLogger.containsKey( loggerName )
				? durationForLogger.get( loggerName ) : defaultMinimumDuration;
	}

	public void register( MethodLogger methodLogger ) {
		loggers.add( methodLogger );

		updateConfiguration( methodLogger.getName() );
	}

	private void updateAllConfigurations() {
		for ( MethodLogger methodLogger : loggers ) {
			updateConfiguration( methodLogger.getName() );
		}
	}

	private void updateConfiguration( String name ) {
		MethodLogger methodLogger = getMethodLogger( name );

		if ( methodLogger != null ) {
			methodLogger.setEnabled( isEnabled( name ) );
			methodLogger.setMinimumDuration( getMinimumDuration( name ) );

			LOG.info( "Updated configuration for method logger {}: enabled {}, minimum duration {}",
			          name, methodLogger.isEnabled(), methodLogger.getMinimumDuration() );
		}
	}

	private MethodLogger getMethodLogger( String name ) {
		for ( MethodLogger logger : loggers ) {
			if ( StringUtils.equals( name, logger.getName() ) ) {
				return logger;
			}
		}

		return null;
	}

	/**
	 * Creates a default method log configuration that enables method logging for all loggers,
	 * with the set minimum duration.
	 *
	 * @param minimumDuration for the method logging
	 * @return modifiable configuration
	 */
	public static MethodLogConfiguration all( int minimumDuration ) {
		MethodLogConfiguration configuration = new MethodLogConfiguration();
		configuration.setEnabled( true );
		configuration.setDefaultMinimumDuration( minimumDuration );
		configuration.setEnabled( WILDCARD, true );

		return configuration;
	}
}
