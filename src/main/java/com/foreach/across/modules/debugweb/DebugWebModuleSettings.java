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
package com.foreach.across.modules.debugweb;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import com.foreach.across.modules.debugweb.servlet.logging.RequestResponseLogConfiguration;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Arne Vandamme
 */
public class DebugWebModuleSettings extends AcrossModuleSettings
{
	public static final String DASHBOARD_PATH = "debugWeb.dashboard";
	public static final String REQUEST_RESPONSE_LOG_ENABLED = "debugWeb.log.requestResponse.enabled";
	public static final String REQUEST_RESPONSE_LOG_PAUSED = "debugWeb.log.requestResponse.paused";
	public static final String REQUEST_RESPONSE_LOG_CONFIGURATION = "debugWeb.log.requestResponse.configuration";

	private String dashboardPath = null;

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( DASHBOARD_PATH, String.class, null,
		                   "Relative path (within debug web) for the landing page of debug web." );
		registry.register( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class, false,
		                   "Should request/response details be logged." );
		registry.register( REQUEST_RESPONSE_LOG_CONFIGURATION, RequestResponseLogConfiguration.class,
		                   new RequestResponseLogConfiguration(),
		                   "Configuration settings for request/response details log." );
		registry.register( REQUEST_RESPONSE_LOG_PAUSED, Boolean.class, false,
		                   "If enabled, should this logger be paused or not." );
	}

	/**
	 * Allows changing the debug web dashboard path at runtime.
	 * @param dashboardPath Path within the debug web context to the dashboard.
	 */
	public void setDashboardPath( String dashboardPath ) {
		this.dashboardPath = dashboardPath;
	}

	public String getDashboardPath() {
		if ( dashboardPath == null ) {
			String path = getProperty( DASHBOARD_PATH );

			return StringUtils.isEmpty( path ) ? "/" : path;
		}

		return dashboardPath;
	}

	public boolean isRequestResponseLogEnabled() {
		return getProperty( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class );
	}
}
