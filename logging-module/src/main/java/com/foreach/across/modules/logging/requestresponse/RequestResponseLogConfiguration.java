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

package com.foreach.across.modules.logging.requestresponse;

import com.foreach.across.modules.logging.request.RequestLoggerConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;

/**
 * Configuration instance for request/response debug logging.
 *
 * @author Arne Vandamme
 */
@ConfigurationProperties("logging.request-response")
@Getter
@Setter
public class RequestResponseLogConfiguration extends RequestLoggerConfiguration
{
	/**
	 * Maximum number of log entries that should be kept.
	 */
	private int maxEntries = 100;

	/**
	 * Should default request/response filtering be enabled.
	 */
	private boolean paused = true;

	/**
	 * Should the filter be installed.
	 */
	private boolean enabled = false;

	/**
	 * Which operator should be use for matching the http status.
	 */
	private HttpStatusOperator httpStatusOperator = null;

	/**
	 * Which HTTP status to filter (in combination with the httpStatusOperator)
	 */
	private Integer httpStatusCode = null;

	public RequestResponseLogConfiguration() {
		setExcludedPathPatterns( Arrays.asList( "/debug/**", "/across/**", "/**/login" ) );
	}

	public static RequestResponseLogConfiguration allRequests() {
		return new RequestResponseLogConfiguration();
	}
}


