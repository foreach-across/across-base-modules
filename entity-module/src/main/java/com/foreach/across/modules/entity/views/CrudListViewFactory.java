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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.views.properties.ConversionServicePrintablePropertyView;
import com.foreach.across.modules.entity.views.properties.PrintablePropertyView;
import liquibase.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class CrudListViewFactory extends CommonEntityViewFactory
{
	private ListViewPageFetcher pageFetcher;

	public ListViewPageFetcher getPageFetcher() {
		return pageFetcher;
	}

	public void setPageFetcher( ListViewPageFetcher pageFetcher ) {
		this.pageFetcher = pageFetcher;
	}

	@Override
	public EntityView create( EntityConfiguration entityConfiguration, Model model ) {
		// fetch entities
		// get the properties to apply

		EntityView view = new EntityView();
		view.setViewName( getTemplate() );
		view.addModel( model );

		Pageable pageable = buildPageable( model );
		Page page = getPageFetcher().fetchPage( entityConfiguration, pageable, model );

		view.addObject( "page", page );
		view.addObject( "entities", page.getContent() );
		view.addObject( "entityConfig", entityConfiguration );
		view.addObject( "props", getProperties() );

		view.addObject( "sortQueryString", buildSortQueryString( pageable.getSort() ) );
		return view;
	}

	private String buildSortQueryString( Sort sort ) {
		List<String> sortProps = new LinkedList<>();

		if ( sort != null ) {
			for ( Sort.Order order : sort ) {
				sortProps.add( String.format( "sort=%s,%s", order.getProperty(), order.getDirection().name() ) );
			}
		}

		return StringUtils.join( sortProps, "&" );
	}

	private Pageable buildPageable( Model model ) {
		Pageable existing = (Pageable) model.asMap().get( "pageable" );

		if ( existing == null ) {
			existing = new PageRequest( 0, 50 );
			model.addAttribute( "pageable", existing );
		}

		return existing;
	}

	private List<PrintablePropertyView> getProperties() {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NoOp;

		List<EntityPropertyDescriptor> descriptors;

		if ( getPropertyComparator() != null ) {
			descriptors = getPropertyRegistry().getProperties( filter, getPropertyComparator() );
		}
		else {
			descriptors = getPropertyRegistry().getProperties( filter );
		}

		List<PrintablePropertyView> propertyViews = new ArrayList<>( descriptors.size() );
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService( true );
		conversionService.addFormatter( new DateFormatter( "dd MMM yyyy - HH:mm" ) );

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			propertyViews.add( new ConversionServicePrintablePropertyView( conversionService, descriptor ) );
		}

		return propertyViews;
	}
}
