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
package com.foreach.across.modules.entity.registrars;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.entity.business.*;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.views.CrudListViewFactory;
import com.foreach.across.modules.entity.views.RepositoryListViewPageFetcher;
import com.foreach.across.modules.entity.views.helpers.SpelValueFetcher;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Scans for {@link org.springframework.data.repository.CrudRepository} implementations
 * and creates a default EntityConfiguration for them.
 *
 * @author Arne Vandamme
 */
public class CrudRepositoryEntityRegistrar implements EntityRegistrar
{
	@Autowired
	private EntityPropertyRegistries entityPropertyRegistries;

	@Override
	public void registerEntities( MutableEntityRegistry entityRegistry,
	                              AcrossModuleInfo moduleInfo,
	                              AcrossContextBeanRegistry beanRegistry ) {
		Map<String, CrudRepository> repositories = moduleInfo.getApplicationContext()
		                                                     .getBeansOfType( CrudRepository.class );

		for ( CrudRepository repository : repositories.values() ) {
			Class entityType = determineEntityType( repository );

			if ( entityRegistry.getEntityConfiguration( entityType ) != null ) {
				// already registered, skip it
				continue;
			}

			EntityConfiguration entityConfiguration = new EntityConfiguration( entityType );
			entityConfiguration.setPropertyRegistry( buildEntityPropertyRegistry( entityType ) );

			buildCrudListView( entityConfiguration, repository );

			/*
			//entityConfiguration.setAttribute( "EntityModule" );
						//entityConfiguration.setView( "crud-list", new View().setDataFetcher( fetcher ).layout() )
			 */
			entityRegistry.register( entityConfiguration );
		}
	}

	private EntityPropertyRegistry buildEntityPropertyRegistry( Class<?> entityType ) {
		EntityPropertyRegistry registry = entityPropertyRegistries.getRegistry( entityType );

		if ( registry.getDefaultFilter() == null ) {
			List<String> excludedProps = new LinkedList<>();
			excludedProps.add( "class" );
			if ( Persistable.class.isAssignableFrom( entityType ) ) {
				excludedProps.add( "new" );
			}
			if ( SettableIdBasedEntity.class.isAssignableFrom( entityType ) ) {
				excludedProps.add( "newEntityId" );
			}
			registry.setDefaultFilter( EntityPropertyFilters.exclude( excludedProps ) );
		}

		if ( Auditable.class.isAssignableFrom( entityType ) ) {
			modifyDisplayName( registry.getProperty( "createdDate" ), "Created" );
			modifyDisplayName( registry.getProperty( "lastModifiedDate" ), "Last modified" );
		}

		return registry;
	}

	private void modifyDisplayName( EntityPropertyDescriptor descriptor, String displayName ) {
		if ( descriptor instanceof SimpleEntityPropertyDescriptor ) {
			( (SimpleEntityPropertyDescriptor) descriptor ).setDisplayName( displayName );
		}
	}

	private void buildCrudListView( EntityConfiguration entityConfiguration, CrudRepository repository ) {
		CrudListViewFactory viewFactory = new CrudListViewFactory();
		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);
		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( "th/entity/list" );
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

					/*if ( registry.contains( "groups" ) ) {
						SimpleEntityPropertyDescriptor calculated = new SimpleEntityPropertyDescriptor();
						calculated.setName( "groups.size()" );
						calculated.setValueFetcher( new SpelValueFetcher( "groups.size()" ) );
						reg.register( calculated );
					}*/

		entityConfiguration.registerView( "crud-list", viewFactory );
	}

	private Class determineEntityType( CrudRepository repository ) {
		return TypeDescriptor.forObject( repository )
		                     .upcast( CrudRepository.class )
		                     .getResolvableType().getGeneric( 0 ).resolve();
	}
}
