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
package com.foreach.across.modules.logging.controllers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.debugweb.support.Table;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@DebugWebController
public class LogController
{
	@EventListener
	public void buildMenu( DebugMenuEvent event ) {
		event.builder().group( "/logging", "Logging" ).and().item( "/logging/loggers", "Logger overview" );
	}

	@RequestMapping(value = "/logging/loggers", method = RequestMethod.GET)
	public String showLoggers( Model model ) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		List<Logger> loggers = loggerContext.getLoggerList();
		List<Level> levels = Arrays.asList( Level.OFF, Level.TRACE, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG );

		Map<String, Level> loggerMap = new TreeMap<>();

		Set<Appender> appenderSet = new HashSet<>();

		for ( Logger logger : loggers ) {
			loggerMap.put( logger.getName(), logger.getEffectiveLevel() );

			Iterator<Appender<ILoggingEvent>> appenders = logger.iteratorForAppenders();
			while ( appenders.hasNext() ) {
				appenderSet.add( appenders.next() );
			}
		}

		Map<String, String> appenderMap = new TreeMap<>();

		int unnamedAppenders = 0;

		for ( Appender appender : appenderSet ) {
			String appenderName = appender.getName();

			if ( appenderName == null ) {
				appenderName = "<unnamed-" + ++unnamedAppenders + ">";
			}

			if ( appender instanceof FileAppender ) {
				FileAppender fileAppender = (FileAppender) appender;
				String filename = fileAppender.getFile();
				appenderMap.put( appenderName, filename );
			}
			else {
				appenderMap.put( appenderName, appender.toString() );
			}
		}

		model.addAttribute( "levels", levels );
		model.addAttribute( "loggers", loggerMap );
		model.addAttribute( "appenders", Table.fromMap( "Appenders", appenderMap ) );

		return "th/logging/listLoggers";
	}

	@RequestMapping(value = "/logging/loggers", method = RequestMethod.POST)
	public String updateLoggers( Model model, HttpServletRequest request ) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		List<Logger> loggers = loggerContext.getLoggerList();

		int updated = 0;

		for ( Logger logger : loggers ) {
			String requestedLevel = request.getParameter( logger.getName() );

			if ( !StringUtils.isBlank( requestedLevel ) ) {
				Level newLevel = Level.valueOf( requestedLevel );

				if ( newLevel != logger.getEffectiveLevel() ) {
					logger.setLevel( newLevel );

					updated++;
				}
			}
		}

		if ( updated > 0 ) {
			model.addAttribute( "feedback", updated + " loggers have been updated." );
		}
		else {
			model.addAttribute( "feedback", "No loggers have been updated." );
		}

		return showLoggers( model );
	}
}