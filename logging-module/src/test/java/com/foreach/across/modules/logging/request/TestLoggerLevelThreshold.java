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

package com.foreach.across.modules.logging.request;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLoggerLevelThreshold
{
	@Test
	public void testGetLogLevelForDurationWithStupidConfig() throws Exception {
		LoggerLevelThreshold loggerLevelThreshold = new LoggerLevelThreshold.Builder().setInfoLevelThreshold( 15 ).setErrorLevelThreshold(10).build();
		assertEquals( LoggerLevelThreshold.LoggerLevel.DEBUG, loggerLevelThreshold.getLogLevelForDuration( 5 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.ERROR, loggerLevelThreshold.getLogLevelForDuration( 10 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.INFO, loggerLevelThreshold.getLogLevelForDuration( Integer.MAX_VALUE ) );
	}

	@Test
	public void testGetLogLevelForDurationWithInfo() throws Exception {
		LoggerLevelThreshold loggerLevelThreshold = new LoggerLevelThreshold.Builder().setInfoLevelThreshold( 10 ).setErrorLevelThreshold(15).build();
		assertEquals( LoggerLevelThreshold.LoggerLevel.DEBUG, loggerLevelThreshold.getLogLevelForDuration( 5 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.INFO, loggerLevelThreshold.getLogLevelForDuration( 10 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.ERROR, loggerLevelThreshold.getLogLevelForDuration( Integer.MAX_VALUE ) );
	}

	@Test
	public void testGetLogLevelForDurationWithInfoAndError() throws Exception {
		LoggerLevelThreshold loggerLevelThreshold = new LoggerLevelThreshold.Builder().setInfoLevelThreshold( 10 ).setErrorLevelThreshold(15).build();
		assertEquals( LoggerLevelThreshold.LoggerLevel.DEBUG, loggerLevelThreshold.getLogLevelForDuration( 5 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.INFO, loggerLevelThreshold.getLogLevelForDuration( 10 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.ERROR, loggerLevelThreshold.getLogLevelForDuration( Integer.MAX_VALUE ) );
	}

	@Test
	public void testGetLogLevelForDurationWithInfoWarnAndError() throws Exception {
		LoggerLevelThreshold loggerLevelThreshold = new LoggerLevelThreshold.Builder().setInfoLevelThreshold( 10 ).setErrorLevelThreshold(20).setWarnLevelThreshold( 15 ).build();
		assertEquals( LoggerLevelThreshold.LoggerLevel.DEBUG, loggerLevelThreshold.getLogLevelForDuration( 5 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.INFO, loggerLevelThreshold.getLogLevelForDuration( 10 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.WARN, loggerLevelThreshold.getLogLevelForDuration( 15 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.ERROR, loggerLevelThreshold.getLogLevelForDuration( 20 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.ERROR, loggerLevelThreshold.getLogLevelForDuration( Integer.MAX_VALUE ) );
	}

	@Test
	public void testGetLogLevelForDurationWithNoConfig() throws Exception {
		assertEquals( LoggerLevelThreshold.LoggerLevel.DEBUG,
		              new LoggerLevelThreshold.Builder().build().getLogLevelForDuration( 12 ) );
		assertEquals( LoggerLevelThreshold.LoggerLevel.DEBUG,
		              new LoggerLevelThreshold.Builder().build().getLogLevelForDuration( Integer.MAX_VALUE ) );
	}
}