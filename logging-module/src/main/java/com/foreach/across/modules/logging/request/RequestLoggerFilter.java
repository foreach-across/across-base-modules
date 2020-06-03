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

package com.foreach.across.modules.logging.request;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

/**
 * <p>RequestLoggerFilter times http requests and logs the result in a tab separated fashion.
 * Logs the following attributes if they are available:
 * <ul>
 * <li>Remote address</li>
 * <li>HTTP method</li>
 * <li>URL</li>
 * <li>Servlet path</li>
 * <li>Best matching request mapping</li>
 * <li>Handler name</li>
 * <li>View name</li>
 * <li>HTTP status code</li>
 * <li>Duration (ms)</li>
 * </ul>
 * </p>
 * <p>
 * RequestLoggerFilter uses {@link org.slf4j.MDC org.slf4j.MDC} for storing a unique request id which can be referred to in logfiles.
 * The unique id can be added to any logfile by adding the %X{requestId} parameter.
 * The same id can found in the request header attribute "Request-Reference".</p>
 *
 * @version 1.0
 */
public class RequestLoggerFilter extends OncePerRequestFilter
{
	public static final String HEADER_REQUEST_ID = "Request-Reference";
	public static final String LOG_INSTANCEID = "applicationInstanceId";
	public static final String LOG_REQUESTID = "requestId";
	public static final String ATTRIBUTE_UNIQUE_ID = "_log_uniqueRequestId";
	public static final String ATTRIBUTE_START_TIME = "_log_requestStartTime";

	private static final Logger REQUEST_LOG = RequestLogger.LOG;

	private static final String LOG_FORMAT = "{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}";

	private final UrlPathHelper urlPathHelper = new UrlPathHelper();
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	private String instanceId = "instance-not-set";

	private Collection<String> includedPathPatterns = Collections.emptyList();
	private Collection<String> excludedPathPatterns = Collections.emptyList();
	private LoggerLevelThreshold loggerLevelThreshold;

	public void setInstanceId( String instanceId ) {
		this.instanceId = instanceId;
	}

	@Override
	protected void doFilterInternal( HttpServletRequest request,
	                                 HttpServletResponse response,
	                                 FilterChain chain ) throws ServletException, IOException {
		if ( shouldLog( request ) ) {
			// Create a unique id for this request
			String requestId = UUID.randomUUID().toString();

			// Put id as MDC in your logging: this can be used to identify this request in the log files
			MDC.put( LOG_INSTANCEID, instanceId );
			MDC.put( LOG_REQUESTID, requestId );

			response.setHeader( HEADER_REQUEST_ID, requestId );
			request.setAttribute( ATTRIBUTE_UNIQUE_ID, requestId );

			long startTime = System.currentTimeMillis();
			request.setAttribute( ATTRIBUTE_START_TIME, startTime );

			boolean finished = false;

			try {
				chain.doFilter( request, response );

				finished = true;
			}
			finally {
				if( loggerLevelThreshold != null ) {
					long duration = System.currentTimeMillis() - startTime;
					LoggerLevelThreshold.LoggerLevel loggerLevel = loggerLevelThreshold.getLogLevelForDuration( duration );
					switch ( loggerLevel ) {
						case DEBUG:
							if( REQUEST_LOG.isDebugEnabled() ) {
								REQUEST_LOG.debug( LOG_FORMAT, loggingArguments( request, response, startTime, finished ) );
							}
							break;
						case INFO:
							if( REQUEST_LOG.isInfoEnabled() ) {
								REQUEST_LOG.info( LOG_FORMAT, loggingArguments( request, response, startTime,
								                                                finished ) );
							}
							break;
						case WARN:
							if( REQUEST_LOG.isWarnEnabled() ) {
								REQUEST_LOG.warn( LOG_FORMAT, loggingArguments( request, response, startTime, finished ) );
							}
							break;
						case ERROR:
							if( REQUEST_LOG.isErrorEnabled() ) {
								REQUEST_LOG.error( LOG_FORMAT, loggingArguments( request, response, startTime, finished ) );
							}
							break;
					}
				} else {
					if ( REQUEST_LOG.isDebugEnabled() ) {
						REQUEST_LOG.debug( LOG_FORMAT, loggingArguments( request, response, startTime, finished ) );
					}
				}
			}

			// Remove the MDC
			MDC.clear();
		}
		else {
			chain.doFilter( request, response );
		}
	}

	private Object[] loggingArguments( HttpServletRequest request,
	                               HttpServletResponse response,
	                               long startTime,
	                               boolean finished ) {
		long duration = System.currentTimeMillis() - startTime;

		String handlerName = (String) request.getAttribute(
				LogHandlerAndViewNameInterceptor.ATTRIBUTE_HANDLER );
		String viewName = (String) request.getAttribute(
				LogHandlerAndViewNameInterceptor.ATTRIBUTE_VIEW_NAME );
		String requestMapping = (String) request.getAttribute(
				HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE );

		return new Object[] {
				request.getRemoteAddr(),
				request.getMethod(),
				createUrlFromRequest( request ),
				request.getServletPath(),
				StringUtils.defaultString( requestMapping, "-" ),
				StringUtils.defaultString( handlerName, "-" ),
				StringUtils.defaultString( viewName, "-" ),
				finished ? response.getStatus() : -1,
				duration };
	}

	public Collection<String> getIncludedPathPatterns() {
		return includedPathPatterns;
	}

	public void setIncludedPathPatterns( @NonNull Collection<String> includedPathPatterns ) {
		this.includedPathPatterns = new HashSet<>( includedPathPatterns );
	}

	public Collection<String> getExcludedPathPatterns() {
		return excludedPathPatterns;
	}

	public void setExcludedPathPatterns( @NonNull Collection<String> excludedPathPatterns ) {
		this.excludedPathPatterns = new HashSet<>( excludedPathPatterns );
	}

	private boolean shouldLog( HttpServletRequest request ) {
		String path = urlPathHelper.getLookupPathForRequest( request );

		if ( !excludedPathPatterns.isEmpty() ) {
			for ( String pattern : excludedPathPatterns ) {
				if ( antPathMatcher.match( pattern, path ) ) {
					return false;
				}
			}
		}

		if ( !includedPathPatterns.isEmpty() ) {
			for ( String pattern : includedPathPatterns ) {
				if ( antPathMatcher.match( pattern, path ) ) {
					return true;
				}
			}
		}

		return includedPathPatterns.isEmpty();
	}

	private String createUrlFromRequest( HttpServletRequest request ) {
		StringBuffer buf = request.getRequestURL();
		String qs = request.getQueryString();

		if ( qs != null ) {
			buf.append( '?' ).append( qs );
		}

		return buf.toString();
	}

	public void setLoggerLevelThreshold( LoggerLevelThreshold loggerLevelThreshold ) {
		this.loggerLevelThreshold = loggerLevelThreshold;
	}
}
