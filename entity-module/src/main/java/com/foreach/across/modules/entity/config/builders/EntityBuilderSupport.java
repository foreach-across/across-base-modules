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
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Base class for entity related builder supporting configuration of attributes and builder post processors.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 */
public abstract class EntityBuilderSupport<T extends EntityBuilderSupport>
{
	private final Map<Object, Object> attributes = new HashMap<>();
	private final Collection<PostProcessor<MutableEntityConfiguration<?>>> postProcessors = new LinkedList<>();

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
	public void addPostProcessor( PostProcessor<MutableEntityConfiguration<?>> postProcessor ) {
		postProcessors.add( postProcessor );
	}

	/**
	 * Apply the builder configuration to the EntityRegistry.  This will not invoke the post processors,
	 * use {@link #postProcess(com.foreach.across.modules.entity.registry.MutableEntityRegistry)} to execute
	 * the post processors after the configuration has been applied.
	 *
	 * @param entityRegistry EntityRegistry to which the configuration should be applied.
	 */
	synchronized void apply( MutableEntityRegistry entityRegistry ) {
		for ( EntityConfiguration entityConfiguration : entitiesToConfigure( entityRegistry ) ) {
			MutableEntityConfiguration mutableEntityConfiguration
					= entityRegistry.getMutableEntityConfiguration( entityConfiguration.getEntityType() );

			applyAttributes( mutableEntityConfiguration );
		}
	}

	protected void applyAttributes( MutableEntityConfiguration entityConfiguration ) {
		for ( Map.Entry<Object, Object> attribute : attributes.entrySet() ) {
			if ( attribute.getKey() instanceof String ) {
				entityConfiguration.addAttribute( (String) attribute.getKey(), attribute.getValue() );
			}
			else {
				entityConfiguration.addAttribute( (Class) attribute.getKey(), attribute.getValue() );
			}
		}
	}

	/**
	 * Apply the post processors to the registry.  Post processors are applied one after the other.
	 * <p/>
	 * If a PostProcessor returns a different instance than the one passed in as parameter, the new instance
	 * will replace the new one in the registry even if it would defer in entity type.  If the post processor
	 * returns null, the entity configuration will be removed.
	 *
	 * @param entityRegistry EntityRegistry to which the post processors should be applied.
	 */
	synchronized void postProcess( MutableEntityRegistry entityRegistry ) {
		for ( PostProcessor<MutableEntityConfiguration<?>> postProcessor : postProcessors ) {
			for ( EntityConfiguration entityConfiguration : new ArrayList<>( entitiesToConfigure( entityRegistry ) ) ) {
				MutableEntityConfiguration mutableEntityConfiguration
						= entityRegistry.getMutableEntityConfiguration( entityConfiguration.getEntityType() );

				MutableEntityConfiguration modified = postProcessor.process( mutableEntityConfiguration );

				if ( modified != entityConfiguration ) {
					entityRegistry.remove( mutableEntityConfiguration.getEntityType() );

					if ( modified != null ) {
						entityRegistry.register( modified );
					}
				}
			}
		}
	}

	protected abstract Collection<EntityConfiguration> entitiesToConfigure( MutableEntityRegistry entityRegistry );
}
