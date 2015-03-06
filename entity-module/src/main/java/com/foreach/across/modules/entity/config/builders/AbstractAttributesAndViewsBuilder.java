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

import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.registry.ConfigurableEntityViewRegistry;
import com.foreach.across.modules.entity.registry.support.WritableAttributes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Base class for entity related builder supporting configuration of attributes and builder post processors.
 * Most methods should be overridden in child builders to provide type specificity.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 * @see com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder
 */
public abstract class AbstractAttributesAndViewsBuilder<T extends AbstractAttributesAndViewsBuilder, U>
{
	private final Map<Object, Object> attributes = new HashMap<>();
	private final Collection<PostProcessor<U>> postProcessors = new LinkedList<>();
	private final Map<String, AbstractEntityViewBuilder> viewBuilders = new HashMap<>();

	/**
	 * Add a custom attribute this builder should apply to the entity it processes.
	 *
	 * @param name  Name of the attribute.
	 * @param value Value of the attribute.
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public T attribute( String name, Object value ) {
		Assert.notNull( name );
		attributes.put( name, value );
		return (T) this;
	}

	/**
	 * Add a custom attribute this builder should apply to the entity it processes.
	 *
	 * @param type  Type of the attribute.
	 * @param value Value of the attribute.
	 * @param <S>   Class that is both key and value type of the attribute
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public <S> T attribute( Class<S> type, S value ) {
		Assert.notNull( type );
		attributes.put( type, value );
		return (T) this;
	}

	/**
	 * Add a post processor that will be applied to all configurations in the registry.
	 * The post processors will be applied after all other building operations are done.
	 *
	 * @param postProcessor Post processor instance to add.
	 */
	public void addPostProcessor( PostProcessor<U> postProcessor ) {
		postProcessors.add( postProcessor );
	}

	/**
	 * Returns a {@link AbstractSimpleEntityViewBuilder} instance for the view with the given name.
	 * If there is already another builder type for that view, an exception will be thrown.
	 *
	 * @param name Name of the view for which to retrieve a builder.
	 * @return builder instance
	 */
	public abstract AbstractSimpleEntityViewBuilder view( String name );

	/**
	 * Returns the default list view builder for the entity being configured.
	 * A default list view is usually available.
	 *
	 * @return builder instance
	 */
	public abstract AbstractEntityListViewBuilder listView();

	/**
	 * Returns a list view builder for the view with the given name.
	 *
	 * @param name Name of the view for which to retrieve a builder.
	 * @return builder instance
	 */
	public abstract AbstractEntityListViewBuilder listView( String name );

	/**
	 * Returns the default create form view builder for the entity being configured.
	 * A default create form view is usually available.
	 *
	 * @return builder instance
	 */
	public abstract AbstractEntityFormViewBuilder createFormView();

	/**
	 * Returns the default update form view builder for the entity being configured.
	 * A default update form view is usually available.
	 *
	 * @return builder instance
	 */
	public abstract AbstractEntityFormViewBuilder updateFormView();

	/**
	 * Returns a form view builder for the view with the given name.
	 *
	 * @param name Name of the view for which to retrieve a builder.
	 * @return builder instance
	 */
	public abstract AbstractEntityFormViewBuilder formView( String name );

	/**
	 * Returns a builder for the view with the specified name.  Any existing builder is assumed to be of the given
	 * type and a new instance of that type will be created if there is no builder yet.  Note that custom
	 * builder types *must* have a parameterless constructor.
	 *
	 * @param name         Name of the view for which to retrieve a builder.
	 * @param builderClass Type of the builder.
	 * @param <V>          Specific builder implementation.
	 * @return builder instance
	 */
	@SuppressWarnings("unchecked")
	public synchronized <V extends AbstractEntityViewBuilder<?, V>> V view( String name, Class<V> builderClass ) {
		V builder = (V) viewBuilders.get( name );

		if ( builder == null ) {
			builder = BeanUtils.instantiateClass( builderClass );
			builder.setName( name );
			builder.setParent( this );

			viewBuilders.put( name, builder );
		}

		return builder;
	}

	protected void applyViewBuilders( ConfigurableEntityViewRegistry viewRegistry,
	                                  AutowireCapableBeanFactory beanFactory ) {
		for ( AbstractEntityViewBuilder viewBuilder : viewBuilders.values() ) {
			viewBuilder.apply( viewRegistry, beanFactory );
		}
	}

	protected void applyAttributes( WritableAttributes writableAttributes ) {
		for ( Map.Entry<Object, Object> attribute : attributes.entrySet() ) {
			if ( attribute.getKey() instanceof String ) {
				writableAttributes.addAttribute( (String) attribute.getKey(), attribute.getValue() );
			}
			else {
				writableAttributes.addAttribute( (Class) attribute.getKey(), attribute.getValue() );
			}
		}
	}

	protected Collection<PostProcessor<U>> postProcessors() {
		return postProcessors;
	}
}
