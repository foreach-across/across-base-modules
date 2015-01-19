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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;

import java.util.Comparator;

/**
 * EntityViewFactory that allows common properties to be configured.
 * The use of the different properties is still implementation dependant, but implementations
 * are expected to support all properties or throw an exception when trying to set them.
 *
 * @author Arne Vandamme
 */
public abstract class CommonEntityViewFactory implements EntityViewFactory
{
	private EntityPropertyRegistry propertyRegistry;
	private String template;

	private EntityPropertyFilter propertyFilter;
	private Comparator<EntityPropertyDescriptor> propertyComparator;

	public EntityPropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate( String template ) {
		this.template = template;
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
}
