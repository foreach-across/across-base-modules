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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.*;
import com.foreach.across.modules.logging.services.*;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = { ITLoggingModuleWithHibernate.Config.class })
public class ITLoggingModuleWithHibernate
{
	@Autowired
	private LoggingService loggingService;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Autowired
	private Collection<LogDelegateService> logDelegateServices;

	@Autowired
	private FunctionalLogFileService functionalLogFileService;

	@Autowired
	private TechnicalLogFileService technicalLogFileService;

	@Autowired
	private FunctionalLogDBService functionalLogDBService;

	@Autowired
	private TechnicalLogDBService technicalLogDBService;

	@Autowired
	private LogDBReaderService logDBReaderService;

	@Test
	public void verifyBootstrapped() {
		assertNotNull( loggingService );

		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( LoggingModule.NAME );
		assertNotNull( moduleInfo.getApplicationContext().getBean( FunctionalLogDBService.class ) );
		assertNotNull( moduleInfo.getApplicationContext().getBean( TechnicalLogDBService.class ) );

		assertNotNull( logDelegateServices );
		assertTrue( logDelegateServices.contains( functionalLogFileService ) );
		assertTrue( logDelegateServices.contains( technicalLogFileService ) );
		assertTrue( logDelegateServices.contains( functionalLogDBService ) );
		assertTrue( logDelegateServices.contains( technicalLogDBService ) );
		assertEquals( logDelegateServices.size(), 4 );
	}

	@Test
	public void logDBReaderServiceReturnsTheCorrectLogs() throws InterruptedException {
		loggingService.logFunctional( "foo", getClass(), 1L, "user@example.com", null );
		loggingService.logTechnical( "A message", getClass(), LogLevel.INFO, null );

		Collection<LogEvent> allA = logDBReaderService.getEvents();
		Collection<LogEvent> funcA = logDBReaderService.getEvents( LogType.FUNCTIONAL );
		Collection<LogEvent> techA = logDBReaderService.getEvents( LogType.TECHNICAL );

		assertEquals( 1, funcA.size() );
		assertEquals( 1, techA.size() );
		assertEquals( 2, allA.size() );
		assertTrue( allA.containsAll( funcA ) );
		assertTrue( allA.containsAll( techA ) );
		funcA.removeAll( techA );
		assertEquals( 1, funcA.size() );

		assertTrue( ( (FunctionalLogEvent) funcA.iterator().next() ).getAction().equals( "foo" ) );
		assertTrue( ( (TechnicalLogEvent) techA.iterator().next() ).getMessage().equals( "A message" ) );

		Thread.sleep( 1000 ); //If we don't sleep, we can't properly test order.

		loggingService.logFunctional( "bar", getClass(), 2L, "user2@example.com", null );
		loggingService.logTechnical( "Another message", getClass(), LogLevel.WARN, null );

		Collection<LogEvent> allB = logDBReaderService.getEvents();
		Collection<LogEvent> funcB = logDBReaderService.getEvents( LogType.FUNCTIONAL );
		Collection<LogEvent> techB = logDBReaderService.getEvents( LogType.TECHNICAL );
		Collection<LogEvent> limitedB = logDBReaderService.getEvents( 2 );
		Collection<LogEvent> funcLimitedB = logDBReaderService.getEvents( LogType.FUNCTIONAL, 1 );
		Collection<LogEvent> techLimitedB = logDBReaderService.getEvents( LogType.TECHNICAL, 1 );

		assertEquals( 2, funcB.size() );
		assertEquals( 2, techB.size() );
		assertEquals( 4, allB.size() );
		assertEquals( 1, funcLimitedB.size() );
		assertEquals( 1, techLimitedB.size() );
		assertEquals( 2, limitedB.size() );

		assertTrue( allB.containsAll( funcB ) );
		assertTrue( allB.containsAll( techB ) );
		assertTrue( funcB.containsAll( funcLimitedB ) );
		assertTrue( techB.containsAll( techLimitedB ) );
		assertTrue( allB.containsAll( limitedB ) );

		funcLimitedB.removeAll( techLimitedB );
		assertEquals( 1, funcA.size() );

		Iterator<LogEvent> funcBIt = funcB.iterator();
		Iterator<LogEvent> techBIt = techB.iterator();
		assertTrue( ( (FunctionalLogEvent) funcBIt.next() ).getAction().equals( "bar" ) );
		assertTrue( ( (TechnicalLogEvent) techBIt.next() ).getMessage().equals( "Another message" ) );
		assertTrue( ( (FunctionalLogEvent) funcBIt.next() ).getAction().equals( "foo" ) );
		assertTrue( ( (TechnicalLogEvent) techBIt.next() ).getMessage().equals( "A message" ) );
		assertTrue( ( (FunctionalLogEvent) funcLimitedB.iterator().next() ).getAction().equals( "bar" ) );
		assertTrue( ( (TechnicalLogEvent) techLimitedB.iterator().next() ).getMessage().equals( "Another message" ) );
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossHibernateModule() );
			context.addModule( new AcrossWebModule() );
			context.addModule( loggingModule() );
		}

		private LoggingModule loggingModule() {
			LoggingModule loggingModule = new LoggingModule();
			loggingModule.setProperty( LoggingModuleSettings.FUNCTIONAL_DB_STRATEGY, DatabaseStrategy.SINGLE_TABLE );
			loggingModule.setProperty( LoggingModuleSettings.TECHNICAL_DB_STRATEGY, DatabaseStrategy.SINGLE_TABLE );
			return loggingModule;
		}
	}
}
