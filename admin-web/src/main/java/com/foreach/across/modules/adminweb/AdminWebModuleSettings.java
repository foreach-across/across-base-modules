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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

	public static final String LOCALE_DEFAULT = "adminWebModule.locale.default";
	public static final String LOCALE_OPTIONS = "adminWebModule.locale.options";

	public static final String ADMIN_ACCESS_PERMISSIONS = "adminWebModule.access.permissions";

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
		registry.register( LOCALE_DEFAULT, Locale.class, null,
		                   "Default locale that should explicitly be set when accessing the administration interface if no specific locale." );
		registry.register( LOCALE_OPTIONS, List.class, Collections.emptyList(),
		                   "List of locales that can be selected on the login page." );
		registry.register( ADMIN_ACCESS_PERMISSIONS, String[].class, new String[]{ "access administration" },
		                   "List of permissions that grant access to the administration interface." );
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

	public Locale getDefaultLocale() {
		return getProperty( LOCALE_DEFAULT, Locale.class );
	}

	public List getLocaleOptions() {
		return getProperty( LOCALE_OPTIONS, List.class );
	}

	public String[] getAccessPermissions() {
		return getProperty( ADMIN_ACCESS_PERMISSIONS, String[].class );
	}

}
