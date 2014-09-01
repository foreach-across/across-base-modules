package com.foreach.across.modules.debugweb.config;

import com.foreach.across.modules.debugweb.servlet.logging.RequestResponseLogController;
import com.foreach.across.modules.debugweb.servlet.logging.RequestResponseLogRegistry;
import com.foreach.across.modules.debugweb.servlet.logging.RequestResponseLoggingFilter;
import com.foreach.across.modules.web.servlet.AcrossWebDynamicServletConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

@Configuration
public class DebugWebLoggingConfiguration extends AcrossWebDynamicServletConfigurer
{
	@Bean
	public RequestResponseLogRegistry requestResponseLogRegistry() {
		return new RequestResponseLogRegistry();
	}

	@Bean
	public RequestResponseLogController requestResponseLogController() {
		return new RequestResponseLogController();
	}

	@Override
	protected void dynamicConfigurationAllowed( ServletContext servletContext ) throws ServletException {
		FilterRegistration.Dynamic filter = servletContext.addFilter(
				"loggingFilter", new RequestResponseLoggingFilter( requestResponseLogRegistry() )
		);
		filter.addMappingForUrlPatterns( EnumSet.allOf( DispatcherType.class ), false, "/*" );
	}

	@Override
	protected void dynamicConfigurationDenied( ServletContext servletContext ) throws ServletException {

	}
}
