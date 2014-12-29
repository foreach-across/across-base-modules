package com.foreach.across.modules.logging.services;

import java.util.Map;

public interface LoggingService
{
	void logFunctional( String action, Class entity, Long entityId, String user, Map<String, Object> data );

	//TODO: LogTechnical
}
