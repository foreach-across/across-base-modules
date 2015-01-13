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

import com.foreach.across.modules.entity.business.EntityPropertyFilters;
import com.foreach.across.modules.entity.business.EntityPropertyRegistry;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.entity.views.model.ModelBuilder;
import org.springframework.ui.Model;

/**
 * @author Arne Vandamme
 */
public class CrudListViewFactory implements EntityViewFactory
{
	private EntityPropertyRegistry propertyRegistry;
	private String viewName;
	private ModelBuilder modelBuilder;

	public void setTemplate( String viewName ) {
		this.viewName = viewName;
	}

	public void setModelBuilder( ModelBuilder modelBuilder ) {
		this.modelBuilder = modelBuilder;
	}

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	@Override
	public EntityView create( EntityConfiguration entityConfiguration, Model model ) {
		// fetch entities
		// get the properties to apply

		EntityView view = new EntityView();
		view.setViewName( viewName );
		view.addModel( model );

		modelBuilder.build( entityConfiguration, view );

		view.addObject( "props", propertyRegistry.getProperties(
				                EntityPropertyFilters.includeOrdered( "username", "email", "groups.size()" ) )
		);

		return view;
	}
}
