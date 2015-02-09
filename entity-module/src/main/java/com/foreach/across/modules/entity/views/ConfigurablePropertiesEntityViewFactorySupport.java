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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.services.EntityFormService;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ViewElement;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderContext;
import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import com.foreach.across.modules.entity.views.elements.ViewElements;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

/**
 * Base support class for entity view factories that supports the configuration of the properties
 * to display for a a given entity type.
 *
 * @author Arne Vandamme
 */
public abstract class ConfigurablePropertiesEntityViewFactorySupport<T extends EntityView>
		extends SimpleEntityViewFactorySupport<T>
{
	private EntityPropertyRegistries entityPropertyRegistries;

	@Autowired
	private EntityFormService entityFormService;

	private EntityPropertyRegistry propertyRegistry;
	private EntityPropertyFilter propertyFilter;
	private Comparator<EntityPropertyDescriptor> propertyComparator;

	public EntityPropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	public EntityPropertyFilter getPropertyFilter() {
		return propertyFilter;
	}

	public void setPropertyFilter( EntityPropertyFilter propertyFilter ) {
		this.propertyFilter = propertyFilter;
	}

	public Comparator<EntityPropertyDescriptor> getPropertyComparator() {
		return propertyComparator;
	}

	public void setPropertyComparator( Comparator<EntityPropertyDescriptor> propertyComparator ) {
		this.propertyComparator = propertyComparator;
	}

	@Autowired
	public void setEntityPropertyRegistries( EntityPropertyRegistries entityPropertyRegistries ) {
		this.entityPropertyRegistries = entityPropertyRegistries;
	}

	@Override
	protected void buildViewModel( EntityConfiguration entityConfiguration,
	                               EntityMessageCodeResolver messageCodeResolver,
	                               T view ) {
		view.setEntityProperties( getEntityProperties( entityConfiguration, messageCodeResolver ) );

		extendViewModel( entityConfiguration, view );
	}

	private ViewElements getEntityProperties( EntityConfiguration entityConfiguration,
	                                          EntityMessageCodeResolver messageCodeResolver ) {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NoOp;

		EntityPropertyRegistry registry = getPropertyRegistry();

		if ( registry == null ) {
			registry = entityConfiguration.getPropertyRegistry();
		}

		ViewElementBuilderContext builderContext
				= entityFormService.createBuilderContext( entityConfiguration, registry, messageCodeResolver,
				                                          getMode() );

		List<EntityPropertyDescriptor> descriptors;

		if ( getPropertyComparator() != null ) {
			descriptors = registry.getProperties( filter, getPropertyComparator() );
		}
		else {
			descriptors = registry.getProperties( filter );
		}

		ViewElements propertyViews = new ViewElements();

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			ViewElement propertyView = createPropertyView( builderContext, descriptor );

			if ( propertyView != null ) {
				propertyViews.add( propertyView );
			}
		}

		return propertyViews;
	}

	protected ViewElement createPropertyView( ViewElementBuilderContext builderContext,
	                                          EntityPropertyDescriptor descriptor ) {
		return builderContext.getViewElement( descriptor );
	}

	protected abstract ViewElementMode getMode();

	protected abstract void extendViewModel( EntityConfiguration entityConfiguration, T view );
}
