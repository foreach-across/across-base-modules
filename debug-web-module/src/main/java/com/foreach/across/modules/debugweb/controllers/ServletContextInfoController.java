package com.foreach.across.modules.debugweb.controllers;

import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.debugweb.support.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@DebugWebController
public class ServletContextInfoController
{
	@Autowired
	private ServletContext servletContext;

	@EventListener
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

		Table table = new Table( "Registered servlet filters" );

		for ( Map.Entry<String, ? extends FilterRegistration> entry : filters.entrySet() ) {
			FilterRegistration filter = entry.getValue();

			table.addRow( entry.getKey(), filter.getClassName() );
		}

		model.addAttribute( "filtersTable", table );

		Table filterMapsTable = new Table( "Order of registered servlet filters" );
		List<String> filterMaps = getFilterMaps();
		if ( filterMaps != null ) {
			model.addAttribute( "filterMaps", filterMapsTable );
			int index = 0;
			for ( String filterMap : filterMaps ) {
				filterMapsTable.addRow( index++, filterMap );
			}
		}

		return DebugWeb.VIEW_SERVLET_FILTERS;
	}

	public List<String> getFilterMaps() {
		Object applicationContext = getField( servletContext, "context" );
		if ( applicationContext != null ) {
			Object standardContext = getField( applicationContext, "context" );
			if ( servletContext != null ) {
				Object filterMapsObject = getField( standardContext, "filterMaps" );
				if ( filterMapsObject != null ) {
					Object filterMapArray = getField( filterMapsObject, "array" );
					if ( filterMapArray == null ) {
						// Jboss holds a FilterMap[] inside the StandardContext
						return getFieldMapList( filterMapsObject );
					}
					else {
						// Tomcat uses an internal object to represent the filter maps
						return getFieldMapList( filterMapArray );
					}
				}
			}
		}
		return null;
	}

	private List<String> getFieldMapList( Object filterMapArray ) {
		if ( filterMapArray.getClass().isArray() ) {
			List<String> filterMaps;
			filterMaps = new LinkedList<>();
			for ( Object item : (Object[]) filterMapArray ) {
				filterMaps.add( item.toString() );
			}
			return filterMaps;
		}
		else {
			return null;
		}
	}

	public Object getField( Object object, String fieldName ) {
		Field f = ReflectionUtils.findField( object.getClass(), fieldName );
		if ( f != null ) {
			try {
				ReflectionUtils.makeAccessible( f );
				return ReflectionUtils.getField( f, object );
			}
			catch ( Exception e ) {
				return null;
			}
		}
		else {
			return null;
		}
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
