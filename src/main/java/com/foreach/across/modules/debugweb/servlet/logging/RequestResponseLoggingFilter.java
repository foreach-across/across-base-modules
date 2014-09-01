package com.foreach.across.modules.debugweb.servlet.logging;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestResponseLoggingFilter extends OncePerRequestFilter
{
	private final RequestResponseLogRegistry logRegistry;

	public RequestResponseLoggingFilter( RequestResponseLogRegistry logRegistry ) {
		this.logRegistry = logRegistry;
	}

	@Override
	protected void doFilterInternal( HttpServletRequest request,
	                                 HttpServletResponse response,
	                                 FilterChain filterChain ) throws ServletException, IOException {
		long start = System.currentTimeMillis();

		LogRequestWrapper requestWrapper = new LogRequestWrapper( request );
		LogResponseWrapper responseWrapper = new LogResponseWrapper( response );

		try {
			filterChain.doFilter( request, responseWrapper );
		}
		finally {
			logRegistry.add( new RequestResponseLogEntry( start, System.currentTimeMillis(), requestWrapper,
			                                              responseWrapper ) );
		}
	}
}
