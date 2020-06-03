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
package com.foreach.across.modules.logging.requestresponse;

import lombok.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class RequestResponseLoggingFilter extends OncePerRequestFilter
{
	private final RequestResponseLogRegistry logRegistry;

	private UrlPathHelper urlPathHelper;
	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	private Collection<String> includedPathPatterns = Collections.emptyList();
	private Collection<String> excludedPathPatterns = Collections.emptyList();

	private boolean paused;

	public RequestResponseLoggingFilter( RequestResponseLogRegistry logRegistry, boolean paused ) {
		this.logRegistry = logRegistry;
		urlPathHelper = new UrlPathHelper();
		this.paused = paused;
	}

	public void setUrlPathHelper( UrlPathHelper urlPathHelper ) {
		this.urlPathHelper = urlPathHelper;
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

	@Override
	protected void doFilterInternal( HttpServletRequest request,
	                                 HttpServletResponse response,
	                                 FilterChain filterChain ) throws ServletException, IOException {
		if ( shouldLog( request ) ) {
			long start = System.currentTimeMillis();

			LogRequestWrapper requestWrapper = new LogRequestWrapper( request );
			LogResponseWrapper responseWrapper = new LogResponseWrapper( response );

			try {
				filterChain.doFilter( requestWrapper, responseWrapper );
			}
			finally {
				logRegistry.add( new RequestResponseLogEntry( start, System.currentTimeMillis(), requestWrapper,
				                                              responseWrapper ) );
			}
		}
		else {
			filterChain.doFilter( request, response );
		}
	}

	private boolean shouldLog( HttpServletRequest request ) {
		if ( paused ) {
			return false;
		}
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

	public boolean isPaused() {
		return paused;
	}

	public void setPaused( boolean paused ) {
		this.paused = paused;
	}
}
