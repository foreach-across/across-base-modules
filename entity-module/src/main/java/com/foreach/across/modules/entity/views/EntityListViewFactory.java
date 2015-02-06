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
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ViewElement;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderContext;
import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.support.ListViewEntityMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;

/**
 * Handles a list of items (entities) with support for the properties to show,
 * paging, sorting and configuring the sortable properties.
 *
 * @author Arne Vandamme
 */
public class EntityListViewFactory extends ConfigurablePropertiesEntityViewFactorySupport<EntityListView>
{
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
	protected EntityListView createEntityView() {
		return new EntityListView();
	}

	@Override
	protected void extendViewModel( EntityConfiguration entityConfiguration, EntityListView view ) {
		Pageable pageable = buildPageable( view );
		Page page = getPageFetcher().fetchPage( entityConfiguration, pageable, view );

		view.setPageable( pageable );
		view.setPage( page );
		view.setShowResultNumber( isShowResultNumber() );
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
	protected ViewElement createPropertyView( ViewElementBuilderContext builderContext,
	                                          EntityPropertyDescriptor descriptor ) {
		SortablePropertyViewElement sortablePropertyView = new SortablePropertyViewElement(
				super.createPropertyView( builderContext, descriptor )
		);
		sortablePropertyView.setSortableProperty( determineSortableProperty( descriptor ) );

		return sortablePropertyView;
	}

	private String determineSortableProperty( EntityPropertyDescriptor descriptor ) {
		String sortableProperty = descriptor.getAttribute( EntityAttributes.SORTABLE_PROPERTY, String.class );

		if ( sortableProperties != null && !sortableProperties.contains( descriptor.getName() ) ) {
			sortableProperty = null;
		}

		return sortableProperty;
	}

	@Override
	protected ViewElementMode getMode() {
		return ViewElementMode.FOR_READING;
	}

	@Deprecated
	public static class SortablePropertyViewElement implements ViewElement
	{
		private final ViewElement wrapped;
		private String sortableProperty;

		public SortablePropertyViewElement( ViewElement wrapped ) {
			this.wrapped = wrapped;
		}

		@Override
		public String getElementType() {
			return "sortable-property";
		}

		@Override
		public String getName() {
			return wrapped.getName();
		}

		@Override
		public String getLabel() {
			return wrapped.getLabel();
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
