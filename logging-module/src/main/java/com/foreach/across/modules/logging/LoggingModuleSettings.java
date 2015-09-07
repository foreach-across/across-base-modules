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
import com.foreach.across.modules.logging.method.MethodLogConfiguration;
import com.foreach.across.modules.logging.request.RequestLogger;
import com.foreach.across.modules.logging.request.RequestLoggerConfiguration;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogConfiguration;

/**
 * @author Andy Somers
 */
public class LoggingModuleSettings extends AcrossModuleSettings
{
	public static final String METHOD_LOG_ENABLED = "logging.method.enabled";
	public static final String METHOD_LOG_CONFIGURATION = "logging.method.configuration";
	public static final String REQUEST_RESPONSE_LOG_ENABLED = "logging.requestResponse.enabled";
	public static final String REQUEST_RESPONSE_LOG_PAUSED = "logging.requestResponse.paused";
	public static final String REQUEST_RESPONSE_LOG_CONFIGURATION = "logging.requestResponse.configuration";

	public static final String REQUEST_LOGGER = "logging.request.logger";
	public static final String REQUEST_LOGGER_CONFIGURATION = "logging.request.logger.configuration";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class, false,
		                   "Should request/response details be logged." );
		registry.register( REQUEST_RESPONSE_LOG_CONFIGURATION, RequestResponseLogConfiguration.class,
		                   new RequestResponseLogConfiguration(),
		                   "Configuration settings for request/response details log." );
		registry.register( REQUEST_RESPONSE_LOG_PAUSED, Boolean.class, false,
		                   "If enabled, should this logger be paused or not." );
		registry.register( REQUEST_LOGGER, RequestLogger.class, RequestLogger.FILTER,
		                   "Configures how the requests will be logged." );
		registry.register( REQUEST_LOGGER_CONFIGURATION, RequestLoggerConfiguration.class,
		                   RequestLoggerConfiguration.allRequests(),
		                   "Configuration for servlets and paths that should be logged." );
		registry.register( METHOD_LOG_ENABLED, Boolean.class, false,
		                   "Should method logging extensions in modules be loaded." );
		registry.register( METHOD_LOG_CONFIGURATION, MethodLogConfiguration.class,
		                   null, "Configuration settings for method logging." );
	}

	public boolean isRequestResponseLogEnabled() {
		return getProperty( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class );
	}

	@SuppressWarnings("unused")
	public RequestLogger getRequestLogger() {
		return getProperty( REQUEST_LOGGER, RequestLogger.class );
	}

	@SuppressWarnings("unused")
	public boolean isMethodLogEnabled() {
		return getProperty( METHOD_LOG_ENABLED, Boolean.class );
	}

	public MethodLogConfiguration getMethodLogConfiguration() {
		return getProperty( METHOD_LOG_CONFIGURATION, MethodLogConfiguration.class );
	}
}
