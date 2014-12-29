package com.foreach.across.modules.logging.services;

import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.LogEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FunctionalLogToFileService implements LogDelegateService
{
	private final Logger LOG_FUNCTIONAL = LoggerFactory.getLogger( "functional" );

	@Override
	public boolean supports( LogType logType ) {
		return logType == LogType.FUNCTIONAL;
	}

	@Override
	public void log( LogEventDto logEventDto ) {
		LOG_FUNCTIONAL.info( logEventDto.toString() );
	}
}
