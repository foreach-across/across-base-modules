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

package com.foreach.across.modules.logging.config.dynamic;

import com.foreach.across.core.AcrossException;
import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.request.LogHandlerAndViewNameInterceptor;
import com.foreach.across.modules.logging.request.RequestLoggerConfiguration;
import com.foreach.across.modules.logging.request.RequestLoggerFilter;
import com.foreach.common.spring.context.ApplicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.Collection;

/**
 * Configures the RequestLoggerFilter to be first in the filter chain
 */
@ConditionalOnAcrossModule("AcrossWebModule")
public class RequestLoggerFilterConfiguration implements EnvironmentAware
{
	private Environment environment;

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Bean
	protected RequestLoggerConfiguration requestLoggerConfiguration() {
		return environment.getProperty( LoggingModuleSettings.REQUEST_LOGGER_CONFIGURATION,
		                                RequestLoggerConfiguration.class,
		                                RequestLoggerConfiguration.allRequests() );
	}

	@Bean
	public RequestLoggerFilter requestLogFilter() {
		RequestLoggerFilter filter = new RequestLoggerFilter();

		if ( requestLoggerConfiguration().getIncludedPathPatterns() != null ) {
			filter.setIncludedPathPatterns( requestLoggerConfiguration().getIncludedPathPatterns() );
		}

		if ( requestLoggerConfiguration().getExcludedPathPatterns() != null ) {
			filter.setExcludedPathPatterns( requestLoggerConfiguration().getExcludedPathPatterns() );
		}

		if ( requestLoggerConfiguration().getLoggerLevelThreshold() != null ) {
			filter.setLoggerLevelThreshold( requestLoggerConfiguration().getLoggerLevelThreshold() );
		}
		return filter;
	}

	@Bean
	@Exposed
	public MappedInterceptor logHandlerAndViewNameMappedInterceptor() {
		return new MappedInterceptor( new String[] { "/**" }, new LogHandlerAndViewNameInterceptor() );
	}

	@Bean
	public FilterRegistrationBean requestFilterRegistrationBean( RequestLoggerFilter requestLogFilter ) {
		FilterRegistrationBean<RequestLoggerFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setOrder( Ordered.HIGHEST_PRECEDENCE + 900 ); //SpringSecurityFilterChain = 1000
		filterRegistrationBean.setFilter( requestLogFilter );

		Collection<String> urlFilterMappings = requestLoggerConfiguration().getUrlFilterMappings();
		Collection<String> servletNameFilterMappings = requestLoggerConfiguration().getServletNameFilterMappings();

		if ( urlFilterMappings.isEmpty() && servletNameFilterMappings.isEmpty() ) {
			throw new AcrossException( "At least one filter mapping must be specified when enabling request logging" );
		}

		if ( !urlFilterMappings.isEmpty() ) {
			filterRegistrationBean.addUrlPatterns( urlFilterMappings.toArray( new String[0] ) );
		}
		if ( !servletNameFilterMappings.isEmpty() ) {
			filterRegistrationBean.addServletNames( servletNameFilterMappings.toArray( new String[0] ) );
		}

		return filterRegistrationBean;
	}

	/**
	 * Register the {@link ApplicationInfo#getInstanceId()} as the request log instance id.
	 */
	@Configuration
	@SuppressWarnings("all")
	@ConditionalOnAcrossModule("ApplicationInfoModule")
	public static class ApplicationInstanceLogConfiguration
	{
		@Autowired
		public void registerApplicationInfo( RequestLoggerFilter requestLoggerFilter,
		                                     @Module("ApplicationInfoModule") ApplicationInfo applicationInfo ) {
			requestLoggerFilter.setInstanceId( applicationInfo.getInstanceId() );
		}
	}
}
