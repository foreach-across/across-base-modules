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

/**
 * Configuration instance for request/response debug logging.
 *
 * @author Arne Vandamme
 */
public class RequestResponseLogConfiguration extends RequestLoggerConfiguration
{
	private int maxEntries = 100;
	private boolean paused = false;

	public int getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries( int maxEntries ) {
		this.maxEntries = maxEntries;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused( boolean paused ) {
		this.paused = paused;
	}

	public static RequestResponseLogConfiguration allRequests() {
		return new RequestResponseLogConfiguration();
	}
}


