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

import com.foreach.across.modules.logging.method.MethodLogConfiguration;
import com.foreach.across.modules.logging.request.RequestLogger;
import com.foreach.across.modules.logging.request.RequestLoggerConfiguration;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Andy Somers
 */
@ConfigurationProperties("logging")
public class LoggingModuleSettings
{
	public static final String METHOD_LOG_ENABLED = "logging.method.enabled";
	public static final String METHOD_LOG_CONFIGURATION = "logging.method.configuration";
	public static final String REQUEST_RESPONSE_LOG_ENABLED = "logging.requestResponse.enabled";
	public static final String REQUEST_RESPONSE_LOG_PAUSED = "logging.requestResponse.paused";
	public static final String REQUEST_RESPONSE_LOG_CONFIGURATION = "logging.requestResponse.configuration";

	public static final String REQUEST_LOGGER = "logging.request.logger";
	public static final String REQUEST_LOGGER_CONFIGURATION = "logging.request.configuration";

	private MethodLoggingSettings method = new MethodLoggingSettings();
	private RequestResponseSettings requestResponse = new RequestResponseSettings();
	private RequestSettings request = new RequestSettings();

	public MethodLoggingSettings getMethod() {
		return method;
	}

	public void setMethod( MethodLoggingSettings method ) {
		this.method = method;
	}

	public RequestResponseSettings getRequestResponse() {
		return requestResponse;
	}

	public void setRequestResponse( RequestResponseSettings requestResponse ) {
		this.requestResponse = requestResponse;
	}

	public RequestSettings getRequest() {
		return request;
	}

	public void setRequest( RequestSettings request ) {
		this.request = request;
	}

	public static class MethodLoggingSettings
	{
		private Boolean enabled;
		private MethodLogConfiguration configuration;

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled( Boolean enabled ) {
			this.enabled = enabled;
		}

		public MethodLogConfiguration getConfiguration() {
			return configuration;
		}

		public void setConfiguration( MethodLogConfiguration configuration ) {
			this.configuration = configuration;
		}
	}

	public static class RequestResponseSettings
	{
		private boolean enabled;
		private boolean paused = true;
		private RequestResponseLogConfiguration configuration = new RequestResponseLogConfiguration();

		public boolean getEnabled() {
			return enabled;
		}

		public void setEnabled( boolean enabled ) {
			this.enabled = enabled;
		}

		public boolean getPaused() {
			return paused;
		}

		public void setPaused( boolean paused ) {
			this.paused = paused;
		}

		public RequestResponseLogConfiguration getConfiguration() {
			return configuration;
		}

		public void setConfiguration( RequestResponseLogConfiguration configuration ) {
			this.configuration = configuration;
		}
	}

	public static class RequestSettings
	{
		private RequestLogger logger = RequestLogger.FILTER;
		private RequestLoggerConfiguration configuration = RequestLoggerConfiguration.allRequests();

		public RequestLogger getLogger() {
			return logger;
		}

		public void setLogger( RequestLogger logger ) {
			this.logger = logger;
		}

		public RequestLoggerConfiguration getConfiguration() {
			return configuration;
		}

		public void setConfiguration( RequestLoggerConfiguration configuration ) {
			this.configuration = configuration;
		}
	}
}
