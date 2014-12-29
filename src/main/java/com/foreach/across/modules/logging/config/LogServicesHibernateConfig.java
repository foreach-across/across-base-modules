package com.foreach.across.modules.logging.config;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.logging.services.FunctionalLogDBService;
import com.foreach.across.modules.logging.services.LogDelegateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AcrossDepends(required = "AcrossHibernateModule")
public class LogServicesHibernateConfig
{
	@Bean
	public LogDelegateService functionalLogDBService() {
		return new FunctionalLogDBService();
	}
}
