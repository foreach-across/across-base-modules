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
package com.foreach.across.modules.it.logging;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossException;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.installers.InstallerAction;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.DatabaseStrategy;
import com.foreach.across.modules.logging.services.LoggingService;
import com.foreach.across.modules.web.AcrossWebModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ITTestLoggingModuleSettings.Config.class)
@WebAppConfiguration
public class ITTestLoggingModuleSettings
{
	@Autowired
	private ApplicationContext parent;

	private AcrossContext context;
	private LoggingModule loggingModule;

	@Before
	public void prepare() {
		loggingModule = new LoggingModule();
	}

	@After
	public void teardown() {
		if ( context != null ) {
			try {
				context.shutdown();
			}
			catch ( Exception e ) {
			}
		}
	}

	@Test
	public void defaultSettingsAllowBootstrap() {
		LoggingService loggingService = bootstrapWithParent();
		assertNotNull( loggingService );
	}

	@Test(expected = AcrossException.class)
	public void settingInvalidCronAsFunctionalScheduleFailsBootstrap() {
		loggingModule.setProperty( LoggingModuleSettings.FUNCTIONAL_DB_ROLLING_SCHEDULE, "foo" );

		LoggingService loggingService = bootstrapWithParent();
		assertNull( loggingService );
	}

	@Test(expected = AcrossException.class)
	public void settingInvalidDurationAsFunctionalTimestampFailsBootstrap() {
		loggingModule.setProperty( LoggingModuleSettings.FUNCTIONAL_DB_ROLLING_TIME_SPAN, "foo" );

		LoggingService loggingService = bootstrapWithParent();
		assertNull( loggingService );
	}

	@Test(expected = AcrossException.class)
	public void settingInvalidCronAsTechnicalScheduleFailsBootstrap() {
		loggingModule.setProperty( LoggingModuleSettings.TECHNICAL_DB_ROLLING_SCHEDULE, "foo" );

		LoggingService loggingService = bootstrapWithParent();
		assertNull( loggingService );
	}

	@Test(expected = AcrossException.class)
	public void settingInvalidDurationAsTechnicalTimestampFailsBootstrap() {
		loggingModule.setProperty( LoggingModuleSettings.TECHNICAL_DB_ROLLING_TIME_SPAN, "foo" );

		LoggingService loggingService = bootstrapWithParent();
		assertNull( loggingService );
	}

	@Test
	public void nonRollingScheduleAllowsBootstrapEvenWithInvalidRollingSettings() {
		loggingModule.setProperty( LoggingModuleSettings.FUNCTIONAL_DB_STRATEGY, DatabaseStrategy.NONE );
		loggingModule.setProperty( LoggingModuleSettings.TECHNICAL_DB_STRATEGY, DatabaseStrategy.NONE );
		loggingModule.setProperty( LoggingModuleSettings.FUNCTIONAL_DB_ROLLING_SCHEDULE, "foo" );
		loggingModule.setProperty( LoggingModuleSettings.FUNCTIONAL_DB_ROLLING_TIME_SPAN, "foo" );
		loggingModule.setProperty( LoggingModuleSettings.TECHNICAL_DB_ROLLING_SCHEDULE, "foo" );
		loggingModule.setProperty( LoggingModuleSettings.TECHNICAL_DB_ROLLING_TIME_SPAN, "foo" );

		LoggingService loggingService = bootstrapWithParent();
		assertNotNull( loggingService );
	}

	private LoggingService bootstrapWithoutParent() {
		return bootstrap( false );
	}

	private LoggingService bootstrapWithParent() {
		return bootstrap( true );
	}

	private LoggingService bootstrap( boolean useParent ) {
		context = new AcrossContext( useParent ? parent : null );
		context.setDataSource( mock( DataSource.class ) );
		context.setInstallerAction( InstallerAction.DISABLED );
		context.addModule( new AcrossWebModule() );
		context.addModule( loggingModule );
		context.bootstrap();

		return (LoggingService) AcrossContextUtils.getApplicationContext( loggingModule ).getBean(
				"loggingService" );
	}

	@Configuration
	static class Config
	{
	}
}
