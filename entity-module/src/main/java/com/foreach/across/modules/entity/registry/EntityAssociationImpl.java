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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.support.AttributeSupport;
import com.foreach.across.modules.entity.views.EntityViewFactory;

/**
 * @author Arne Vandamme
 */
public class EntityAssociationImpl<T>
		extends AttributeSupport
		implements MutableEntityAssociation<T>
{
	private final MutableEntityConfiguration entityConfiguration;
	private final MutableEntityConfiguration<T> associatedEntityConfiguration;

	public EntityAssociationImpl( MutableEntityConfiguration entityConfiguration,
	                              MutableEntityConfiguration<T> associatedEntityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
		this.associatedEntityConfiguration = associatedEntityConfiguration;
	}

	@Override
	public String getName() {
		return associatedEntityConfiguration.getName();
	}

	@Override
	public Class<T> getEntityType() {
		return associatedEntityConfiguration.getEntityType();
	}

	@Override
	public EntityConfiguration<T> getAssociatedEntityConfiguration() {
		return associatedEntityConfiguration;
	}

	@Override
	public boolean hasView( String name ) {
		return entityConfiguration.hasView( buildAssociatedViewName( name ) );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <Y extends EntityViewFactory> Y getViewFactory( String viewName ) {
		return (Y) entityConfiguration.getViewFactory( buildAssociatedViewName( viewName ) );
	}

	@Override
	public void registerView( String viewName, EntityViewFactory viewFactory ) {
		entityConfiguration.registerView( buildAssociatedViewName( viewName ), viewFactory );
	}

	private String buildAssociatedViewName( String viewName ) {
		return getName() + "_" + viewName;
	}
}
