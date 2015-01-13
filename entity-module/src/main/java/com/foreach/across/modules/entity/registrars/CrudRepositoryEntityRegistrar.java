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
import com.foreach.across.modules.entity.views.helpers.SpelValueFetcher;
import com.foreach.across.modules.entity.views.model.AllEntitiesListModelBuilder;
import com.foreach.across.modules.entity.views.model.ModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

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

			EntityPropertyRegistry registry = entityPropertyRegistries.getRegistry( entityType );
			EntityConfiguration entityConfiguration = new EntityConfiguration( entityType );

			CrudListViewFactory viewFactory = new CrudListViewFactory();

			EntityPropertyRegistry reg = new MergingEntityPropertyRegistry( registry );

			if ( registry.contains( "groups" ) ) {
				SimpleEntityPropertyDescriptor calculated = new SimpleEntityPropertyDescriptor();
				calculated.setName( "groups.size()" );
				calculated.setValueFetcher( new SpelValueFetcher( "groups.size()" ) );
				reg.register( calculated );
			}

			viewFactory.setPropertyRegistry( reg );
			viewFactory.setTemplate( "th/entity/list" );
			viewFactory.setModelBuilder( determineListModelBuilder( repository ) );

			entityConfiguration.registerView( "crud-list", viewFactory );

			/*
			//entityConfiguration.setAttribute( "EntityModule" );
						//entityConfiguration.setView( "crud-list", new View().setDataFetcher( fetcher ).layout() )
			 */
			entityRegistry.register( entityConfiguration );
		}
	}

	private ModelBuilder determineListModelBuilder( CrudRepository repository ) {
		if ( repository instanceof PagingAndSortingRepository ) {
			// PagingAndSortingRepository model builder
		}

		return new AllEntitiesListModelBuilder( repository );
	}

	private Class determineEntityType( CrudRepository repository ) {
		return TypeDescriptor.forObject( repository )
		                     .upcast( CrudRepository.class )
		                     .getResolvableType().getGeneric( 0 ).resolve();
	}
}
