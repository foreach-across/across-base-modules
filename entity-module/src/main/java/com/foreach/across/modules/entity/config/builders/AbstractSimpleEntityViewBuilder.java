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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MergingEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.SimpleEntityViewFactory;
import com.foreach.across.modules.entity.views.processors.ViewDataBinderProcessor;
import com.foreach.across.modules.entity.views.processors.ViewModelAndCommandProcessor;
import com.foreach.across.modules.entity.views.processors.ViewPostProcessor;
import com.foreach.across.modules.entity.views.processors.ViewPreProcessor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic view builder supporting both {@link com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport}
 * and {@link com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport}.
 *
 * @author Arne Vandamme
 */
public abstract class AbstractSimpleEntityViewBuilder<T extends ConfigurablePropertiesEntityViewFactorySupport, SELF extends AbstractSimpleEntityViewBuilder>
		extends AbstractEntityViewBuilder<T, SELF>
{
	public abstract class EntityViewPropertyRegistryBuilder<MYSELF extends EntityViewPropertyRegistryBuilder>
			extends AbstractEntityPropertyRegistryBuilder<MYSELF>
	{
		@SuppressWarnings( "unchecked" )
		public MYSELF filter( String... propertyNames ) {
			and().viewPropertyFilter = EntityPropertyFilters.includeOrdered( propertyNames );
			return (MYSELF) this;
		}

		@Override
		public SELF and() {
			return viewBuilder;
		}
	}

	private final SELF viewBuilder;

	private String template;
	private EntityViewPropertyRegistryBuilder propertyRegistryBuilder;
	private Collection<ViewPreProcessor> preProcessors = new ArrayList<>();
	private Collection<ViewPostProcessor> postProcessors = new ArrayList<>();
	private Collection<ViewDataBinderProcessor> dataBinderProcessors = new ArrayList<>();
	private Collection<ViewModelAndCommandProcessor> modelAndCommandProcessors = new ArrayList<>();

	protected EntityPropertyFilter viewPropertyFilter;

	@SuppressWarnings( "unchecked" )
	public AbstractSimpleEntityViewBuilder() {
		this.viewBuilder = (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF template( String template ) {
		this.template = template;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public EntityViewPropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = createPropertiesBuilder();
		}

		return propertyRegistryBuilder;
	}

	public EntityViewPropertyRegistryBuilder properties( String... propertyNames ) {
		return properties().filter( propertyNames );
	}

	protected abstract EntityViewPropertyRegistryBuilder createPropertiesBuilder();

	/**
	 * Add a processor object that should be applied to the view factory.  The processor should
	 * implement at least one of the processor interfaces and will be registered on the view for
	 * each functional interface type that it implements.
	 * <p/>
	 * Processors added through this method will be registered after processors added using the
	 * specifically typed methods.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.ViewProcessorAdapter
	 * @see com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter
	 */
	@SuppressWarnings("unchecked")
	public SELF addProcessor( Object processor ) {
		Assert.notNull( processor );
		Assert.isTrue(
				processor instanceof ViewPreProcessor
						|| processor instanceof ViewPostProcessor
						|| processor instanceof ViewDataBinderProcessor
						|| processor instanceof ViewModelAndCommandProcessor
		);
		if ( processor instanceof ViewPreProcessor ) {
			preProcessors.add( (ViewPreProcessor) processor );
		}
		if ( processor instanceof ViewPostProcessor ) {
			postProcessors.add( (ViewPostProcessor) processor );
		}
		if ( processor instanceof ViewDataBinderProcessor ) {
			dataBinderProcessors.add( (ViewDataBinderProcessor) processor );
		}
		if ( processor instanceof ViewModelAndCommandProcessor ) {
			modelAndCommandProcessors.add( (ViewModelAndCommandProcessor) processor );
		}

		return (SELF) this;
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

	/**
	 * Add a {@link com.foreach.across.modules.entity.views.processors.ViewModelAndCommandProcessor} that
	 * should be called when preparing the model and command for the view.
	 *
	 * @param modelAndCommandProcessor instance - should not be null
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public SELF addModelAndCommandProcessor( ViewModelAndCommandProcessor modelAndCommandProcessor ) {
		Assert.notNull( modelAndCommandProcessor );
		this.modelAndCommandProcessors.add( modelAndCommandProcessor );
		return (SELF) this;
	}

	/**
	 * Add a {@link com.foreach.across.modules.entity.views.processors.ViewDataBinderProcessor} that
	 * should be called when preparing the databinder for the view.
	 *
	 * @param dataBinderProcessor instance - should not be null
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public SELF addDataBinderProcessor( ViewDataBinderProcessor dataBinderProcessor ) {
		Assert.notNull( dataBinderProcessor );
		this.dataBinderProcessors.add( dataBinderProcessor );
		return (SELF) this;
	}

	/**
	 * @return parent builder
	 */
	public abstract Object and();

	@SuppressWarnings("unchecked")
	@Override
	protected T createFactoryInstance() {
		return (T) new SimpleEntityViewFactory();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void applyToFactory( EntityViewRegistry viewRegistry,
	                               T factory ) {
		if ( template != null ) {
			factory.setTemplate( template );
		}

		if ( !preProcessors.isEmpty() ) {
			factory.setPreProcessors( merge( factory.getPreProcessors(), preProcessors ) );
		}

		if ( !postProcessors.isEmpty() ) {
			factory.setPostProcessors( merge( factory.getPostProcessors(), postProcessors ) );
		}

		if ( !dataBinderProcessors.isEmpty() ) {
			factory.setDataBinderProcessors( merge( factory.getDataBinderProcessors(), dataBinderProcessors ) );
		}

		if ( !modelAndCommandProcessors.isEmpty() ) {
			factory.setModelAndCommandProcessors(
					merge( factory.getModelAndCommandProcessors(), modelAndCommandProcessors )
			);
		}

		EntityPropertyRegistry registry = factory.getPropertyRegistry();

		if ( registry == null ) {
			if ( viewRegistry instanceof EntityConfiguration ) {
				registry = new MergingEntityPropertyRegistry(
						( (EntityConfiguration) viewRegistry ).getPropertyRegistry() );
			}
			else if ( viewRegistry instanceof EntityAssociation ) {
				registry = new MergingEntityPropertyRegistry(
						( (EntityAssociation) viewRegistry ).getTargetEntityConfiguration().getPropertyRegistry() );
			}

			factory.setPropertyRegistry( registry );
		}

		if ( propertyRegistryBuilder != null ) {
			propertyRegistryBuilder.apply( registry );
		}

		if ( viewPropertyFilter != null ) {
			factory.setPropertyFilter( viewPropertyFilter );
		}
	}

	private <V> Collection<V> merge( Collection<V> original, Collection<V> additional ) {
		List<V> total = new ArrayList<>( original.size() + additional.size() );
		total.addAll( original );
		total.addAll( additional );

		return total;
	}
}
