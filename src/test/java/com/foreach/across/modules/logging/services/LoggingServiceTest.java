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

import com.foreach.across.modules.logging.business.LogLevel;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.FunctionalLogEventDto;
import com.foreach.across.modules.logging.dto.TechnicalLogEventDto;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = LoggingServiceTest.Config.class)
@DirtiesContext
public class LoggingServiceTest
{
	@Autowired
	private LoggingService loggingService;

	@Autowired
	private FunctionalLogFileService functionalLogFileService;

	@Autowired
	private TechnicalLogFileService technicalLogFileService;

	@Before
	public void resetMocks() {
		reset( functionalLogFileService );
		reset( technicalLogFileService );

		loggingService.setDelegates( Arrays.asList( functionalLogFileService, technicalLogFileService ) );
	}

	@Test
	public void logFunctionalCorrectlyCreatesFunctionalLogEventDtoWithoutData() {
		String action = "foo";
		Class entity = getClass();
		Long entityId = 1L;
		String user = "bar";

		FunctionalLogEventDto expected = new FunctionalLogEventDto();
		expected.setAction( action );
		expected.setEntity( entity.getName() );
		expected.setEntityId( entityId );
		expected.setUser( user );

		when( functionalLogFileService.supports( LogType.FUNCTIONAL ) ).thenReturn( true );
		when( technicalLogFileService.supports( LogType.FUNCTIONAL ) ).thenReturn( false );
		loggingService.logFunctional( action, entity, entityId, user, null );
		verify( functionalLogFileService ).supports( LogType.FUNCTIONAL );
		verify( technicalLogFileService ).supports( LogType.FUNCTIONAL );
		verify( functionalLogFileService ).log( expected );
		verifyNoMoreInteractions( technicalLogFileService );
	}

	@Test
	public void logFunctionalCorrectlyCreatesFunctionalLogEventDtoWitData() {
		String action = "foo";
		Class entity = getClass();
		Long entityId = 1L;
		String user = "bar";
		Map<String, Object> data = new HashMap<>();
		data.put( "foo", "bar" );

		FunctionalLogEventDto expected = new FunctionalLogEventDto();
		expected.setAction( action );
		expected.setEntity( entity.getName() );
		expected.setEntityId( entityId );
		expected.setUser( user );
		expected.setData( "{\"foo\":\"bar\"}" );

		when( functionalLogFileService.supports( LogType.FUNCTIONAL ) ).thenReturn( true );
		when( technicalLogFileService.supports( LogType.FUNCTIONAL ) ).thenReturn( false );
		loggingService.logFunctional( action, entity, entityId, user, data );
		verify( functionalLogFileService ).supports( LogType.FUNCTIONAL );
		verify( technicalLogFileService ).supports( LogType.FUNCTIONAL );
		verify( functionalLogFileService ).log( expected );
		verifyNoMoreInteractions( technicalLogFileService );
	}

	@Test
	public void logTechnicalCorrectlyCreatesTechnicalLogEventDtoWithoutData() {
		String message = "foobar";
		Class entity = getClass();
		LogLevel level = LogLevel.WARN;

		TechnicalLogEventDto expected = new TechnicalLogEventDto();
		expected.setMessage( message );
		expected.setLevel( level );
		expected.setSender( entity );

		when( functionalLogFileService.supports( LogType.TECHNICAL ) ).thenReturn( false );
		when( technicalLogFileService.supports( LogType.TECHNICAL ) ).thenReturn( true );
		loggingService.logTechnical( message, entity, level, null );
		verify( functionalLogFileService ).supports( LogType.TECHNICAL );
		verify( technicalLogFileService ).supports( LogType.TECHNICAL );
		verify( technicalLogFileService ).log( expected );
		verifyNoMoreInteractions( functionalLogFileService );
	}

	@Test
	public void logTechnicalCorrectlyCreatesTechnicalLogEventDtoWitData() {
		String message = "foobar";
		Class entity = getClass();
		LogLevel level = LogLevel.WARN;
		Map<String, Object> data = new HashMap<>();
		data.put( "foo", "bar" );

		TechnicalLogEventDto expected = new TechnicalLogEventDto();
		expected.setMessage( message );
		expected.setLevel( level );
		expected.setSender( entity );
		expected.setData( "{\"foo\":\"bar\"}" );

		when( functionalLogFileService.supports( LogType.TECHNICAL ) ).thenReturn( false );
		when( technicalLogFileService.supports( LogType.TECHNICAL ) ).thenReturn( true );
		loggingService.logTechnical( message, entity, level, data );
		verify( functionalLogFileService ).supports( LogType.TECHNICAL );
		verify( technicalLogFileService ).supports( LogType.TECHNICAL );
		verify( technicalLogFileService ).log( expected );
		verifyNoMoreInteractions( functionalLogFileService );
	}

	@Configuration
	static class Config
	{
		@Bean
		public LoggingService loggingService() {
			return new LoggingServiceImpl();
		}
	}
}
