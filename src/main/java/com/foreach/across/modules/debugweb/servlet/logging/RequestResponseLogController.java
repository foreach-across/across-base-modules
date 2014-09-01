package com.foreach.across.modules.debugweb.servlet.logging;

import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@DebugWebController
public class RequestResponseLogController
{
	@Autowired
	private RequestResponseLogRegistry logRegistry;

	@Handler
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .item( "/logging/requestResponse/list", "Request - response logs" );
	}

	@RequestMapping("/logging/requestResponse/list")
	public String listEntries( Model model ) {
		model.addAttribute( "logEntries", logRegistry.getEntries() );

		return DebugWeb.VIEW_LOGGING_DETAILED_LIST;
	}
}
