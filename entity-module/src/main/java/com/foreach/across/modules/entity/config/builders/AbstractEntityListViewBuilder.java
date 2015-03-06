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
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;

/**
 * Builder class for configuring a {@link com.foreach.across.modules.entity.views.EntityListViewFactory}.
 *
 * @author Arne Vandamme
 */
@SuppressWarnings( "unchecked" )
public abstract class AbstractEntityListViewBuilder<SELF extends AbstractSimpleEntityViewBuilder>
		extends AbstractSimpleEntityViewBuilder<EntityListViewFactory, SELF>
{
	private Boolean showResultNumber;
	private Integer pageSize;
	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private EntityListViewPageFetcher pageFetcher;

	@Override
	protected EntityListViewFactory createFactoryInstance( AutowireCapableBeanFactory beanFactory ) {
		return beanFactory.getBean( EntityListViewFactory.class );
	}

	/**
	 * Configure the page fetcher on the view.
	 *
	 * @param pageFetcher instance - may not be null
	 * @return current builder
	 */
	public SELF pageFetcher( EntityListViewPageFetcher pageFetcher ) {
		Assert.notNull( pageFetcher );
		this.pageFetcher = pageFetcher;
		return (SELF) this;
	}

	/**
	 * @param pageSize number of results per page.
	 * @return current builder
	 */
	public SELF pageSize( int pageSize ) {
		this.pageSize = pageSize;
		return (SELF) this;
	}

	/**
	 * @param propertyNames of properties that can be sorted on
	 * @return current builder
	 */
	public SELF sortableOn( String... propertyNames ) {
		this.sortableProperties = Arrays.asList( propertyNames );
		return (SELF) this;
	}

	/**
	 * @param sort default sort instance that should be applied when fetching
	 * @return current builder
	 */
	public SELF defaultSort( Sort sort ) {
		this.defaultSort = sort;
		return (SELF) this;
	}

	/**
	 * @param showResultNumber true if result numbers should be shown in the list
	 * @return current builder
	 */
	public SELF showResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
		return (SELF) this;
	}

	@Override
	protected void applyToFactory( EntityViewRegistry viewRegistry, EntityListViewFactory factory ) {
		super.applyToFactory( viewRegistry, factory );

		if ( pageFetcher != null ) {
			factory.setPageFetcher( pageFetcher );
		}
		if ( pageSize != null ) {
			factory.setPageSize( pageSize );
		}
		if ( sortableProperties != null ) {
			factory.setSortableProperties( sortableProperties );
		}
		if ( defaultSort != null ) {
			factory.setDefaultSort( defaultSort );
		}
		if ( showResultNumber != null ) {
			factory.setShowResultNumber( showResultNumber );
		}
	}
}
