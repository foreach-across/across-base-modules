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

import com.foreach.across.core.annotations.AcrossCondition;
import com.foreach.across.modules.logging.filters.RequestLogFilter;
import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Configures the RequestLogFilter to be first in the filter chain
 */
@Configuration
@AcrossCondition("settings.requestLogger == T(com.foreach.across.modules.logging.config.RequestLogger).FILTER")
public class RequestLogFilterConfiguration extends AcrossWebDynamicServletConfigurer
{
	@Override
	protected void dynamicConfigurationAllowed( ServletContext servletContext ) throws ServletException {
		servletContext.addFilter( "RequestLogFilter", RequestLogFilter.class );
	}

	@Override
	protected void dynamicConfigurationDenied( ServletContext servletContext ) throws ServletException {
	}
}
