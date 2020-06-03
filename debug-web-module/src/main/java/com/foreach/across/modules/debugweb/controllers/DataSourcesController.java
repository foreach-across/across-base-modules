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

import com.foreach.across.modules.debugweb.DebugWeb;
import com.foreach.across.modules.debugweb.mvc.DebugMenuEvent;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import com.foreach.across.modules.debugweb.support.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@DebugWebController
public class DataSourcesController
{
	@Autowired(required = false)
	private List<DataSource> dataSources;

	@EventListener
	public void buildMenu( DebugMenuEvent event ) {
		event.builder().group( "/datasources", "DataSources" ).and()
		     .item( "/datasources/drivers", "Drivers" ).and()
		     .item( "/datasources/overview", "Overview" );
	}

	@RequestMapping("/datasources/drivers")
	public String showDrivers( Model model ) {
		List<Table> tablesDrivers = new LinkedList<>();

		Enumeration<Driver> drivers = DriverManager.getDrivers();

		while ( drivers.hasMoreElements() ) {
			Driver driver = drivers.nextElement();
			Table tableDrivers = new Table(
					driver.toString() + " , major: " + driver.getMajorVersion() + " , minor: " + driver
							.getMinorVersion() + " jdbc compliant: " + driver.jdbcCompliant() );
			interestingFields( driver, tableDrivers );

			tablesDrivers.add( tableDrivers );
		}

		model.addAttribute( "drivers", tablesDrivers );
		return DebugWeb.VIEW_DATASOURCES;
	}

	@RequestMapping("/datasources/overview")
	public String showDatasources( Model model ) {
		List<Table> tables = new LinkedList<>();
		if ( !CollectionUtils.isEmpty( dataSources ) ) {
			int i = 1;
			for ( DataSource dataSource : dataSources ) {
				if ( dataSource != null ) {
					Table table = new Table( "Datasource " + i );
					interestingFields( dataSource, table );
					tables.add( table );

					Table metadata = new Table( "Datasource metadata " + i );
					try (Connection conn = dataSource.getConnection()) {
						interestingFields( conn.getMetaData(), metadata, true );
					}
					catch ( SQLException e ) {
						// Do nothing
					}

					tables.add( metadata );
					i++;
				}
			}
		}
		model.addAttribute( "drivers", tables );
		return DebugWeb.VIEW_DATASOURCES;
	}

	private void interestingFields( Object object, Table table ) {
		interestingFields( object, table, false );
	}

	private void interestingFields( Object object, Table table, boolean allPrimitiveMethods ) {
		Method[] methods = ReflectionUtils.getAllDeclaredMethods( object.getClass() );
		for ( Method m : methods ) {
			try {
				String name = m.getName();
				Class returnType = m.getReturnType();
				if ( allPrimitiveMethods || StringUtils.startsWith( name, "get" ) || ( StringUtils.startsWith( name,
				                                                                                               "is" ) && ( returnType == Boolean.class || returnType == boolean.class ) ) ) {
					if ( m.getParameterTypes().length == 0 ) {
						if ( BeanUtils.isSimpleValueType( returnType ) ) {
							Object o = m.invoke( object );
							table.addRow( m.getName(), o );
						}
					}
				}
			}
			catch ( IllegalAccessException | NoClassDefFoundError | InvocationTargetException e ) {
				table.addRow( m.getName(), "-" );
			}
		}

		String fieldsString = ReflectionToStringBuilder.reflectionToString( object, ToStringStyle.MULTI_LINE_STYLE,
		                                                                    true );
		if ( StringUtils.isNotBlank( fieldsString ) ) {
			for ( String keyValue : StringUtils.split( fieldsString, System.lineSeparator() ) ) {
				if ( StringUtils.contains( keyValue, "=" ) ) {
					table.addRow( StringUtils.split( keyValue, "=", 2 ) );
				}
			}
		}
	}

}
