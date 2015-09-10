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
package com.foreach.across.modules.spring.security.filters;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Arne Vandamme
 */
public class LocaleChangeFilter extends OncePerRequestFilter
{
	/**
	 * Default name of the locale specification parameter: "locale".
	 */
	public static final String DEFAULT_PARAM_NAME = "locale";

	private final LocaleResolver localeResolver;

	private String paramName = DEFAULT_PARAM_NAME;

	public LocaleChangeFilter( LocaleResolver localeResolver ) {
		this.localeResolver = localeResolver;
	}

	/**
	 * Set the name of the parameter that contains a locale specification
	 * in a locale change request. Default is "locale".
	 */
	public void setParamName( String paramName ) {
		this.paramName = paramName;
	}

	/**
	 * Return the name of the parameter that contains a locale specification
	 * in a locale change request.
	 */
	public String getParamName() {
		return this.paramName;
	}

	@Override
	protected void doFilterInternal( HttpServletRequest request,
	                                 HttpServletResponse response,
	                                 FilterChain filterChain ) throws ServletException, IOException {
		String newLocale = request.getParameter( this.paramName );

		if ( newLocale != null ) {
			localeResolver.setLocale( request, response, StringUtils.parseLocaleString( newLocale ) );
		}

		filterChain.doFilter( request, response );
	}
}
