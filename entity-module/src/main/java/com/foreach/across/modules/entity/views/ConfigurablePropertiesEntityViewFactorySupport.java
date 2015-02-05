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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.services.EntityFormService;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ViewElement;
import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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

	/*
	* @deprecated temporary solution until we have the concept of grouped attributes
	*/
	@Deprecated
	private List<ViewElement> getEntityProperties( Class<?> entityType,
	                                               String prefix,
	                                               EntityMessageCodeResolver messageCodeResolver,
	                                               EntityConfiguration entityConfiguration ) {
		List<ViewElement> propertyViews = new ArrayList<>();
		EntityPropertyRegistry registry = entityPropertyRegistries.getRegistry( entityType );
		for ( EntityPropertyDescriptor descriptor : registry.getProperties() ) {
			EntityPropertyDescriptor parentPropertyDescriptor = getPropertyRegistry().getProperty(
					prefix + "." + descriptor.getName() );
			ViewElement propertyView = createPropertyView( entityConfiguration, parentPropertyDescriptor,
			                                               messageCodeResolver );
			if ( propertyView != null ) {
				propertyViews.add( propertyView );
			}
		}
		return propertyViews;
	}

	private List<ViewElement> getEntityProperties( EntityConfiguration entityConfiguration,
	                                               EntityMessageCodeResolver messageCodeResolver ) {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NoOp;

		List<EntityPropertyDescriptor> descriptors;

		if ( getPropertyComparator() != null ) {
			descriptors = getPropertyRegistry().getProperties( filter, getPropertyComparator() );
		}
		else {
			descriptors = getPropertyRegistry().getProperties( filter );
		}

		List<ViewElement> propertyViews = new ArrayList<>( descriptors.size() );

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			PropertyPersistenceMetadata propertyPersistenceMetadata = descriptor.getAttribute(
					EntityAttributes.PROPERTY_PERSISTENCE_METADATA, PropertyPersistenceMetadata.class );
			if ( propertyPersistenceMetadata != null && propertyPersistenceMetadata.isEmbedded() ) {
				// TODO remove this temporary solution
				propertyViews.addAll( getEntityProperties( descriptor.getPropertyType(), descriptor.getName(),
				                                           messageCodeResolver, entityConfiguration ) );
			}
			else {
				ViewElement propertyView = createPropertyView( entityConfiguration, descriptor,
				                                               messageCodeResolver );
				if ( propertyView != null ) {
					propertyViews.add( propertyView );
				}
			}
		}

		return propertyViews;
	}

	protected ViewElement createPropertyView(
			EntityConfiguration entityConfiguration,
			EntityPropertyDescriptor descriptor,
			EntityMessageCodeResolver messageCodeResolver ) {
		return entityFormService.createViewElement( entityConfiguration, descriptor, messageCodeResolver,
		                                            getMode() );
	}

	protected abstract ViewElementMode getMode();

	protected abstract void extendViewModel( EntityConfiguration entityConfiguration, T view );
}
