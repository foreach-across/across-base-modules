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

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.core.events.AcrossModuleBootstrappedEvent;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.method.MethodLogConfiguration;
import com.foreach.across.modules.logging.method.MethodLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 * @author Arne Vandamme
 */
@Configuration
@ConditionalOnProperty(LoggingModuleSettings.METHOD_LOG_ENABLED)
@AcrossEventHandler
public class MethodLoggingConfiguration implements EnvironmentAware
{
	private static final Logger LOG = LoggerFactory.getLogger( MethodLoggingConfiguration.class );

	private Environment environment;

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = environment;
	}

	@Bean
	@Exposed
	public MethodLogConfiguration methodLogConfiguration() {
		return environment.getProperty( LoggingModuleSettings.METHOD_LOG_CONFIGURATION,
		                                MethodLogConfiguration.class,
		                                MethodLogConfiguration.all( 75 ) );
	}

	@SuppressWarnings("unused")
	@Event
	private void registerMethodLoggingConfiguration( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) throws ClassNotFoundException {
		// If extension class exists
		Class moduleClass = beforeBootstrapEvent.getModule().getModule().getClass();

		String extensionClassName = moduleClass.getPackage().getName() + ".extensions.MethodLoggingConfiguration";

		if ( ClassUtils.isPresent( extensionClassName, moduleClass.getClassLoader() ) ) {
			LOG.info( "Adding method logging {} configuration to module {}",
			          beforeBootstrapEvent.getModule().getName() );

			Class methodLoggingConfigurationClass
					= ClassUtils.forName( extensionClassName, moduleClass.getClassLoader() );

			beforeBootstrapEvent.addApplicationContextConfigurers(
					new AnnotatedClassConfigurer( methodLoggingConfigurationClass )
			);
		}
	}

	@SuppressWarnings("unused")
	@Event
	private void registerMethodLoggersFromModule( AcrossModuleBootstrappedEvent afterBootstrapEvent ) {
		Map<String, MethodLogger> methodLoggers = afterBootstrapEvent.getModule()
		                                                             .getApplicationContext()
		                                                             .getBeansOfType( MethodLogger.class );

		for ( MethodLogger methodLogger : methodLoggers.values() ) {
			LOG.info( "Registering methodLogger {} from module {}", methodLogger.getName(),
			          afterBootstrapEvent.getModule().getName() );
			methodLogConfiguration().register( methodLogger );
		}
	}
}
