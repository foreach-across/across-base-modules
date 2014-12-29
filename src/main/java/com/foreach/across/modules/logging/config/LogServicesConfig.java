package com.foreach.across.modules.logging.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.logging.services.FunctionalLogToFileService;
import com.foreach.across.modules.logging.services.LogDelegateService;
import com.foreach.across.modules.logging.services.LoggingService;
import com.foreach.across.modules.logging.services.LoggingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogServicesConfig
{
	@Bean
	@Exposed
	public LoggingService loggingService() { return new LoggingServiceImpl(); }

	@Bean
	public LogDelegateService functionalLogToFileService() { return new FunctionalLogToFileService(); }
}
