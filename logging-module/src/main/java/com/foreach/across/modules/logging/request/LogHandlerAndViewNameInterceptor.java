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

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Interceptor that registers both the handler name and view name as request attribute.
 * Used by the {@link RequestLoggerFilter}.
 *
 * @author Arne Vandamme
 */
public class LogHandlerAndViewNameInterceptor extends HandlerInterceptorAdapter
{
	public static final String ATTRIBUTE_HANDLER = LogHandlerAndViewNameInterceptor.class.getName() + ".HANDLER";
	public static final String ATTRIBUTE_VIEW_NAME = LogHandlerAndViewNameInterceptor.class.getName() + ".VIEW";

	@Override
	public boolean preHandle( HttpServletRequest request,
	                          HttpServletResponse response,
	                          Object handler ) throws Exception {
		request.setAttribute( ATTRIBUTE_HANDLER, handlerName( handler ) );

		return super.preHandle( request, response, handler );
	}

	@Override
	public final void postHandle( HttpServletRequest request,
	                              HttpServletResponse response,
	                              Object handler,
	                              ModelAndView modelAndView ) {
		request.setAttribute( ATTRIBUTE_HANDLER, handlerName( handler ) );

		// Redirects won't have a modelAndView
		if ( modelAndView != null ) {
			request.setAttribute( ATTRIBUTE_VIEW_NAME, modelAndView.getViewName() );
		}
	}

	private String handlerName( Object handler ) {
		if ( handler == null ) {
			return "no-handler";
		}

		if ( handler instanceof HandlerMethod ) {
			Method method = ( (HandlerMethod) handler ).getMethod();

			StringBuilder sb = new StringBuilder( method.getDeclaringClass().getSimpleName() )
					.append( "." )
					.append( method.getName() )
					.append( "(" );

			Class<?>[] params = method.getParameterTypes();
			for ( int j = 0; j < params.length; j++ ) {
				sb.append( params[j].getSimpleName() );
				if ( j < ( params.length - 1 ) ) {
					sb.append( ',' );
				}
			}

			sb.append( ")" );

			return sb.toString();
		}

		return handler.toString();
	}
}
