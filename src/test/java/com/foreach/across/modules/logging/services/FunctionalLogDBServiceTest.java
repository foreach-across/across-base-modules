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

import com.foreach.across.modules.logging.business.FunctionalLogEvent;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.FunctionalLogEventDto;
import com.foreach.across.modules.logging.repositories.FunctionalLogEventRepository;
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
@ContextConfiguration(loader = MockedLoader.class, classes = FunctionalLogDBServiceTest.Config.class)
@DirtiesContext
public class FunctionalLogDBServiceTest
{
	@Autowired
	private FunctionalLogDBService functionalLogDBService;

	@Autowired
	private FunctionalLogEventRepository functionalLogEventRepository;

	@Before
	public void resetMocks() {
		reset( functionalLogEventRepository );
	}

	@Test
	public void functionalLogDBServiceSupportsOnlyFunctional() {
		assertTrue( functionalLogDBService.supports( LogType.FUNCTIONAL ) );
		assertFalse( functionalLogDBService.supports( LogType.TECHNICAL ) );
	}

	@Test
	public void functionalLogDBServiceCreatesDBRecord() {
		FunctionalLogEventDto dto = new FunctionalLogEventDto();
		dto.setAction( "foo" );
		dto.setUser( "bar" );
		dto.setEntity( "com.foreach.across.modules.logging.services.FunctionalLogDBServiceTest" );
		dto.setEntityId( 1 );

		FunctionalLogEvent event = new FunctionalLogEvent();
		BeanUtils.copyProperties( dto, event );

		doNothing().when( functionalLogEventRepository ).create( event );
		functionalLogDBService.log( dto );

		ArgumentCaptor<FunctionalLogEvent> argument = ArgumentCaptor.forClass( FunctionalLogEvent.class );
		verify( functionalLogEventRepository ).create( argument.capture() );
		verifyNoMoreInteractions( functionalLogEventRepository );

		FunctionalLogEvent resulting = argument.getValue();
		assertEquals( dto.getTime(), resulting.getTime() );
		assertEquals( dto.getAction(), resulting.getAction() );
		assertEquals( dto.getEntity(), resulting.getEntity() );
		assertEquals( dto.getEntityId(), resulting.getEntityId() );
		assertEquals( dto.getUser(), resulting.getUser() );
		assertEquals( dto.getData(), resulting.getData() );
		assertEquals( dto.getLogType(), resulting.getLogType() );
	}

	@Configuration
	static class Config
	{
		@Bean
		public FunctionalLogDBService functionalLogDBService() {
			return new FunctionalLogDBService();
		}
	}
}
