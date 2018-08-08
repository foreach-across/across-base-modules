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

/**
 * @author Andy Somers
 */
public class LoggingModuleSettings
{
	public static final String METHOD_LOG_ENABLED = "logging.method.enabled";
	public static final String METHOD_LOG_CONFIGURATION = "logging.method.configuration";
	public static final String REQUEST_RESPONSE_LOG_ENABLED = "logging.request-response.enabled";
	public static final String REQUEST_RESPONSE_LOG_PAUSED = "logging.request-response.paused";
	public static final String REQUEST_RESPONSE_LOG_CONFIGURATION = "logging.request-response.configuration";

	public static final String REQUEST_LOGGER = "logging.request.logger";
	public static final String REQUEST_LOGGER_CONFIGURATION = "logging.request.configuration";

	public static final String LOGSTASH_CONFIGURATION_SERVER = "logging.logstash.servers";
	public static final String KIBANA_CONFIGURATION_SERVER = "logging.kibana.server";
	public static final String LOGSTASH_CONFIGURATION_APPLICATION = "logging.logstash.application";
}
