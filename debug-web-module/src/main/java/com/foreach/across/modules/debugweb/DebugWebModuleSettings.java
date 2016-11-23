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

import com.sun.org.apache.xpath.internal.operations.Bool;
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

	public static final String SECURITY_ENABLED = "debugWebModule.security.enabled";
	public static final String SECURITY_USERNAME = "debugWebModule.security.username";
	public static final String SECURITY_PASSWORD = "debugWebModule.security.password";
	public static final String SECURITY_IP_ADDRESSES = "debugWebModule.security.ipAddresses";

	/**
	 * Path within the debug web context for the initial dashboard.
	 */
	private String dashboard = DEFAULT_DASHBOARD;

	/**
	 * The Security settings of the rootPath
	 */
	private SecuritySettings security = new SecuritySettings();

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

	public SecuritySettings getSecurity() {
		return security;
	}

	public void setSecurity( SecuritySettings security ) {
		this.security = security;
	}

	public static class SecuritySettings {
		/**
		 * Is Basic Authentication enabled for the rootPath?
		 */
		private Boolean enabled = true;

		/**
		 * The username to use for Basic Authentication
		 */
		private String username = "debug";

		/**
		 * The password to use for Basic Authentication
		 */
		private String password;

		/**
		 * A comma seperated list of IP Addresses to allow without Basic Authentication
		 */
		private String ipAddresses;

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled( Boolean enabled ) {
			this.enabled = enabled;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername( String username ) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword( String password ) {
			this.password = password;
		}

		public String getIpAddresses() {
			return ipAddresses;
		}

		public void setIpAddresses( String ipAddresses ) {
			this.ipAddresses = ipAddresses;
		}
	}
}
