package com.foreach.across.modules.debugweb;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import com.foreach.across.modules.debugweb.servlet.logging.RequestResponseLogConfiguration;

/**
 * @author Arne Vandamme
 */
public class DebugWebModuleSettings extends AcrossModuleSettings
{
	public static final String REQUEST_RESPONSE_LOG_ENABLED = "debugWeb.log.requestResponse.enabled";
	public static final String REQUEST_RESPONSE_LOG_CONFIGURATION = "debugWeb.log.requestResponse.configuration";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class, false,
		                   "Should request/response details be logged." );
		registry.register( REQUEST_RESPONSE_LOG_CONFIGURATION, RequestResponseLogConfiguration.class,
		                   new RequestResponseLogConfiguration(),
		                   "Configuration settings for request/response details log." );
	}

	public boolean isRequestResponseLogEnabled() {
		return getProperty( REQUEST_RESPONSE_LOG_ENABLED, Boolean.class );
	}
}
