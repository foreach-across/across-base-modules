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
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.ViewPostProcessor;
import com.foreach.across.modules.entity.views.processors.ViewPreProcessor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class SimpleEntityViewBuilder<T extends ConfigurablePropertiesEntityViewFactorySupport, SELF extends SimpleEntityViewBuilder<T, SELF>>
		extends EntityViewBuilder<T, SELF>
{
	public class EntityViewPropertyRegistryBuilder extends EntityPropertyRegistryBuilderSupport<SELF, EntityViewPropertyRegistryBuilder>
	{
		EntityViewPropertyRegistryBuilder( SELF parent ) {
			super( parent );
		}

		public EntityViewPropertyRegistryBuilder filter( String... propertyNames ) {
			and().viewPropertyFilter = EntityPropertyFilters.includeOrdered( propertyNames );
			return this;
		}

		@Override
		public SELF and() {
			return super.and();
		}
	}

	public static class StandardEntityViewBuilder extends SimpleEntityViewBuilder<ConfigurablePropertiesEntityViewFactorySupport, StandardEntityViewBuilder>
	{

	}

	private String template;
	private EntityViewPropertyRegistryBuilder propertyRegistryBuilder;
	private Collection<ViewPreProcessor> preProcessors = new ArrayList<>();
	private Collection<ViewPostProcessor> postProcessors = new ArrayList<>();

	protected EntityPropertyFilter viewPropertyFilter;

	@SuppressWarnings("unchecked")
	public SELF template( String template ) {
		this.template = template;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public EntityViewPropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = new EntityViewPropertyRegistryBuilder( (SELF) this );
		}

		return propertyRegistryBuilder;
	}

	public EntityViewPropertyRegistryBuilder properties( String... propertyNames ) {
		return properties().filter( propertyNames );
	}

	/**
	 * Add a {@link com.foreach.across.modules.entity.views.processors.ViewPreProcessor} that
	 * should be called before generating the view.
	 *
	 * @param preProcessor instance - should not be null
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public SELF addPreProcessor( ViewPreProcessor preProcessor ) {
		Assert.notNull( preProcessor );
		this.preProcessors.add( preProcessor );
		return (SELF) this;
	}

	/**
	 * Add a {@link com.foreach.across.modules.entity.views.processors.ViewPostProcessor} that
	 * should be called after generating the view.
	 *
	 * @param postProcessor instance - should not be null
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public SELF addPostProcessor( ViewPostProcessor postProcessor ) {
		Assert.notNull( postProcessor );
		this.postProcessors.add( postProcessor );
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T createFactoryInstance() {
		return (T) new ConfigurablePropertiesEntityViewFactorySupport()
		{
			@Override
			protected ViewElementMode getMode() {
				return ViewElementMode.FOR_READING;
			}

			@Override
			protected void extendViewModel( ViewCreationContext viewCreationContext, EntityView view ) {
			}

			@Override
			protected EntityView createEntityView() {
				return new EntityView();
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void applyToFactory( EntityConfiguration configuration,
	                               T factory ) {
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

		if ( !preProcessors.isEmpty() ) {
			factory.setPreProcessors( merge( factory.getPreProcessors(), preProcessors ) );
		}

		if ( !postProcessors.isEmpty() ) {
			factory.setPostProcessors( merge( factory.getPostProcessors(), postProcessors ) );
		}
	}

	private <V> Collection<V> merge( Collection<V> original, Collection<V> additional ) {
		List<V> total = new ArrayList<>( original.size() + additional.size() );
		total.addAll( original );
		total.addAll( additional );

		return total;
	}
}
