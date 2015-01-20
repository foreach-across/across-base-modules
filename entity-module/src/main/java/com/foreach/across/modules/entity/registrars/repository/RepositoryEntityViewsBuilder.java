package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MergingEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.RepositoryListViewPageFetcher;
import com.foreach.across.modules.entity.views.helpers.SpelValueFetcher;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Attempts to create default views for an EntityConfiguration.
 * Creates a list, read, create, update and delete view if possible.
 */
public class RepositoryEntityViewsBuilder
{
	public void createViews( MutableEntityConfiguration entityConfiguration ) {
		// Get the repository
		buildListView( entityConfiguration, (CrudRepository) entityConfiguration.getAttribute( Repository.class ) );
	}

	private void buildListView( MutableEntityConfiguration entityConfiguration, CrudRepository repository ) {
		EntityListViewFactory viewFactory = new EntityListViewFactory();

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityListView.VIEW_TEMPLATE );
		viewFactory.setPageFetcher( new RepositoryListViewPageFetcher( repository ) );

		LinkedList<String> defaultProperties = new LinkedList<>();
		if ( registry.contains( "name" ) ) {
			defaultProperties.add( "name" );
		}
		if ( registry.contains( "title" ) ) {
			defaultProperties.add( "title" );
		}

		if ( defaultProperties.isEmpty() ) {
			if ( !registry.contains( "#generatedLabel" ) ) {
				SimpleEntityPropertyDescriptor label = new SimpleEntityPropertyDescriptor();
				label.setName( "#generatedLabel" );
				label.setDisplayName( "Generated label" );
				label.setValueFetcher( new SpelValueFetcher( "toString()" ) );

				registry.register( label );
			}

			defaultProperties.add( "#generatedLabel" );
		}

		if ( SecurityPrincipal.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
			defaultProperties.addFirst( "principalName" );
		}

		if ( Auditable.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
			defaultProperties.add( "createdDate" );
			defaultProperties.add( "createdBy" );
			defaultProperties.add( "lastModifiedDate" );
			defaultProperties.add( "lastModifiedBy" );
		}

		viewFactory.setPropertyFilter( EntityPropertyFilters.includeOrdered( defaultProperties ) );
		viewFactory.setDefaultSort( determineDefaultSort( defaultProperties ) );

		entityConfiguration.registerView( EntityListView.VIEW_NAME, viewFactory );
	}

	private Sort determineDefaultSort( Collection<String> defaultProperties ) {
		String propertyName = null;

		if ( defaultProperties.contains( "name" ) ) {
			propertyName = "name";
		}
		else if ( defaultProperties.contains( "title" ) ) {
			propertyName = "title";
		}

		if ( propertyName != null ) {
			return new Sort( propertyName );
		}

		if ( defaultProperties.contains( "createdDate" ) ) {
			return new Sort( Sort.Direction.DESC, "createdDate" );
		}

		return null;
	}
}
