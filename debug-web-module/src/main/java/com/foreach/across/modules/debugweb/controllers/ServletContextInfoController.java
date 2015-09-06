package com.foreach.across.modules.debugweb.controllers;

import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.web.table.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Map;

@DebugWebController
public class ServletContextInfoController
{
	@Autowired
	private ServletContext servletContext;

	@Event
	public void buildMenu( DebugMenuEvent event ) {
		event.builder()
		     .group( "/servlet", "Servlet context" ).order( Ordered.HIGHEST_PRECEDENCE )
		     .and()
		     .item( "/servlet/filters", "Filters" ).and()
		     .item( "/servlet/servlets", "Servlets" ).and();
	}

	@RequestMapping("/servlet/filters")
	public String showFilters( Model model ) {
		Map<String, ? extends FilterRegistration> filters = servletContext.getFilterRegistrations();

		Table table = new Table( "Servlet filters" );

		int index = 0;
		for ( Map.Entry<String, ? extends FilterRegistration> entry : filters.entrySet() ) {
			FilterRegistration filter = entry.getValue();

			table.addRow( ++index, entry.getKey(), filter.getClassName() );
		}

		model.addAttribute( "filtersTable", table );

		return DebugWeb.VIEW_SERVLET_FILTERS;
	}

	@RequestMapping("/servlet/servlets")
	public String showServlets( Model model ) {
		Map<String, ? extends ServletRegistration> servlets = servletContext.getServletRegistrations();

		Table table = new Table( "Servlets" );

		int index = 0;
		for ( Map.Entry<String, ? extends ServletRegistration> entry : servlets.entrySet() ) {
			ServletRegistration servlet = entry.getValue();

			table.addRow( ++index, entry.getKey(), servlet.getClassName() );
		}

		model.addAttribute( "servletsTable", table );

		return DebugWeb.VIEW_SERVLET_SERVLETS;
	}
}
