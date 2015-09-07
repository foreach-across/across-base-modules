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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.logging.LoggingModule;
import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.method.MethodLogConfiguration;
import com.foreach.across.modules.logging.request.RequestLoggerConfiguration;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.context.AcrossWebApplicationContext;
import com.foreach.across.modules.web.servlet.AbstractAcrossServletInitializer;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
public class WebAppInitializer extends AbstractAcrossServletInitializer
{
	@Override
	protected void configure( AcrossWebApplicationContext applicationContext ) {
		applicationContext.register( WebAppConfiguration.class );
	}

	@Configuration
	@EnableAcrossContext
	protected static class WebAppConfiguration implements AcrossContextConfigurer
	{
		@Bean
		public DataSource acrossDataSource() {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName( "org.hsqldb.jdbc.JDBCDriver" );
			dataSource.setUrl( "jdbc:hsqldb:mem:/hsql/testWeb" );
			dataSource.setUsername( "sa" );
			dataSource.setPassword( "" );

			return dataSource;
		}

		@Override
		public void configure( AcrossContext context ) {
			context.addModule( acrossWebModule() );
			context.addModule( debugWebModule() );
			context.addModule( loggingModule() );
		}

		private AcrossWebModule acrossWebModule() {
			return new AcrossWebModule();
		}

		private DebugWebModule debugWebModule() {
			DebugWebModule debugWebModule = new DebugWebModule();
			debugWebModule.setRootPath( "/debug" );

			return debugWebModule;
		}

		private LoggingModule loggingModule() {
			LoggingModule loggingModule = new LoggingModule();
			loggingModule.setProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_ENABLED, true );

//			RequestResponseLogConfiguration logConfiguration = new RequestResponseLogConfiguration();
//			logConfiguration.setExcludedPathPatterns(
//					Arrays.asList( "/static/**", "/debug/**", "/resources/images/**", "/admin/**" )
//			);
//
//			if ( !testEnvironments.contains( environment.getProperty( "environment.type" ) ) ) {
//				logConfiguration.setPaused( true );
//			}
//
//			loggingModule.setProperty( LoggingModuleSettings.REQUEST_RESPONSE_LOG_CONFIGURATION,
//			                           logConfiguration );

			// Enable agressive method logging
			loggingModule.setProperty( LoggingModuleSettings.METHOD_LOG_ENABLED, true );
			MethodLogConfiguration methodLogConfiguration = MethodLogConfiguration.all( 15 );
			loggingModule.setProperty( LoggingModuleSettings.METHOD_LOG_CONFIGURATION, methodLogConfiguration );

			RequestLoggerConfiguration requestLoggerConfiguration = RequestLoggerConfiguration.allRequests();
//			requestLoggerConfiguration.setExcludedPathPatterns(
//					Arrays.asList( "/static/**", "/debug/**", "/resources/images/**", "/admin/**" )
//			);
			loggingModule.setProperty( LoggingModuleSettings.REQUEST_LOGGER_CONFIGURATION, requestLoggerConfiguration );

			return loggingModule;
		}
	}
}
