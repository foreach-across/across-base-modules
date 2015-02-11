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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class RepositoryEntityListViewPageFetcher implements EntityListViewPageFetcher<ViewCreationContext>
{
	private CrudRepository repository;

	public RepositoryEntityListViewPageFetcher( CrudRepository repository ) {
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page fetchPage( ViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
		if ( repository instanceof PagingAndSortingRepository ) {
			return ( (PagingAndSortingRepository) repository ).findAll( pageable );
		}

		Iterable items = repository.findAll();
		return new PageImpl<>( convertToList( items ) );
	}

	private <T> List<T> convertToList( Iterable<T> items ) {
		if ( items instanceof List ) {
			return (List<T>) items;
		}

		if ( items instanceof Collection ) {
			return new ArrayList<>( (Collection<T>) items );
		}

		List<T> list = new ArrayList<>();
		for ( T item : items ) {
			list.add( item );
		}

		return list;
	}
}
