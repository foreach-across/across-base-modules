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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.logging.business.LogLevel;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.FunctionalLogEventDto;
import com.foreach.across.modules.logging.dto.TechnicalLogEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Service
public class LoggingServiceImpl implements LoggingService
{
	private final Logger LOG = LoggerFactory.getLogger( getClass() );

	@Autowired
	private Collection<LogDelegateService> logDelegateServices;

	private ObjectMapper jsonObjectMapper;

	@PostConstruct
	public void init() {
		jsonObjectMapper = new ObjectMapper();
	}

	@Override
	public void logFunctional( String action, Class entity, Long entityId, String user, Map<String, Object> data ) {
		FunctionalLogEventDto eventDto = new FunctionalLogEventDto();
		eventDto.setTime( new Date() );
		eventDto.setAction( action );
		eventDto.setEntity( entity.getName() );
		eventDto.setEntityId( entityId );
		eventDto.setUser( user );
		if ( data != null && data.size() > 1 ) {
			try {
				eventDto.setData( jsonObjectMapper.writeValueAsString( data ) );
			}
			catch ( JsonProcessingException e ) {
				LOG.error( "Couldn't serialize functional log event data", e );
			}
		}

		for ( LogDelegateService delegateService : logDelegateServices ) {
			if ( delegateService.supports( LogType.FUNCTIONAL ) ) {
				delegateService.log( eventDto );
			}
		}
	}

	@Override
	public void logTechnical( String message, Class sender, LogLevel level, Map<String, Object> data ) {
		TechnicalLogEventDto dto = new TechnicalLogEventDto();
		dto.setTime( new Date() );
		dto.setMessage( message );
		dto.setLevel( level );
		dto.setSender( sender );

		for ( LogDelegateService delegateService : logDelegateServices ) {
			if ( delegateService.supports( LogType.TECHNICAL ) ) {
				delegateService.log( dto );
			}
		}
	}
}
