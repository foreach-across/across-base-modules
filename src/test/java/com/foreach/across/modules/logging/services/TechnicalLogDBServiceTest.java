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
import com.foreach.across.modules.logging.business.TechnicalLogEvent;
import com.foreach.across.modules.logging.dto.TechnicalLogEventDto;
import com.foreach.across.modules.logging.repositories.TechnicalLogEventRepository;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TechnicalLogDBServiceTest.Config.class)
@DirtiesContext
public class TechnicalLogDBServiceTest
{
	@Autowired
	private TechnicalLogDBService technicalLogDBService;

	@Autowired
	private TechnicalLogEventRepository technicalLogEventRepository;

	@Before
	public void resetMocks() {
		reset( technicalLogEventRepository );
	}

	@Test
	public void technicalLogDBServiceSupportsOnlyTechnical() {
		assertFalse( technicalLogDBService.supports( LogType.FUNCTIONAL ) );
		assertTrue( technicalLogDBService.supports( LogType.TECHNICAL ) );
	}

	@Test
	public void functionalLogDBServiceCreatesDBRecord() {
		TechnicalLogEventDto dto = new TechnicalLogEventDto();
		dto.setSender( TechnicalLogDBServiceTest.class );
		dto.setMessage( "foobar" );
		dto.setLevel( LogLevel.INFO );

		TechnicalLogEvent event = new TechnicalLogEvent();
		BeanUtils.copyProperties( dto, event );

		doNothing().when( technicalLogEventRepository ).create( event );
		technicalLogDBService.log( dto );

		ArgumentCaptor<TechnicalLogEvent> argument = ArgumentCaptor.forClass( TechnicalLogEvent.class );
		verify( technicalLogEventRepository ).create( argument.capture() );
		verifyNoMoreInteractions( technicalLogEventRepository );

		TechnicalLogEvent resulting = argument.getValue();
		assertEquals( dto.getTime(), resulting.getTime() );
		assertEquals( dto.getData(), resulting.getData() );
		assertEquals( dto.getLogType(), resulting.getLogType() );
		assertEquals( dto.getMessage(), resulting.getMessage() );
		assertEquals( dto.getLevel(), resulting.getLevel() );
		//Sender is not on the TechnicalLogEvent class
	}

	@Configuration
	static class Config
	{
		@Bean
		public TechnicalLogDBService technicalLogDBService() {
			return new TechnicalLogDBService();
		}
	}
}
