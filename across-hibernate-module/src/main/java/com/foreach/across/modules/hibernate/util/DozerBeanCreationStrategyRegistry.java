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
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.BeanCreationStrategy;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Configures a proxy {@link BeanCreationStrategy} to support customizing the Dozer {@link Mapper}.
 * If multiple {@link BeanCreationStrategy}s match, the one with the lowest order matches.
 * If no order is specified during registration, the {@link #DEFAULT_ORDER} is applied.
 *
 * @author Steven Gentens
 * @see DozerConfiguration
 * @since 4.0.1
 */
@ConditionalOnClass(DozerBeanMapper.class)
public class DozerBeanCreationStrategyRegistry
{
	public static final int DEFAULT_ORDER = 1000;

	private final List<BeanCreationStrategyHolder> strategies = new ArrayList<>();

	BeanCreationStrategy getBeanCreationStrategy() {
		Function<BeanCreationDirective, Optional<BeanCreationStrategy>> strategyResolver =
				( directive ) -> {
					synchronized ( strategies ) {
						return strategies.stream()
						                 .map( BeanCreationStrategyHolder::getStrategy )
						                 .filter( bcs -> bcs.isApplicable( directive ) )
						                 .findFirst();
					}
				};
		return new BeanCreationStrategy()
		{
			ThreadLocal<BeanCreationStrategy> strategyHolder = new ThreadLocal<>();

			@Override
			public boolean isApplicable( BeanCreationDirective directive ) {
				Optional<BeanCreationStrategy> strategy = strategyResolver.apply( directive );
				if ( strategy.isPresent() ) {
					strategyHolder.set( strategy.get() );
					return true;
				}
				return false;
			}

			@Override
			public Object create( BeanCreationDirective directive ) {
				BeanCreationStrategy beanCreationStrategy = strategyHolder.get();
				Object result = beanCreationStrategy.create( directive );
				strategyHolder.remove();
				return result;
			}
		};
	}

	/**
	 * Registers a {@link BeanCreationStrategy} under a given name.
	 *
	 * @param name     to register the strategy under
	 * @param strategy to register
	 */
	public void add( @NonNull String name, @NonNull BeanCreationStrategy strategy ) {
		add( name, strategy, DEFAULT_ORDER );

	}

	/**
	 * Registers a {@link BeanCreationStrategy} under a given name and a specific order.
	 *
	 * @param name     to register the strategy under
	 * @param strategy to register
	 * @param order    for the strategy
	 */
	public void add( @NonNull String name, @NonNull BeanCreationStrategy strategy, @NonNull int order ) {
		synchronized ( strategies ) {
			strategies.stream()
			          .filter( s -> StringUtils.equals( name, s.getName() ) )
			          .findFirst()
			          .ifPresent( strategies::remove );
			strategies.add(
					BeanCreationStrategyHolder.builder()
					                          .name( name )
					                          .strategy( strategy )
					                          .order( order )
					                          .build()
			);
			strategies.sort( Comparator.comparing( BeanCreationStrategyHolder::getOrder ) );
		}
	}

	/**
	 * Removes a {@link BeanCreationStrategy} under a given name.
	 *
	 * @param name of the strategy to remove
	 */
	public void remove( @NonNull String name ) {
		synchronized ( strategies ) {
			strategies.removeIf( s -> StringUtils.equals( name, s.getName() ) );
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	static class BeanCreationStrategyHolder
	{
		private String name;
		private BeanCreationStrategy strategy;
		private int order;
	}
}
