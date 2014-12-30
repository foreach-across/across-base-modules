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
package com.foreach.across.modules.logging.services;

import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.FunctionalLogEventDto;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = FunctionalLogFileServiceTest.Config.class)
@DirtiesContext
public class FunctionalLogFileServiceTest
{
	@Autowired
	private FunctionalLogFileService functionalLogFileService;

	private static Logger mockLogger;

	@Before
	public void resetMocks() {
		mockLogger = mock( Logger.class );
		functionalLogFileService.setFunctionalLog( mockLogger );
	}

	@Test
	public void functionalLogFileServiceSupportsOnlyFunctional() {
		assertTrue( functionalLogFileService.supports( LogType.FUNCTIONAL ) );
		assertFalse( functionalLogFileService.supports( LogType.TECHNICAL ) );
	}

	@Test
	public void functionalLogFileServiceLogsToFunctionalLog() {
		FunctionalLogEventDto dto = new FunctionalLogEventDto();
		dto.setAction( "foo" );
		dto.setUser( "bar" );
		dto.setEntity( "com.foreach.across.modules.logging.services.FunctionalLogDBServiceTest" );
		dto.setEntityId( 1 );

		functionalLogFileService.log( dto );
		verify( mockLogger ).info(
				dto.toString() ); //TODO Should also test the toString itself, probably? Should it even be a toString in that class, rather than a method in FunctionalLogFileService?
		verifyNoMoreInteractions( mockLogger );
	}

	@Configuration
	static class Config
	{
		@Bean
		public LoggingModuleSettings loggingModuleSettings() {
			return new LoggingModuleSettings();
		}

		@Bean
		public FunctionalLogFileService functionalLogFileService() {
			return new FunctionalLogFileService();
		}
	}
}
