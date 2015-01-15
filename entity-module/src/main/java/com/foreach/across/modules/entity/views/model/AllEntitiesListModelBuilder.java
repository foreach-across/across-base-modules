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
package com.foreach.across.modules.entity.views.model;

import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Arne Vandamme
 */
@Deprecated
public class AllEntitiesListModelBuilder implements ModelBuilder
{
	private final CrudRepository repository;

	public AllEntitiesListModelBuilder( CrudRepository repository ) {
		this.repository = repository;
	}

	@Override
	public void build( EntityConfiguration entityConfiguration, EntityView entityView ) {

		/*
		config
			.properties()
			.labels( "Username", "Groups" )
			.values( "username", "groups.size()" )
			.labels().property( "groups.size()", "Groups" )
			.property( "username", "Username")
			.property( "groups", "Groups", "groups.size()" )
			.order( "username", "hasGroups" )


		config.properties()
			.property( "calculated", "Calculated", new ValueFetcher<>() {
				Object getValue( Object entity ) {

				}
			} );
		 */

		/*entityView.addObject( "props", entityConfiguration.getPropertyRegistry().getProperties(
				EntityPropertyFilters.includeOrdered( "username", "email", "groups.size()" ) ) );
		*/
		entityView.addObject( "entities", repository.findAll() );
		entityView.addObject( "entityConfig", entityConfiguration );
	}
}
