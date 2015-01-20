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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.views.properties.ConversionServicePrintablePropertyView;
import com.foreach.across.modules.entity.views.properties.PrintablePropertyView;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.ui.Model;

import java.util.*;

/**
 * Handles a list of items (entities) with support for the properties to show,
 * paging, sorting and configuring the sortable properties.
 *
 * @author Arne Vandamme
 */
public class EntityListViewFactory extends CommonEntityViewFactory
{
	private int pageSize = 50;
	private boolean showResultNumber = true;

	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private ListViewPageFetcher pageFetcher;

	public ListViewPageFetcher getPageFetcher() {
		return pageFetcher;
	}

	/**
	 * @param pageFetcher The ListViewPageFetcher to use for retrieving the actual items.
	 */
	public void setPageFetcher( ListViewPageFetcher pageFetcher ) {
		this.pageFetcher = pageFetcher;
	}

	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize The default page size to use if no custom Pageable is passed.
	 */
	public void setPageSize( int pageSize ) {
		this.pageSize = pageSize;
	}

	public Sort getDefaultSort() {
		return defaultSort;
	}

	/**
	 * @param defaultSort The default sort to use if no custom Pageable or Sort is passed.
	 */
	public void setDefaultSort( Sort defaultSort ) {
		this.defaultSort = defaultSort;
	}

	public Collection<String> getSortableProperties() {
		return sortableProperties;
	}

	/**
	 * @param sortableProperties Names of the properties that should be sortable in the UI.
	 */
	public void setSortableProperties( Collection<String> sortableProperties ) {
		this.sortableProperties = sortableProperties;
	}

	public boolean isShowResultNumber() {
		return showResultNumber;
	}

	/**
	 * @param showResultNumber True if the index of an entity in the total results should be displayed.
	 */
	public void setShowResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
	}

	@Override
	public EntityView create( EntityConfiguration entityConfiguration, Model model ) {
		EntityListView view = new EntityListView();
		view.setEntityConfiguration( entityConfiguration );
		view.setViewName( getTemplate() );
		view.addModel( model );

		view.addObject( "entityLinks", entityConfiguration.getAttribute( EntityLinkBuilder.class ) );

		Pageable pageable = buildPageable( view );
		Page page = getPageFetcher().fetchPage( entityConfiguration, pageable, model );

		view.setPageable( pageable );
		view.setPage( page );
		view.setShowResultNumber( isShowResultNumber() );

		//view.addObject( "page", page );
		view.addObject( "entities", page.getContent() );
		//view.addObject( "entityConfig", entityConfiguration );
		view.addObject( "props", getProperties() );

		Map<String, String> messages = new HashMap<>();
		MessageSource messageSource = (MessageSource) model.asMap().get( "messageSource" );
		messages.put( "buttonCreateNewText", messageSource.getMessage(
				new DefaultMessageSourceResolvable( new String[] { "create.new.button" }, new Object[] {
						StringUtils.uncapitalize( entityConfiguration.getDisplayName() ) }, "Create a new {0}" ),
				LocaleContextHolder.getLocale() ) );

		model.addAttribute( "messages", messages );
/*Create a new ${T(org.apache.commons.lang3.StringUtils).uncapitalize(entityConfiguration.displayName)}*/

		return view;
	}

	private Pageable buildPageable( EntityListView view ) {
		Pageable existing = view.getPageable();

		if ( existing == null ) {
			existing = new PageRequest( 0, getPageSize(), getDefaultSort() );
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
			SortablePropertyView propertyView = new SortablePropertyView(
					new ConversionServicePrintablePropertyView( conversionService, descriptor )
			);
			propertyView.setSortableProperty( determineSortableProperty( descriptor ) );

			propertyViews.add( propertyView );
		}

		return propertyViews;
	}

	private String determineSortableProperty( EntityPropertyDescriptor descriptor ) {
		String sortableProperty = descriptor.getAttribute( EntityAttributes.SORTABLE_PROPERTY, String.class );

		if ( sortableProperties != null && !sortableProperties.contains( descriptor.getName() ) ) {
			sortableProperty = null;
		}

		return sortableProperty;
	}

	public static class SortablePropertyView implements PrintablePropertyView
	{
		private final PrintablePropertyView wrapped;
		private String sortableProperty;

		public SortablePropertyView( PrintablePropertyView wrapped ) {
			this.wrapped = wrapped;
		}

		@Override
		public String getName() {
			return wrapped.getName();
		}

		@Override
		public String getDisplayName() {
			return wrapped.getDisplayName();
		}

		@Override
		public String getCustomTemplate() {
			return wrapped.getCustomTemplate();
		}

		@Override
		public Object value( Object entity ) {
			return wrapped.value( entity );
		}

		@Override
		public String print( Object entity ) {
			return wrapped.print( entity );
		}

		public boolean isSortable() {
			return sortableProperty != null;
		}

		public String getSortableProperty() {
			return sortableProperty;
		}

		public void setSortableProperty( String sortableProperty ) {
			this.sortableProperty = sortableProperty;
		}
	}
}
