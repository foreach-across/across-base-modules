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

import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.LogEventDto;
import com.foreach.across.modules.logging.dto.TechnicalLogEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TechnicalLogFileService implements LogDelegateService
{
	@Override
	public boolean supports( LogType logType ) {
		return logType == LogType.TECHNICAL;
	}

	@Override
	public void log( LogEventDto logEventDto ) {
		TechnicalLogEventDto technicalLogEventDto = (TechnicalLogEventDto) logEventDto;
		Logger logger = LoggerFactory.getLogger( technicalLogEventDto.getSender() );
		switch ( technicalLogEventDto.getLevel() ) {
			case ERROR:
				logger.error( technicalLogEventDto.toString() );
				break;
			case WARN:
				logger.warn( technicalLogEventDto.toString() );
				break;
			case INFO:
				logger.info( technicalLogEventDto.toString() );
				break;
			case DEBUG:
				logger.debug( technicalLogEventDto.toString() );
				break;
			case TRACE:
				logger.trace( technicalLogEventDto.toString() );
				break;
		}
	}
}
