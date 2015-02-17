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
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.views.EntityViewFactory;

/**
 * @author Arne Vandamme
 */
public abstract class EntityViewBuilder<T extends EntityViewFactory, SELF extends EntityViewBuilder<T, SELF>>
{
	private String name;
	private EntityConfigurationBuilder parent;

	private T factory;

	protected void setName( String name ) {
		this.name = name;
	}

	protected void setParent( EntityConfigurationBuilder parent ) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public SELF factory( T entityViewFactory ) {
		this.factory = entityViewFactory;
		return (SELF) this;
	}

	public EntityConfigurationBuilder and() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	protected void apply( MutableEntityConfiguration configuration ) {
		T configuredFactory = (T) configuration.getViewFactory( name );

		if ( configuredFactory == null ) {
			configuredFactory = factory != null ? factory : createFactoryInstance();
			configuration.registerView( name, configuredFactory );
		}

		applyToFactory( configuration, configuredFactory );
	}

	protected abstract T createFactoryInstance();

	protected abstract void applyToFactory( EntityConfiguration configuration, T factory );
}
