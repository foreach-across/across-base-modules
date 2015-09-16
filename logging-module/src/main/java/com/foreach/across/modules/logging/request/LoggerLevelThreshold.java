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

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class LoggerLevelThreshold
{
	private NavigableMap<Integer, LoggerLevel> loggerLevelThreshold = new TreeMap<>();

	public LoggerLevel getLogLevelForDuration( long duration ) {
		LoggerLevel loggerLevel = LoggerLevel.DEBUG;
		for( Map.Entry<Integer,LoggerLevel> entry : loggerLevelThreshold.entrySet() ) {
			if( duration >= entry.getKey() ) {
				return entry.getValue();
			}
		}
		return loggerLevel;
	}

	public static class Builder {

		protected TreeMap<Integer, LoggerLevel> loggerLevelThreshold = new TreeMap<>();

		public LoggerLevelThreshold.Builder setInfoLevelThreshold( int threshold ) {
			loggerLevelThreshold.put( threshold, LoggerLevel.INFO );
			return this;
		}

		public LoggerLevelThreshold.Builder setWarnLevelThreshold( int warnLevelThreshold ) {
			loggerLevelThreshold.put( warnLevelThreshold, LoggerLevel.WARN );
			return this;
		}

		public LoggerLevelThreshold.Builder setErrorLevelThreshold( int errorLevelThreshold ) {
			loggerLevelThreshold.put( errorLevelThreshold, LoggerLevel.ERROR );
			return this;
		}

		public LoggerLevelThreshold build() {
			return new LoggerLevelThreshold( this );
		}
	}

	private LoggerLevelThreshold( Builder b ) {
		loggerLevelThreshold.putAll( b.loggerLevelThreshold );
		loggerLevelThreshold = loggerLevelThreshold.descendingMap();
	}

	public enum LoggerLevel {
		DEBUG,
		INFO,
		WARN,
		ERROR,
	}
}
