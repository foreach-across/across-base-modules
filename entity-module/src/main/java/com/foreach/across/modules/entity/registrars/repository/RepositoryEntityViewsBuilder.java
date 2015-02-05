package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private BeanFactory beanFactory;

	public void buildViews( MutableEntityConfiguration entityConfiguration ) {
		buildCreateView( entityConfiguration );
		buildUpdateView( entityConfiguration );

		// todo: support regular repository to be used instead of specific CrudRepository interface (use repo information)
		buildListView( entityConfiguration, (CrudRepository) entityConfiguration.getAttribute( Repository.class ) );
	}

	private void buildCreateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory viewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.CREATE_VIEW_NAME, "entityViews" );

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityFormView.VIEW_TEMPLATE );

		if ( Auditable.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
			LinkedList<String> excludedProperties = new LinkedList<>();
			excludedProperties.add( "createdDate" );
			excludedProperties.add( "createdBy" );
			excludedProperties.add( "lastModifiedDate" );
			excludedProperties.add( "lastModifiedBy" );

			EntityPropertyFilter existingFilter = registry.getDefaultFilter();

			if ( existingFilter instanceof EntityPropertyFilter.Exclusive ) {
				excludedProperties.addAll( existingFilter.getPropertyNames() );
			}

			registry.setDefaultFilter( EntityPropertyFilters.exclude( excludedProperties ) );
		}

		entityConfiguration.registerView( EntityFormView.CREATE_VIEW_NAME, viewFactory );
	}

	private void buildUpdateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory viewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.UPDATE_VIEW_NAME, "entityViews" );

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityFormView.VIEW_TEMPLATE );

		entityConfiguration.registerView( EntityFormView.UPDATE_VIEW_NAME, viewFactory );
	}

	private void buildListView( MutableEntityConfiguration entityConfiguration, CrudRepository repository ) {
		EntityListViewFactory viewFactory = beanFactory.getBean( EntityListViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews.listView", "entityViews" );

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityListView.VIEW_TEMPLATE );
		viewFactory.setPageFetcher( new RepositoryEntityListViewPageFetcher( repository ) );

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
