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
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.logging.services.*;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITLoggingModule.Config.class)
public class ITLoggingModule
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

	@Test
	public void verifyBootstrapped() {
		assertNotNull( loggingService );

		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( LoggingModule.NAME );

		assertBeanDoesNotExistInApplicationContext( moduleInfo.getApplicationContext(), FunctionalLogDBService.class );
		assertBeanDoesNotExistInApplicationContext( moduleInfo.getApplicationContext(), TechnicalLogDBService.class );

		assertNotNull( logDelegateServices );
		assertTrue( logDelegateServices.contains( functionalLogFileService ) );
		assertTrue( logDelegateServices.contains( technicalLogFileService ) );
		assertEquals( logDelegateServices.size(), 2 );
	}

	private void assertBeanDoesNotExistInApplicationContext( ApplicationContext applicationContext,
	                                                         Class<?> aClass ) {
		try {
			assertNull( applicationContext.getBean( aClass ) );
		}
		catch ( NoSuchBeanDefinitionException e ) {
			assertTrue( true ); //If we get this exception, the desired result has been achieved.
		}
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossWebModule() );
			context.addModule( new LoggingModule() );
		}
	}
}
