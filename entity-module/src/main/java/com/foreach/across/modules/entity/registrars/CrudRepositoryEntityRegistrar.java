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
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;

import javax.persistence.EntityManager;
import java.util.*;

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

	private Collection<EntityManager> entityManagers = Collections.emptyList();

	private Map<Class, PersistentEntity> persistentEntities = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public void registerEntities( MutableEntityRegistry entityRegistry,
	                              AcrossModuleInfo moduleInfo,
	                              AcrossContextBeanRegistry beanRegistry ) {
		ApplicationContext applicationContext = moduleInfo.getApplicationContext();
		Map<String, CrudRepository> repositories = applicationContext.getBeansOfType( CrudRepository.class );
		Map<String, MappingContext> mappingContexts = applicationContext.getBeansOfType( MappingContext.class );

		for ( MappingContext mappingContext : mappingContexts.values() ) {
			for ( PersistentEntity persistentEntity : (Collection<PersistentEntity>) mappingContext
					.getPersistentEntities() ) {
				persistentEntities.put( persistentEntity.getType(), persistentEntity );
			}
		}

		// Update the entity managers as some might have been added
		entityManagers = beanRegistry.getBeansOfType( EntityManager.class, true );

		for ( final CrudRepository repository : repositories.values() ) {
			Class entityType = determineEntityType( repository );

			if ( entityRegistry.getEntityConfiguration( entityType ) != null ) {
				// already registered, skip it
				continue;
			}

			EntityConfiguration entityConfiguration = new EntityConfiguration( entityType );
			entityConfiguration.setPropertyRegistry( buildEntityPropertyRegistry( entityType ) );
			entityConfiguration.setEntityModel( buildEntityModel( entityType, repository ) );

			buildCrudListView( entityConfiguration, repository );

			/*
			//entityConfiguration.setAttribute( "EntityModule" );
						//entityConfiguration.setView( "crud-list", new View().setDataFetcher( fetcher ).layout() )
			 */
			entityRegistry.register( entityConfiguration );
		}
	}

	@SuppressWarnings("unchecked")
	private EntityModel buildEntityModel( Class<?> entityType, CrudRepository<?, ?> repository ) {
		return new EntityModelImpl(
				persistentEntities.get( entityType ),
				findEntityInformation( entityType ),
				repository
		);
	}

	private EntityInformation findEntityInformation( Class<?> entityType ) {
		for ( EntityManager entityManager : entityManagers ) {
			if ( entityManager.getMetamodel().managedType( entityType ) != null ) {
				return JpaEntityInformationSupport.getMetadata( entityType, entityManager );
			}
		}

		return null;
	}

	private EntityPropertyRegistry buildEntityPropertyRegistry( Class<?> entityType ) {
		MutableEntityPropertyRegistry registry =
				(MutableEntityPropertyRegistry) entityPropertyRegistries.getRegistry( entityType );

		Map<String, Integer> propertiesOrder = new HashMap<>();

		if ( registry.getDefaultFilter() == null ) {
			List<String> excludedProps = new LinkedList<>();
			excludedProps.add( "class" );
			if ( Persistable.class.isAssignableFrom( entityType ) ) {
				excludedProps.add( "new" );
				registry.getMutableProperty( "id" ).setHidden( true );
			}
			if ( SettableIdBasedEntity.class.isAssignableFrom( entityType ) ) {
				excludedProps.add( "newEntityId" );

				MutableEntityPropertyDescriptor mutable = registry.getMutableProperty( "newEntityId" );
				mutable.setReadable( false );
				mutable.setHidden( true );
			}
			registry.setDefaultFilter( EntityPropertyFilters.exclude( excludedProps ) );
		}

		if ( Auditable.class.isAssignableFrom( entityType ) ) {
			modifyDisplayName( registry.getProperty( "createdDate" ), "Created" );
			modifyDisplayName( registry.getProperty( "lastModifiedDate" ), "Last modified" );

			// Auditable properties are set automatically, should not be set through entity
			registry.getMutableProperty( "createdBy" ).setWritable( false );
			registry.getMutableProperty( "createdDate" ).setWritable( false );
			registry.getMutableProperty( "lastModifiedBy" ).setWritable( false );
			registry.getMutableProperty( "lastModifiedDate" ).setWritable( false );

			propertiesOrder.put( "createdDate", 1 );
			propertiesOrder.put( "createdBy", 2 );
			propertiesOrder.put( "lastModifiedDate", 3 );
			propertiesOrder.put( "lastModifiedBy", 4 );
		}

		registry.setDefaultOrder( new EntityPropertyOrder( propertiesOrder ) );

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
