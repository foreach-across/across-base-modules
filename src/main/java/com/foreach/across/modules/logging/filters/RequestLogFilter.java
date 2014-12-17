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
package com.foreach.across.modules.logging.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>RequestLogFilter times http requests and logs the result.</p>
 * <p/>
 * <p>You need to define a logger with appender in your log properties/xml file or set your own custom logger using method setLogger.
 * * See below for defining new logger in your log4j properties file</p>
 * <pre>
 *
 *  Logger definition
 *  log4j.logger.com.foreach.across.modules.logging.RequestLogFilter=DEBUG,request
 *  log4j.additivity.com.foreach.across.modules.logging.RequestLogFilter=false
 *
 *  Appender definition
 *  log4j.appender.request=org.apache.log4j.rolling.RollingFileAppender
 *  log4j.appender.request.rollingPolicy=org.apache.log4j.rolling.TimeBasedRollingPolicy
 *  log4j.appender.request.rollingPolicy.FileNamePattern=${log.dir}/%d{yyyy'/'MM'/'dd'/request.'yyyy-MM-dd}.log
 *  log4j.appender.request.layout=org.apache.log4j.PatternLayout
 *  log4j.appender.request.layout.ConversionPattern=%d{ISO8601} [%t] [%X{requestId}] %m%n
 * </pre>
 * <p/>
 * <p>
 * RequestLogFilter uses {@link org.slf4j.MDC org.slf4j.MDC} for storing a unique request id which can be referred to in logfiles.
 * The unique id can be added to any logfile by adding the %X{requestId} parameter.
 * The same id can found in the request header attribute "Request-Reference".</p>
 *
 * @version 1.0
 */
public class RequestLogFilter extends OncePerRequestFilter
{
	public static final String ATTRIBUTE_START_TIME = "_log_requestStartTime";
	public static final String ATTRIBUTE_UNIQUE_ID = "_log_uniqueRequestId";
	public static final String ATTRIBUTE_VIEW_NAME = "_log_resolvedViewName";
	public static final String HEADER_REQUEST_ID = "Request-Reference";
	public static final String LOG_REQUESTID = "requestId";

	private final AtomicLong counter = new AtomicLong( System.currentTimeMillis() );

	private Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	protected void doFilterInternal( HttpServletRequest request,
	                                 HttpServletResponse response,
	                                 FilterChain chain ) throws ServletException, IOException {
		// Create a unique id for this request
		String requestId = "" + counter.getAndIncrement();

		// Put id as MDC in your logging: this can be used to identify this request in the log files
		MDC.put( LOG_REQUESTID, requestId );

		response.setHeader( HEADER_REQUEST_ID, requestId );
		request.setAttribute( ATTRIBUTE_UNIQUE_ID, requestId );
		request.setAttribute( ATTRIBUTE_START_TIME, System.currentTimeMillis() );

		// TODO get view name
		// Redirects won't have a modelAndView
//		if ( modelAndView != null ) {
//			request.setAttribute( ATTRIBUTE_VIEW_NAME, modelAndView.getViewName() );
//		}

		chain.doFilter( request, response );

		// Determine duration before sending the log
		long startTime = (Long) request.getAttribute( ATTRIBUTE_START_TIME );
		long duration = System.currentTimeMillis() - startTime;

		// TODO get handler name
		String handlerName = "";
//		String handlerName = handlerName( handler );

		StringBuilder buf = new StringBuilder();
		buf.append( '[' ).append( request.getAttribute( ATTRIBUTE_UNIQUE_ID ) ).append( ']' ).append( '\t' ).append(
				request.getRemoteAddr() ).append( '\t' ).append( request.getMethod() ).append( '\t' ).append(
				createUrlFromRequest( request ) ).append( '\t' ).append(
				request.getServletPath() ).append(
				'\t' ).append( handlerName ).append( '\t' ).append(
				request.getAttribute( ATTRIBUTE_VIEW_NAME ) ).append( '\t' ).append( duration );

		logger.debug( buf.toString() );

		// Remove the MDC
		MDC.clear();
	}

	/**
	 * Get the logger
	 *
	 * @return Logger
	 */
	protected final Logger getLogger() {
		return this.logger;
	}

	/**
	 * Specify your custom logger
	 *
	 * @param log the logger
	 */
	protected final void setLogger( Logger log ) {
		this.logger = log;
	}

	private String createUrlFromRequest( HttpServletRequest request ) {
		StringBuffer buf = request.getRequestURL();
		String qs = request.getQueryString();

		if ( qs != null ) {
			buf.append( '?' ).append( qs );
		}

		return buf.toString();
	}

}
