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
package com.foreach.across.modules.adminweb;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import org.apache.commons.lang3.StringUtils;

public class AdminWebModuleSettings extends AcrossModuleSettings
{
//	/**
//	 * URL or relative path (without admin prefix) for the page to which a user will be sent
//	 * after successful login without previously accessing a protected url.
//	 * <p/>
//	 * String
//	 */
//	public static final String LOGIN_DASHBOARD_URL = "adminWebModule.login.dashboardUrl";
//
//	/**
//	 * If true, the user will always redirect to the dashboard after login, even if a previously
//	 * protected url has been accessed.
//	 * <p/>
//	 * Boolean - default: false
//	 */
//	public static final String LOGIN_REDIRECT_TO_DASHBOARD = "adminWebModule.login.redirectToDashboard";

	/**
	 * Key used for creating the remember me cookie.  If this property is not present, remember me will not be enabled.
	 */
	public static final String REMEMBER_ME_KEY = "adminWebModule.login.rememberMe.key";

	public static final String TITLE = "adminWebModule.title";

	/**
	 * Number of seconds a remember me token should be valid.
	 * Defaults to 2592000 (30 days).
	 * <p/>
	 * Integer
	 */
	public static final String REMEMBER_ME_TOKEN_VALIDITY_SECONDS =
			"adminWebModule.login.rememberMe.tokenValiditySeconds";

	private String title;

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( REMEMBER_ME_KEY, String.class, null,
		                   "Key used for creating the remember me cookie.  " +
				                   "If this property is not present, remember me will not be enabled." );
		registry.register( REMEMBER_ME_TOKEN_VALIDITY_SECONDS, Integer.class, 2592000,
		                   "Number of seconds a remember me token should be valid. Defaults to 30 days." );
		registry.register( TITLE, String.class, null,
		                   "Name of the application to be shown on the default admin web layout in the top left corner." );
	}

	public String getTitle() {
		if ( title == null ) {
			return getProperty( TITLE );
		}

		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public boolean isRememberMeEnabled() {
		return !StringUtils.isBlank( getProperty( AdminWebModuleSettings.REMEMBER_ME_KEY, "" ) );
	}
}
