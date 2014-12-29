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
	}

	public boolean isRequestResponseLogEnabled() {
		return getProperty( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class );
	}
	
	public String getFunctionalFileLogger() {
		return getProperty( FUNCTIONAL_FILE_LOGGER, String.class );
	}
}
