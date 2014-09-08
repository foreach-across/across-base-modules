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
package com.foreach.across.modules.debugweb.servlet.logging;

import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@DebugWebController
public class RequestResponseLogController
{
	@Autowired
	private RequestResponseLogRegistry logRegistry;

	@Handler
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .item( "/logging/requestResponse", "Request - response logs", "/logging/requestResponse/list" );/*.and()
		     .item( "/logging/requestResponse/detail", "detail" ).disable()*/;
	}

	@RequestMapping("/logging/requestResponse/list")
	public String listEntries( Model model ) {
		model.addAttribute( "maxEntries", logRegistry.getMaxEntries() );
		model.addAttribute( "logEntries", logRegistry.getEntries() );

		return DebugWeb.VIEW_LOGGING_REQUEST_RESPONSE_LIST;
	}

	@RequestMapping("/logging/requestResponse/detail")
	public String detail( @RequestParam("id") UUID id, Model model ) {
		model.addAttribute( "entry", logRegistry.getEntry( id ) );

		return DebugWeb.VIEW_LOGGING_REQUEST_RESPONSE_DETAIL;
	}
}
