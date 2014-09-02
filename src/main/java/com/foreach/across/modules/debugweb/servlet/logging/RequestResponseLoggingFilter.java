package com.foreach.across.modules.debugweb.servlet.logging;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
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

	public RequestResponseLoggingFilter( RequestResponseLogRegistry logRegistry ) {
		this.logRegistry = logRegistry;
		urlPathHelper = new UrlPathHelper();
	}

	public void setUrlPathHelper( UrlPathHelper urlPathHelper ) {
		this.urlPathHelper = urlPathHelper;
	}

	public void setIncludedPathPatterns( Collection<String> includedPathPatterns ) {
		Assert.notNull( includedPathPatterns );
		this.includedPathPatterns = new HashSet<>( includedPathPatterns );
	}

	public void setExcludedPathPatterns( Collection<String> excludedPathPatterns ) {
		Assert.notNull( excludedPathPatterns );
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
}
