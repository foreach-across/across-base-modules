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

import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLogRegistry;
import com.foreach.across.modules.logging.requestresponse.RequestResponseLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@DebugWebController
public class RequestResponseLogController
{
	@Autowired
	private RequestResponseLogRegistry logRegistry;
	@Autowired
	private RequestResponseLoggingFilter logFilter;
	@Autowired
	private DebugWeb debugWeb;

	@Event
	public void buildMenu( DebugMenuEvent event ) {
		event.builder().group( "/logging/requestResponse", "Request - response logs" ).and()
		     .item( "/logging/requestResponse/list", "Overview" ).and()
		     .item( "/logging/requestResponse/settings", "Settings" );/*.and()
		     .item( "/logging/requestResponse/detail", "detail" ).disable()*/
	}

	@RequestMapping("/logging/requestResponse/list")
	public String listEntries( Model model ) {
		model.addAttribute( "maxEntries", logRegistry.getMaxEntries() );
		model.addAttribute( "paused", logFilter.isPaused() );
		model.addAttribute( "logEntries", logRegistry.getEntries() );

		return DebugWeb.VIEW_LOGGING_REQUEST_RESPONSE_LIST;
	}

	@RequestMapping("/logging/requestResponse/detail")
	public String detail( @RequestParam("id") UUID id, Model model ) {
		model.addAttribute( "entry", logRegistry.getEntry( id ) );

		return DebugWeb.VIEW_LOGGING_REQUEST_RESPONSE_DETAIL;
	}

	@RequestMapping(value = "/logging/requestResponse/settings")
	public String settings( Model model,
	                        @RequestParam(value = "excludedPathPatterns", required = false) String excludedPathPatterns,
	                        @RequestParam(value = "includedPathPatterns",
	                                      required = false) String includedPathPatterns ) {
		model.addAttribute( "logFilter", logFilter );
		if ( excludedPathPatterns != null ) {
			logFilter.setExcludedPathPatterns( fromTextArea( excludedPathPatterns ) );
		}
		if ( includedPathPatterns != null ) {
			logFilter.setIncludedPathPatterns( fromTextArea( includedPathPatterns ) );
		}
		return DebugWeb.VIEW_LOGGING_REQUEST_RESPONSE_SETTINGS;
	}

	@RequestMapping("/logging/requestResponse/pause")
	public String pauseLogger() {
		logFilter.setPaused( true );
		return debugWeb.redirect( "/logging/requestResponse/list" );
	}

	@RequestMapping("/logging/requestResponse/resume")
	public String resumeLogger() {
		logFilter.setPaused( false );
		return debugWeb.redirect( "/logging/requestResponse/list" );
	}

	private List<String> fromTextArea( String items ) {
		List<String> splitItems = Arrays.asList( items.split( "," ) );
		List<String> cleanedItems = new ArrayList<>();
		if ( splitItems.size() > 0 ) {
			for ( String item : splitItems ) {
				if ( item != null && item.length() > 0 ) {
					cleanedItems.add( item.trim() );
				}
			}
		}
		return cleanedItems;
	}
}
