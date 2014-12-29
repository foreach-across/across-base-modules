package com.foreach.across.modules.logging.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.FunctionalLogEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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

		for (LogDelegateService delegateService : logDelegateServices) {
			if (delegateService.supports( LogType.FUNCTIONAL )) {
				delegateService.log( eventDto );
			}
		}
	}
}
