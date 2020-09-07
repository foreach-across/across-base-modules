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

package com.foreach.across.modules.hibernate.util;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.BeanCreationStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Used as a proxy {@link BeanCreationStrategy} which supports registering custom {@link BeanCreationStrategy}s for mapping DTOs.
 *
 * @author Steven Gentens
 * @see DozerConfiguration
 * @since 4.0.1
 */
@ConditionalOnClass(DozerBeanMapper.class)
public class DozerBeanCreationStrategyRegistry
{
	private final Map<String, BeanCreationStrategy> strategies = new ConcurrentHashMap<>();

	BeanCreationStrategy getBeanCreationStrategy() {
		Function<BeanCreationDirective, Optional<BeanCreationStrategy>> strategyResolver =
				( directive ) -> strategies.values().stream()
				                           .filter( bcs -> bcs.isApplicable( directive ) )
				                           .findFirst();
		return new BeanCreationStrategy()
		{
			// threadlocal? -> remove after create?
			@Override
			public boolean isApplicable( BeanCreationDirective directive ) {
				return strategyResolver.apply( directive ).isPresent();
			}

			@Override
			public Object create( BeanCreationDirective directive ) {
				BeanCreationStrategy beanCreationStrategy = strategyResolver.apply( directive )
				                                                            .get();
				return beanCreationStrategy.create( directive );
			}
		};
	}

	/**
	 * Registers a {@link BeanCreationStrategy} under a given name.
	 *
	 * @param name     to register the strategy under
	 * @param strategy to register
	 */
	public void add( String name, BeanCreationStrategy strategy ) {
		strategies.put( name, strategy );
	}

	/**
	 * Removes a {@link BeanCreationStrategy} under a given name.
	 *
	 * @param name of the strategy to remove
	 */
	public void remove( String name ) {
		strategies.remove( name );
	}

	// BeanCreationStrategyHolder -> name, strategy, order
	// ArrayList, sorted on order after each add
	// synchronized access in add, remove and resolving -> synchronized (strategies) { do shizzle }

}
