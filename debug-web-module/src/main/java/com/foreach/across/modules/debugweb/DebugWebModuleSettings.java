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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Arne Vandamme
 */
@ConfigurationProperties(prefix = "debugWebModule")
public class DebugWebModuleSettings
{
	public static final String DEFAULT_DASHBOARD = "/";

	@SuppressWarnings( "unused" )
	public static final String DASHBOARD_PATH = "debugWebModule.dashboard";

	/**
	 * Path within the debug web context for the initial dashboard.
	 */
	private String dashboard = DEFAULT_DASHBOARD;

	/**
	 * Root path for all debug web controllers.  All mappings will be relative to this path.
	 */
	private String rootPath = "/debug";

	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard( String dashboard ) {
		this.dashboard = dashboard;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath( String rootPath ) {
		this.rootPath = rootPath;
	}
}
