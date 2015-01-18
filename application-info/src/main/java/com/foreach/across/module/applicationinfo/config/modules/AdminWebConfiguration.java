package com.foreach.across.module.applicationinfo.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.events.AcrossModuleBootstrappedEvent;
import com.foreach.across.module.applicationinfo.controllers.ApplicationInfoController;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.common.spring.context.ApplicationInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Set the default admin web title.
 */
@AcrossDepends(required = "AdminWebModule")
@Configuration
@AcrossEventHandler
public class AdminWebConfiguration
{
	private static Logger LOG = LoggerFactory.getLogger( AdminWebConfiguration.class );

	@Autowired(required = false)
	private AdminWebModuleSettings adminWebModuleSettings;

	@Autowired
	private ApplicationInfo runningApplicationInfo;

	@Event
	protected void registerAdminWebTitle( AcrossModuleBootstrappedEvent moduleBootstrappedEvent ) {
		if ( StringUtils.equals( AdminWebModule.NAME, moduleBootstrappedEvent.getModule().getName() )
				&& adminWebModuleSettings != null
				&& adminWebModuleSettings.getTitle() == null ) {
			LOG.trace( "Registering application name as AdminWeb title" );
			adminWebModuleSettings.setTitle( runningApplicationInfo.getApplicationName() );
		}
	}

	@Bean
	public ApplicationInfoController applicationInfoController() {
		return new ApplicationInfoController();
	}
}
