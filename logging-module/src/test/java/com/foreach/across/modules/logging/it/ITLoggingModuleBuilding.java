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

package com.foreach.across.modules.logging.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.config.RequestResponseLoggingConfiguration;
import com.foreach.across.modules.logging.config.dynamic.RequestLoggerFilterConfiguration;
import com.foreach.across.modules.logging.config.dynamic.RequestLoggerInterceptorConfiguration;
import com.foreach.across.modules.logging.controllers.LogController;
import com.foreach.across.modules.logging.controllers.RequestResponseLogController;
import com.foreach.across.modules.logging.request.RequestLogger;
import com.foreach.across.modules.logging.request.RequestLoggerFilter;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogConfiguration;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLoggingFilter;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.servlet.AcrossMultipartFilter;
import com.foreach.across.test.AcrossTestContext;
import com.foreach.across.test.AcrossTestWebContext;
import com.foreach.across.test.MockFilterRegistration;
import com.foreach.across.test.support.AcrossTestBuilders;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Andy Somers
 */
public class ITLoggingModuleBuilding
{
	public static final String LOGGING_MODULE = "LoggingModule";

	@Test
	public void moduleWithoutRequestResponseLogging() throws Exception {
		try (AcrossTestContext ctx = AcrossTestBuilders.web().configurer( new SimpleLoggingModuleConfig() ).build()) {
			LogController logController = ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
			                                                           LogController.class );
			assertNotNull( logController );

			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestResponseLogConfiguration.class );
				fail( "There should not be a bean of type " + RequestResponseLogConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             RequestResponseLoggingConfiguration.class );
				fail( "There should not be a bean of type " + RequestResponseLoggingConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestResponseLogController.class );
				fail( "There should not be a bean of type " + RequestResponseLogController.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
		}
	}

	@Test
	public void moduleWithRequestResponseEnabled() throws Exception {
		try (AcrossTestContext ctx = AcrossTestBuilders.web().configurer( new ComplexLoggingModuleConfig() ).build()) {
			RequestResponseLogConfiguration settings
					= ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestResponseLogConfiguration.class );

			assertTrue( settings.isEnabled() );
			assertTrue( settings.isPaused() );

			LogController logController = ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
			                                                           LogController.class );
			assertNotNull( logController );

			RequestResponseLoggingConfiguration requestResponseLoggingConfiguration =
					ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
					                             RequestResponseLoggingConfiguration.class );

			assertNotNull( requestResponseLoggingConfiguration );
			RequestResponseLogController requestResponseLogController = ctx.getBeanOfTypeFromModule(
					LOGGING_MODULE, RequestResponseLogController.class );
			assertNotNull( requestResponseLogController );
		}
	}

	@Test
	public void moduleWithoutDebugWebWorks() throws Exception {
		try (AcrossTestContext ctx = AcrossTestBuilders.web().configurer( new LoggingModuleWithoutDebugWebConfig() )
		                                               .build()) {
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestResponseLogConfiguration.class );
				fail( "There should not be a bean of type " + RequestResponseLogConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}

			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             LogController.class );
				fail( "There should not be a bean of type " + LogController.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}

			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             RequestResponseLoggingConfiguration.class );
				fail( "There should not be a bean of type " + RequestResponseLoggingConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestResponseLogController.class );
				fail( "There should not be a bean of type " + RequestResponseLogController.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
		}
	}

	@Test
	public void moduleWithRequestLogFilter() throws Exception {
		try (AcrossTestContext ctx = AcrossTestBuilders.web().configurer( new SimpleLoggingModuleConfig() ).build()) {

			RequestLoggerFilterConfiguration filterConfiguration = ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestLoggerFilterConfiguration.class );
			assertNotNull( filterConfiguration );
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE, RequestLoggerInterceptorConfiguration.class );
				fail( "There should not be a bean of type " + RequestLoggerInterceptorConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
		}
	}

	@Test
	public void moduleWithRequestInterceptorConfig() throws Exception {
		try (AcrossTestContext ctx = AcrossTestBuilders.web().configurer(
				new LoggingModuleWithRequestLogInterceptorConfig() ).build()) {
			RequestLoggerInterceptorConfiguration logInterceptorConfiguration = ctx.getBeanOfTypeFromModule(
					LOGGING_MODULE, RequestLoggerInterceptorConfiguration.class );
			assertNotNull( logInterceptorConfiguration );
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             RequestLoggerFilterConfiguration.class );
				fail( "There should not be a bean of type " + RequestLoggerFilterConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
		}
	}

	@Test
	public void moduleWithoutRequestLoggerConfig() throws Exception {
		try (AcrossTestContext ctx = AcrossTestBuilders.web().configurer( new LoggingModuleWithoutRequestLogConfig() )
		                                               .build()) {
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             RequestResponseLogConfiguration.class );
				fail( "There should not be a bean of type " + RequestResponseLogConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             RequestLoggerFilterConfiguration.class );
				fail( "There should not be a bean of type " + RequestLoggerFilterConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
			try {
				ctx.getBeanOfTypeFromModule( LOGGING_MODULE,
				                             RequestLoggerInterceptorConfiguration.class );
				fail( "There should not be a bean of type " + RequestLoggerInterceptorConfiguration.class.getName() );
			}
			catch ( NoSuchBeanDefinitionException e ) {
				// expected
			}
		}
	}

	@Test
	public void moduleWithSpringSecurityModuleOrder() {
		try (AcrossTestWebContext ctx = AcrossTestBuilders.web().configurer( new LoggingModuleWithSecurityModuleConfig() ).build()) {

			Map<String, ? extends FilterRegistration> filters = ctx.getServletContext().getFilterRegistrations();
			List<? extends Class<? extends Filter>> orderedFilters = filters.values()
			                                                                .stream()
			                                                                .map( o -> ( (MockFilterRegistration) o ).getFilterClass() )
			                                                                .collect( Collectors.toList() );

			assertFalse( "Filters should not be empty", orderedFilters.isEmpty() );

			assertEquals( "OrderedCharacterEncodingFilter is not 1st filter", OrderedCharacterEncodingFilter.class, orderedFilters.get( 0 ) );
			assertEquals( "AcrossMultipartFilter is not 2nd filter", AcrossMultipartFilter.class, orderedFilters.get( 1 ) );
			assertEquals( "RequestLoggerFilter is not 3rd filter", RequestLoggerFilter.class, orderedFilters.get( 2 ) );
			assertEquals( "RequestResponseLoggingFilter is not 4th filter", RequestResponseLoggingFilter.class, orderedFilters.get( 3 ) );
			assertEquals( "ResourceUrlEncodingFilter is not 8th filter", ResourceUrlEncodingFilter.class, orderedFilters.get( 7 ) );
			assertEquals( "springSecurityFilterChain is not 9th filter", "springSecurityFilterChain", new ArrayList<>( filters.keySet() ).get( 8 ) );
		}
	}

	protected static class ComplexLoggingModuleConfig implements AcrossContextConfigurer
	{

		@Override
		public void configure( AcrossContext context ) {
			LoggingModule loggingModule = new LoggingModule();
			loggingModule.setProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_ENABLED, true );

			RequestResponseLogConfiguration logConfiguration = new RequestResponseLogConfiguration();
			logConfiguration.setExcludedPathPatterns( Arrays.asList( "/static/**", "/debug/**" ) );

			loggingModule.setProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_CONFIGURATION, logConfiguration );
			context.addModule( loggingModule );
			context.addModule( new DebugWebModule() );
		}
	}

	protected static class SimpleLoggingModuleConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new LoggingModule() );
			context.addModule( new DebugWebModule() );
		}
	}

	protected static class LoggingModuleWithSecurityModuleConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			LoggingModule loggingModule = new LoggingModule();
			loggingModule.setProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_ENABLED, true );

			RequestResponseLogConfiguration logConfiguration = new RequestResponseLogConfiguration();
			logConfiguration.setExcludedPathPatterns( Arrays.asList( "/static/**", "/debug/**" ) );

			loggingModule.setProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_CONFIGURATION, logConfiguration );
			context.addModule( loggingModule );

			DebugWebModule debugWebModule = new DebugWebModule();
			debugWebModule.setProperty( "debug-web-module.security.enabled", false );
			context.addModule( debugWebModule );
			context.addModule( new SpringSecurityModule() );

			ConfigurableListableBeanFactory beanFactory = ( (ConfigurableApplicationContext) context.getParentApplicationContext() ).getBeanFactory();
			beanFactory.registerSingleton( "webSecurityConfigurer", new AcrossWebSecurityConfigurer()
			{
			} );

		}
	}

	protected static class LoggingModuleWithoutDebugWebConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new LoggingModule() );
		}
	}

	protected static class LoggingModuleWithRequestLogInterceptorConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossWebModule() );
			LoggingModule module = new LoggingModule();
			module.setProperty( LoggingModuleSettings.REQUEST_LOGGER, RequestLogger.INTERCEPTOR );
			context.addModule( module );
		}
	}

	protected static class LoggingModuleWithoutRequestLogConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossWebModule() );
			LoggingModule module = new LoggingModule();
			module.setProperty( LoggingModuleSettings.REQUEST_LOGGER, RequestLogger.NONE );
			context.addModule( module );
		}
	}
}
