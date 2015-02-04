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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MergingEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityView;

/**
* @author Arne Vandamme
*/
public class CommonEntityViewBuilder extends EntityViewBuilder<ConfigurablePropertiesEntityViewFactorySupport>
{
	public static class EntityViewPropertyRegistryBuilder
			extends EntityPropertyRegistryBuilderSupport<CommonEntityViewBuilder>
	{
		EntityViewPropertyRegistryBuilder( CommonEntityViewBuilder parent ) {
			super( parent );
		}

		public EntityViewPropertyRegistryBuilder filter( String... propertyNames ) {
			and().viewPropertyFilter = EntityPropertyFilters.includeOrdered( propertyNames );
			return this;
		}
	}

	private String template;
	private EntityViewPropertyRegistryBuilder propertyRegistryBuilder;
	private EntityPropertyFilter viewPropertyFilter;

	public CommonEntityViewBuilder template( String template ) {
		this.template = template;
		return this;
	}

	public EntityViewPropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = new EntityViewPropertyRegistryBuilder( this );
		}

		return propertyRegistryBuilder;
	}

	public EntityViewPropertyRegistryBuilder properties( String... propertyNames ) {
		return properties().filter( propertyNames );
	}

	@Override
	protected ConfigurablePropertiesEntityViewFactorySupport createFactoryInstance() {
		return new ConfigurablePropertiesEntityViewFactorySupport()
		{
			@Override
			protected void extendViewModel( EntityConfiguration entityConfiguration, EntityView view ) {
			}

			@Override
			protected EntityView createEntityView() {
				return new EntityView();
			}
		};
	}

	@Override
	protected void applyToFactory( EntityConfiguration configuration,
	                               ConfigurablePropertiesEntityViewFactorySupport factory ) {
		if ( template != null ) {
			factory.setTemplate( template );
		}

		EntityPropertyRegistry registry = factory.getPropertyRegistry();

		if ( registry == null ) {
			registry = new MergingEntityPropertyRegistry( configuration.getPropertyRegistry() );
			factory.setPropertyRegistry( registry );
		}

		if ( propertyRegistryBuilder != null ) {
			propertyRegistryBuilder.apply( registry );
		}

		if ( viewPropertyFilter != null ) {
			factory.setPropertyFilter( viewPropertyFilter );
		}
	}
}
