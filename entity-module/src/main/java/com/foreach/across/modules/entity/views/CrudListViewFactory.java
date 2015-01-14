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

import com.foreach.across.modules.entity.business.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.business.EntityPropertyFilter;
import com.foreach.across.modules.entity.business.EntityPropertyFilters;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import org.springframework.ui.Model;

import java.util.List;

/**
 * @author Arne Vandamme
 */
public class CrudListViewFactory extends CommonEntityViewFactory
{
	@Override
	public EntityView create( EntityConfiguration entityConfiguration, Model model ) {
		// fetch entities
		// get the properties to apply

		EntityView view = new EntityView();
		view.setViewName( getTemplate() );
		view.addModel( model );

		getModelBuilder().build( entityConfiguration, view );

		view.addObject( "props", getProperties() );

		return view;
	}

	private List<EntityPropertyDescriptor> getProperties() {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NoOp;

		if ( getPropertyComparator() != null ) {
			return getPropertyRegistry().getProperties( filter, getPropertyComparator() );
		}

		return getPropertyRegistry().getProperties( filter );
	}
}
