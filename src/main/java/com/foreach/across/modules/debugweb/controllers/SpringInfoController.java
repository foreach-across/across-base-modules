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

import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.web.table.Table;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@DebugWebController
public class SpringInfoController
{
	@Autowired
	private ApplicationContext applicationContext;

	@Event
	@SuppressWarnings( "unused" )
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .group( "/across/web", "AcrossWebModule" ).and()
		     .item( "/across/web/interceptors", "Interceptors", "/spring/interceptors" ).and()
		     .item( "/across/web/handlers", "Handlers", "/spring/handlers" );
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/spring/interceptors")
	public String showInterceptors( Model model ) {
		try {
			Map<String, AbstractHandlerMapping> handlers = BeanFactoryUtils.beansOfTypeIncludingAncestors(
					(ListableBeanFactory) applicationContext.getAutowireCapableBeanFactory(),
					AbstractHandlerMapping.class );

			Field field = AbstractHandlerMapping.class.getDeclaredField( "interceptors" );
			field.setAccessible( true );

			List<Table> tables = new LinkedList<>();
			for ( Map.Entry<String, AbstractHandlerMapping> handlerEntry : handlers.entrySet() ) {

				Table table = new Table( handlerEntry.getKey() + " - " + handlerEntry.getValue().getClass().getName() );

				List<Object> interceptors = (List<Object>) field.get( handlerEntry.getValue() );
				if ( interceptors != null ) {
					int index = 0;
					for ( Object interceptor : interceptors ) {
						table.addRow( ++index, interceptor.getClass().getName() );
					}
				}

				tables.add( table );
			}

			model.addAttribute( "handlerTables", tables );
		}
		catch ( Exception e ) {
			// Do nothing
		}

		return DebugWeb.VIEW_SPRING_INTERCEPTORS;
	}

	@RequestMapping("/spring/handlers")
	@SuppressWarnings( "unchecked" )
	public String showHandlers( Model model ) {
		Map<String, AbstractHandlerMethodMapping> handlers = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				(ListableBeanFactory) applicationContext.getAutowireCapableBeanFactory(),
				AbstractHandlerMethodMapping.class );

		List<Table> tables = new LinkedList<>();
		for ( Map.Entry<String, AbstractHandlerMethodMapping> handlerEntry : handlers.entrySet() ) {
			Table table = new Table( handlerEntry.getKey() + " - " + handlerEntry.getValue().getClass().getName() );

			Map<RequestMappingInfo, HandlerMethod> mappings = handlerEntry.getValue().getHandlerMethods();

			for ( Map.Entry<RequestMappingInfo, HandlerMethod> mapping : mappings.entrySet() ) {
				PatternsRequestCondition patterns = mapping.getKey().getPatternsCondition();
				RequestMethodsRequestCondition methodsRequestCondition = mapping.getKey().getMethodsCondition();

				Object patternLabel = patterns;
				Object methodLabel = methodsRequestCondition;

				if ( patterns.getPatterns().size() == 1 ) {
					patternLabel = patterns.getPatterns().iterator().next();
				}

				if ( methodsRequestCondition.getMethods().isEmpty() ) {
					methodLabel = "";
				}
				else if ( methodsRequestCondition.getMethods().size() == 1 ) {
					methodLabel = methodsRequestCondition.getMethods().iterator().next();
				}

				table.addRow( patternLabel, methodLabel, mapping.getValue() );
			}

			tables.add( table );
		}

		model.addAttribute( "handlerTables", tables );

		return DebugWeb.VIEW_SPRING_INTERCEPTORS;
	}
}
