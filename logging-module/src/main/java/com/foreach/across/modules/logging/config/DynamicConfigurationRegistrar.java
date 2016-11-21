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

import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.config.dynamic.RequestLoggerFilterConfiguration;
import com.foreach.across.modules.logging.config.dynamic.RequestLoggerInterceptorConfiguration;
import com.foreach.across.modules.logging.request.RequestLogger;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marc Vanbrabant
 */
public class DynamicConfigurationRegistrar implements ImportSelector, EnvironmentAware
{
	private Environment environment;

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Override
	public String[] selectImports( AnnotationMetadata importingClassMetadata ) {
		List<String> imports = new ArrayList<>();

		RequestLogger requestLogger = environment.getProperty( LoggingModuleSettings.REQUEST_LOGGER,
		                                                       RequestLogger.class, RequestLogger.FILTER );
		if ( requestLogger == RequestLogger.INTERCEPTOR ) {
			imports.add( RequestLoggerInterceptorConfiguration.class.getName() );
		}
		if ( requestLogger == RequestLogger.FILTER ) {
			imports.add( RequestLoggerFilterConfiguration.class.getName() );
		}

		return imports.toArray( new String[imports.size()] );
	}

}
