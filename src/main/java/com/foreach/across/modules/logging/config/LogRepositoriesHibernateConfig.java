package com.foreach.across.modules.logging.config;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.logging.repositories.FunctionalLogEventRepository;
import com.foreach.across.modules.logging.repositories.FunctionalLogEventRepositoryImpl;
import com.foreach.across.modules.logging.repositories.LogEventRepository;
import com.foreach.across.modules.logging.repositories.LogEventRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AcrossDepends(required = "AcrossHibernateModule")
public class LogRepositoriesHibernateConfig
{
	@Bean
	public LogEventRepository logEventRepository() {
		return new LogEventRepositoryImpl();
	}

	@Bean
	public FunctionalLogEventRepository functionalLogEventRepository() { return new FunctionalLogEventRepositoryImpl(); }
}
