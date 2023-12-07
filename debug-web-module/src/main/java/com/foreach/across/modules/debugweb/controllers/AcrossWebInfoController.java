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
package com.foreach.across.modules.debugweb.controllers;

import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DebugWebController
@RequiredArgsConstructor
public class AcrossWebInfoController
{
	private static Logger LOG = LoggerFactory.getLogger( AcrossWebInfoController.class );

	private final ApplicationContext applicationContext;

	@EventListener
	@SuppressWarnings("unused")
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .group( "/across/web", "AcrossWebModule" ).and()
		     .item( "/across/web/interceptors", "Interceptors", "/spring/interceptors" ).and()
		     .item( "/across/web/handlers", "Handlers", "/spring/handlers" );
	}

	@RequestMapping("/spring/handlers")
	@SuppressWarnings("unchecked")
	public String showHandlers( Model model ) {
		Map<String, AbstractHandlerMethodMapping> handlers = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				(ListableBeanFactory) applicationContext.getAutowireCapableBeanFactory(),
				AbstractHandlerMethodMapping.class );
		List<HandlerMappingInfo> handlerMappings = new ArrayList<>( handlers.size() );

		for ( Map.Entry<String, AbstractHandlerMethodMapping> handlerEntry : handlers.entrySet() ) {
			HandlerMappingInfo mappingInfo = new HandlerMappingInfo();
			mappingInfo.setBeanName( handlerEntry.getKey() );
			mappingInfo.setHandlerType( handlerEntry.getValue().getClass().getName() );

			Map<RequestMappingInfo, HandlerMethod> mappings = handlerEntry.getValue().getHandlerMethods();

			List<HandlerMethodInfo> methods = new ArrayList<>();
			mappingInfo.setMethods( methods );

			for ( Map.Entry<RequestMappingInfo, HandlerMethod> mapping : mappings.entrySet() ) {
				HandlerMethodInfo methodInfo = new HandlerMethodInfo();

				PatternsRequestCondition patterns = mapping.getKey().getPatternsCondition();
				RequestMethodsRequestCondition methodsRequestCondition = mapping.getKey().getMethodsCondition();

				Object patternLabel = patterns;
				Object methodLabel = methodsRequestCondition;

				if ( patterns.getPatterns().size() == 1 ) {
					patternLabel = patterns.getPatterns().iterator().next();
				}

				methodInfo.setPattern( Objects.toString( patternLabel ) );

				if ( methodsRequestCondition.getMethods().isEmpty() ) {
					methodLabel = "";
				}
				else if ( methodsRequestCondition.getMethods().size() == 1 ) {
					methodLabel = methodsRequestCondition.getMethods().iterator().next();
				}

				methodInfo.setMappingType( Objects.toString( methodLabel ) );

				HandlerMethod handlerMethod = mapping.getValue();
				methodInfo.setMethodOwnerType( handlerMethod.getBeanType().getName() );
				methodInfo.setMethodSignature( handlerMethod.toString() );

				Method targetMethod = handlerMethod.getMethod();
				if ( targetMethod != null ) {
					StringBuilder signature = new StringBuilder( targetMethod.getReturnType().getSimpleName() )
							.append( " " )
							.append( targetMethod.getName() )
							.append( "(" );

					if ( targetMethod.getParameterTypes().length > 0 ) {
						signature.append( " " )
						         .append(
								         Stream.of( targetMethod.getParameterTypes() )
								               .map( Class::getSimpleName )
								               .collect( Collectors.joining( ", " ) )
						         )
						         .append( " " );
					}
					signature.append( ")" );
					methodInfo.setMethodSignature( signature.toString() );
				}
				methods.add( methodInfo );
			}

			handlerMappings.add( mappingInfo );
		}

		model.addAttribute( "handlerMappings", handlerMappings );

		return "th/debugweb/web/handlerMappings";
	}

	@SuppressWarnings("all")
	@RequestMapping("/spring/interceptors")
	public String showInterceptors( Model model ) {
		try {
			Map<String, AbstractHandlerMapping> handlers = BeanFactoryUtils.beansOfTypeIncludingAncestors(
					(ListableBeanFactory) applicationContext.getAutowireCapableBeanFactory(),
					AbstractHandlerMapping.class );

			Field interceptorsField = AbstractHandlerMapping.class.getDeclaredField( "interceptors" );
			interceptorsField.setAccessible( true );

			Method mappedInterceptorsMethod = AbstractHandlerMapping.class.getDeclaredMethod( "getMappedInterceptors" );
			mappedInterceptorsMethod.setAccessible( true );

			List<HandlerMappingInfo> handlerMappings = new ArrayList<>( handlers.size() );

			for ( Map.Entry<String, AbstractHandlerMapping> handlerEntry : handlers.entrySet() ) {
				HandlerMappingInfo mappingInfo = new HandlerMappingInfo();
				mappingInfo.setBeanName( handlerEntry.getKey() );
				mappingInfo.setHandlerType( handlerEntry.getValue().getClass().getName() );

				List<HandlerInterceptorInfo> interceptorInfo = new ArrayList<>();

				List<Object> interceptors = (List<Object>) interceptorsField.get( handlerEntry.getValue() );
				if ( interceptors != null ) {
					int index = 0;
					for ( Object interceptor : interceptors ) {
						interceptorInfo.add( new HandlerInterceptorInfo( "ordered", "" + ++index, interceptor.getClass().getName() ) );
					}
				}

				MappedInterceptor[] mappedInterceptors = (MappedInterceptor[]) mappedInterceptorsMethod.invoke( handlerEntry.getValue() );
				if ( mappedInterceptors != null ) {
					for ( MappedInterceptor interceptor : mappedInterceptors ) {
						interceptorInfo.add( new HandlerInterceptorInfo(
								"mapped",
								StringUtils.join( interceptor.getPathPatterns(), ", " ),
								interceptor.getInterceptor().getClass().getName()
						) );
					}
				}

				mappingInfo.setInterceptors( interceptorInfo );
				handlerMappings.add( mappingInfo );
			}

			model.addAttribute( "handlerMappings", handlerMappings );
		}
		catch ( Exception ignore ) {
			// Do nothing
			LOG.warn( "Exception occured while getting interceptors", ignore );
		}

		return "th/debugweb/web/handlerInterceptors";
	}

	@Data
	static class HandlerMappingInfo
	{
		private String beanName;
		private String handlerType;

		private Collection<HandlerInterceptorInfo> interceptors;
		private Collection<HandlerMethodInfo> methods;
	}

	@Data
	static class HandlerMethodInfo
	{
		private String pattern;
		private String mappingType;
		private String methodOwnerType;
		private String methodSignature;
	}

	@Data
	@AllArgsConstructor
	static class HandlerInterceptorInfo
	{
		private String type;
		private String mapping;
		private String interceptorClass;
	}
}
