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

package com.foreach.across.modules.logging.config;

import com.foreach.across.core.AcrossException;
import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.controllers.RequestResponseLogController;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogConfiguration;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogRegistry;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLoggingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.util.Collection;

/**
 * @author Andy Somers
 */
@Configuration
@ConditionalOnAcrossModule("AcrossWebModule")
@ConditionalOnProperty(LoggingModuleSettings.REQUEST_RESPONSE_LOG_ENABLED)
public class RequestResponseLoggingConfiguration implements EnvironmentAware
{
	private Environment environment;

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Bean
	public RequestResponseLogConfiguration requestResponseLogConfiguration() {
		return environment.getProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_CONFIGURATION,
		                                RequestResponseLogConfiguration.class,
		                                new RequestResponseLogConfiguration() );
	}

	@Bean
	public RequestResponseLogRegistry requestResponseLogRegistry() {
		RequestResponseLogRegistry registry = new RequestResponseLogRegistry();
		registry.setMaxEntries( requestResponseLogConfiguration().getMaxEntries() );

		return registry;
	}

	@Bean
	@ConditionalOnAcrossModule("DebugWebModule")
	public RequestResponseLogController requestResponseLogController( RequestResponseLogRegistry registry, RequestResponseLoggingFilter filter ) {
		return new RequestResponseLogController( registry, filter );
	}

	@Bean
	@Lazy
	public RequestResponseLoggingFilter requestResponseLoggingFilter() {
		RequestResponseLoggingFilter filter
				= new RequestResponseLoggingFilter( requestResponseLogRegistry(),
				                                    requestResponseLogConfiguration().isPaused() );

		if ( requestResponseLogConfiguration().getIncludedPathPatterns() != null ) {
			filter.setIncludedPathPatterns( requestResponseLogConfiguration().getIncludedPathPatterns() );
		}

		if ( requestResponseLogConfiguration().getExcludedPathPatterns() != null ) {
			filter.setExcludedPathPatterns( requestResponseLogConfiguration().getExcludedPathPatterns() );
		}

		if ( requestResponseLogConfiguration().getHttpStatusCode() != null ) {
			filter.setHttpStatusCode( requestResponseLogConfiguration().getHttpStatusCode() );
		}

		if ( requestResponseLogConfiguration().getHttpStatusOperator() != null ) {
			filter.setHttpStatusOperator( requestResponseLogConfiguration().getHttpStatusOperator() );
		}

		return filter;
	}

	@Bean
	public FilterRegistrationBean requestResponseFilterRegistrationBean( RequestResponseLoggingFilter requestResponseLoggingFilter ) {
		FilterRegistrationBean<RequestResponseLoggingFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setOrder( Ordered.HIGHEST_PRECEDENCE + 901 ); //SpringSecurityFilterChain = 1000
		filterRegistrationBean.setFilter( requestResponseLoggingFilter );

		Collection<String> urlFilterMappings = requestResponseLogConfiguration().getUrlFilterMappings();
		Collection<String> servletNameFilterMappings = requestResponseLogConfiguration().getServletNameFilterMappings();

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
}
