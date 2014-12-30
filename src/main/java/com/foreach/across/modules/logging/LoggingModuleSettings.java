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
package com.foreach.across.modules.logging;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import com.foreach.across.modules.logging.business.DatabaseStrategy;
import com.foreach.across.modules.logging.business.FileStrategy;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogConfiguration;

/**
 * @author Andy Somers
 */
public class LoggingModuleSettings extends AcrossModuleSettings
{
	/**
	 * Name of the logger to be used when logging functional data to file.
	 * <p/>
	 * String, name of the logger in logback configuration.
	 */
	public static final String FUNCTIONAL_FILE_LOGGER = "loggingModule.functionalFileLogger";

	public static final String REQUEST_RESPONSE_LOG_ENABLED = "logging.requestResponse.enabled";
	public static final String REQUEST_RESPONSE_LOG_PAUSED = "logging.requestResponse.paused";
	public static final String REQUEST_RESPONSE_LOG_CONFIGURATION = "logging.requestResponse.configuration";

	/**
	 * Which strategy should be used for logging to the database.
	 * <p/>
	 * Enum (@see com.foreach.across.modules.logging.business.DatabaseStrategy)
	 * DEFAULT: NONE
	 */
	public static final String FUNCTIONAL_DB_STRATEGY = "loggingModule.functionalDBStrategy";
	public static final String TECHNICAL_DB_STRATEGY = "loggingModule.technicalDBStrategy";

	/**
	 * The time at which we should backup old data and clear it from the main table.
	 * <p/>
	 * String, CRON expression, including seconds.
	 * DEFAULT: "0 0 0 1 * *"
	 */
	public static final String FUNCTIONAL_DB_ROLLING_SCHEDULE = "loggingModule.functionalDBRollingSchedule";
	public static final String TECHNICAL_DB_ROLLING_SCHEDULE = "loggingModule.technicalDBRollingSchedule";

	/**
	 * Which strategy should be used for logging to the filesystem.
	 * <p/>
	 * Enum (@see com.foreach.across.modules.logging.business.FileStrategy)
	 * Default: LOGBACK
	 */
	public static final String FUNCTIONAL_FILE_STRATEGY = "loggingModule.functionalFileStrategy";
	public static final String TECHNICAL_FILE_STRATEGY = "loggingModule.technicalFileStrategy";

	/**
	 * The amount of time covered by one table (except for the rolling table).
	 * <p/>
	 * String, ISO 8601 Duration (@see http://en.wikipedia.org/wiki/ISO_8601#Durations ) using only PnYnMnD components.
	 * DEFAULT: "P1M"
	 */
	public static final String FUNCTIONAL_DB_ROLLING_TIME_SPAN = "loggingModule.functionalDBRollingTimeSpan";
	public static final String TECHNICAL_DB_ROLLING_TIME_SPAN = "loggingModule.technicalDBRollingTimeSpan";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class, false,
		                   "Should request/response details be logged." );
		registry.register( REQUEST_RESPONSE_LOG_CONFIGURATION, RequestResponseLogConfiguration.class,
		                   new RequestResponseLogConfiguration(),
		                   "Configuration settings for request/response details log." );
		registry.register( REQUEST_RESPONSE_LOG_PAUSED, Boolean.class, false,
		                   "If enabled, should this logger be paused or not." );
		registry.register( FUNCTIONAL_FILE_LOGGER, String.class, "functional-logger",
		                   "Name of the logger to be used when logging functional data to file." );

		registry.register( FUNCTIONAL_DB_STRATEGY, DatabaseStrategy.class, DatabaseStrategy.NONE,
		                   "Which strategy should be used for functional logging to the database." );
		registry.register( TECHNICAL_DB_STRATEGY, DatabaseStrategy.class, DatabaseStrategy.NONE,
		                   "Which strategy should be used for technical logging to the database." );

		registry.register( FUNCTIONAL_DB_ROLLING_SCHEDULE, String.class, "0 0 0 1 * *",
		                   "The time at which we should backup old data and clear it from the main table." );
		registry.register( TECHNICAL_DB_ROLLING_SCHEDULE, String.class, "0 0 0 1 * *",
		                   "The time at which we should backup old data and clear it from the main table." );

		registry.register( FUNCTIONAL_FILE_STRATEGY, FileStrategy.class, FileStrategy.LOGBACK,
		                   "Which strategy should be used for logging to the filesystem." );
		registry.register( TECHNICAL_FILE_STRATEGY, FileStrategy.class, FileStrategy.LOGBACK,
		                   "Which strategy should be used for logging to the filesystem." );

		registry.register( FUNCTIONAL_DB_ROLLING_TIME_SPAN, String.class, "P1M",
		                   "The amount of time covered by one table (except for the rolling table)." );
		registry.register( TECHNICAL_DB_ROLLING_TIME_SPAN, String.class, "P1M",
		                   "The amount of time covered by one table (except for the rolling table)." );
	}

	public boolean isRequestResponseLogEnabled() {
		return getProperty( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class );
	}

	public String getFunctionalFileLogger() {
		return getProperty( FUNCTIONAL_FILE_LOGGER, String.class );
	}

	public DatabaseStrategy getFunctionalDBStrategy() {
		return getProperty( FUNCTIONAL_DB_STRATEGY, DatabaseStrategy.class );
	}

	public DatabaseStrategy getTechnicalDBStrategy() {
		return getProperty( TECHNICAL_DB_STRATEGY, DatabaseStrategy.class );
	}

	public String getFunctionalDBRollingSchedule() {
		return getProperty( FUNCTIONAL_DB_ROLLING_SCHEDULE, String.class );
	}

	public String getTechnicalDBRollingSchedule() {
		return getProperty( TECHNICAL_DB_ROLLING_SCHEDULE, String.class );
	}

	public FileStrategy getFunctionalFileStrategy() {
		return getProperty( FUNCTIONAL_FILE_STRATEGY, FileStrategy.class );
	}

	public FileStrategy getTechnicalFileStrategy() {
		return getProperty( TECHNICAL_FILE_STRATEGY, FileStrategy.class );
	}

	public String getFunctionalDBRollingTimeSpan() {
		return getProperty( FUNCTIONAL_DB_ROLLING_TIME_SPAN, String.class );
	}

	public String getTechnicalDBRollingTimeSpan() {
		return getProperty( TECHNICAL_DB_ROLLING_TIME_SPAN, String.class );
	}
}
