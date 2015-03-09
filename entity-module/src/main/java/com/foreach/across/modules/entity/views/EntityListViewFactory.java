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

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.*;
import com.foreach.across.modules.entity.views.elements.button.ButtonViewElement;
import com.foreach.across.modules.entity.views.elements.container.ContainerViewElement;
import com.foreach.across.modules.entity.views.elements.table.SortableTableHeaderCellProcessor;
import com.foreach.across.modules.entity.views.elements.table.TableRowProcessor;
import com.foreach.across.modules.entity.views.elements.table.TableViewElement;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.support.ListViewEntityMessages;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.resource.WebResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles a list of items (entities) with support for the properties to show,
 * paging, sorting and configuring the sortable properties.
 *
 * @author Arne Vandamme
 */
public class EntityListViewFactory<V extends ViewCreationContext> extends ConfigurablePropertiesEntityViewFactorySupport<V, EntityListView>
{
	@Autowired
	private AdminWeb adminWeb;

	private int pageSize = 50;
	private boolean showResultNumber = true;

	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private EntityListViewPageFetcher pageFetcher;

	public EntityListViewPageFetcher getPageFetcher() {
		return pageFetcher;
	}

	/**
	 * @param pageFetcher The ListViewPageFetcher to use for retrieving the actual items.
	 */
	public void setPageFetcher( EntityListViewPageFetcher pageFetcher ) {
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
	protected EntityListView createEntityView( ModelMap model ) {
		return new EntityListView( model );
	}

	@Override
	protected void extendViewModel( V viewCreationContext, final EntityListView view ) {
		Pageable pageable = buildPageable( view );
		Page page = getPageFetcher().fetchPage( viewCreationContext, pageable, view );

		view.setPageable( pageable );
		view.setPage( page );
		view.setShowResultNumber( isShowResultNumber() );

		SortableTableHeaderCellProcessor sortableTableHeaderCellProcessor = new SortableTableHeaderCellProcessor();
		sortableTableHeaderCellProcessor.setSortableProperties( sortableProperties );
		sortableTableHeaderCellProcessor.setViewElementDescriptorMap(
				(Map<ViewElement, EntityPropertyDescriptor>)
						viewCreationContext.removeAttribute( "descriptorElementsMap" )
		);

		TableViewElement table = new TableViewElement();
		table.getHeader().setCellProcessor( sortableTableHeaderCellProcessor );
		table.setName( "resultsTable" );
		table.setPage( page );
		table.setShowResultNumber( isShowResultNumber() );
		table.setColumns( (Iterable<ViewElement>) view.getEntityProperties().remove( "table" ) );

		Map<String, String> tableAttributs = new HashMap<>();
		tableAttributs.put( "data-tbl", "entity-list" );
		tableAttributs.put( "data-tbl-type", "paged" );
		tableAttributs.put( "data-tbl-entity-type", view.getEntityConfiguration().getName() );
		tableAttributs.put( "data-tbl-current-page", "" + page.getNumber() );
		tableAttributs.put( "data-tbl-size", "" + page.getSize() );
		tableAttributs.put( "data-tbl-sort", "" + page.getSort() );

		table.setAttributes( tableAttributs );

		boolean hasListSummaryView = viewCreationContext.isForAssociation()
				? viewCreationContext.getEntityAssociation().hasView( EntityListView.SUMMARY_VIEW_NAME )
				: viewCreationContext.getEntityConfiguration().hasView( EntityListView.SUMMARY_VIEW_NAME );

		if ( hasListSummaryView ) {
			table.setRowProcessor( new TableRowProcessor()
			{
				@Override
				public Map<String, String> attributes( Object entity ) {
					return Collections.singletonMap(
							"data-summary-url",
							ServletUriComponentsBuilder
									.fromCurrentContextPath()
									.path( adminWeb.path( view.getEntityLinkBuilder().view( entity ) ) )
									.queryParam( "view", EntityListView.SUMMARY_VIEW_NAME )
									.queryParam( "_partial", "content" )
									.toUriString()
					);
				}
			} );

			if ( viewCreationContext instanceof WebViewCreationContext ) {
				( (WebViewCreationContext) viewCreationContext )
						.getWebResourceRegistry()
						.add( WebResource.JAVASCRIPT_PAGE_END, "/js/entity/expandable.js", WebResource.VIEWS );
			}
		}

		ContainerViewElement buttons = new ContainerViewElement( "buttons" );
		buttons.setElementType( "paragraph" );

		EntityMessages messages = view.getEntityMessages();

		ButtonViewElement create = new ButtonViewElement();
		create.setName( "btn-create" );
		create.setElementType( CommonViewElements.LINK_BUTTON );
		create.setLink( view.getEntityLinkBuilder().create() );
		create.setLabel( messages.createAction() );
		buttons.add( create );

		ContainerViewElement itemButtons = new ContainerViewElement( "itemButtons" );

		ButtonViewElement edit = new ButtonViewElement()
		{
			@Override
			public String print( Object entity ) {
				return view.getEntityLinkBuilder().update( entity );
			}
		};
		edit.setName( "btn-edit" );
		edit.setElementType( CommonViewElements.LINK_BUTTON );
		edit.setStyle( ButtonViewElement.Style.ICON );
		edit.setIcon( "edit" );
		edit.setLabel( messages.updateAction() );

		itemButtons.add( edit );

		( (ViewElements) table.getColumns() ).add( itemButtons );

		view.getEntityProperties().addFirst( table );
		view.getEntityProperties().addFirst( buttons );
	}

	private Pageable buildPageable( EntityListView view ) {
		Pageable existing = view.getPageable();

		if ( existing == null ) {
			existing = new PageRequest( 0, getPageSize(), getDefaultSort() );
		}

		return existing;
	}

	@Override
	protected EntityMessages createEntityMessages( EntityMessageCodeResolver codeResolver ) {
		return new ListViewEntityMessages( codeResolver );
	}

	@Override
	protected void buildViewElements( V viewCreationContext,
	                                  ViewElementBuilderContext builderContext,
	                                  Collection<EntityPropertyDescriptor> descriptors,
	                                  ViewElements viewElements ) {
		Map<ViewElement, EntityPropertyDescriptor> descriptorMap = new HashMap<>();

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			ViewElement propertyView = createPropertyView( builderContext, descriptor );

			if ( propertyView != null ) {
				descriptorMap.put( propertyView, descriptor );
				viewElements.add( propertyView );
			}
		}

		viewCreationContext.addAttribute( "descriptorElementsMap", descriptorMap );
	}

	@Override
	protected ViewElements customizeViewElements( ViewElements elements ) {
		ContainerViewElement root = new ContainerViewElement( "root" );

		// Props are in fact the table members
		ContainerViewElement table = new ContainerViewElement( "table" );
		table.addAll( elements );

		root.add( table );

		return root;
	}

	@Override
	protected ViewElementMode getMode() {
		return ViewElementMode.FOR_READING;
	}
}
