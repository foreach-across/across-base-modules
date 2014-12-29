package com.foreach.across.modules.logging.services;

import com.foreach.across.modules.logging.business.LogType;
import com.foreach.across.modules.logging.dto.LogEventDto;

public interface LogDelegateService
{
	boolean supports( LogType logType );

	void log( LogEventDto logEventDto );
}
