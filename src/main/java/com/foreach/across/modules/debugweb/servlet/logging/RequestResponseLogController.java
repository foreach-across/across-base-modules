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
