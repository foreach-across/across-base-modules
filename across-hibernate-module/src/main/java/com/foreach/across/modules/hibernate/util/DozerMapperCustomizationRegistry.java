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

import com.github.dozermapper.core.CustomFieldMapper;
import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.BeanCreationStrategy;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.util.Assert;

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
public class DozerMapperCustomizationRegistry
{
	public static final int DEFAULT_ORDER = 1000;

	private final List<DozerCustomizationRegistrar<BeanCreationStrategy>> strategies = new ArrayList<>();
	private final List<DozerCustomizationRegistrar<CustomFieldMapper>> fieldMappers = new ArrayList<>();

	CustomFieldMapper getCustomFieldMapper() {
		return ( source, destination, sourceFieldValue, classMap, fieldMapping ) -> {
			synchronized ( fieldMappers ) {
				return fieldMappers.stream()
				                   .map( DozerCustomizationRegistrar::customization )
				                   .anyMatch( cfm -> cfm.mapField( source, destination, sourceFieldValue, classMap, fieldMapping ) );
			}
		};

	}

	BeanCreationStrategy getBeanCreationStrategy() {
		Function<BeanCreationDirective, Optional<BeanCreationStrategy>> strategyResolver =
				( directive ) -> {
					synchronized ( strategies ) {
						return strategies.stream()
						                 .map( DozerCustomizationRegistrar::customization )
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

	public void register( @NonNull DozerCustomizationRegistrar customizationRegistrar ) {
		Assert.notNull( customizationRegistrar, "Registrar should not be null!" );
		Assert.notNull( customizationRegistrar.name(), "Registrar requires a name to be filled in!" );
		Object customization = customizationRegistrar.customization();
		Assert.notNull( customization, "Registrar requires a customization to be set!" );

		if ( CustomFieldMapper.class.isAssignableFrom( customization.getClass() ) ) {
			synchronized ( fieldMappers ) {
				handle( fieldMappers, customizationRegistrar );
			}
		}
		else if ( BeanCreationStrategy.class.isAssignableFrom( customization.getClass() ) ) {
			synchronized ( strategies ) {
				handle( strategies, customizationRegistrar );
			}
		}
	}

	private <T> void handle( List<DozerCustomizationRegistrar<T>> collection, DozerCustomizationRegistrar<T> toRegister ) {
		collection.stream()
		          .filter( s -> StringUtils.equals( toRegister.name(), s.name() ) )
		          .findFirst()
		          .ifPresent( collection::remove );
		collection.add( toRegister );
		collection.sort( Comparator.comparing( DozerCustomizationRegistrar::order ) );
	}

	/**
	 * Removes a {@link BeanCreationStrategy} with a given name.
	 *
	 * @param name of the strategy to remove
	 */
	public void removeBeanCreationStrategy( @NonNull String name ) {
		synchronized ( strategies ) {
			strategies.removeIf( s -> StringUtils.equals( name, s.name() ) );
		}
	}

	/**
	 * Removes a {@link CustomFieldMapper} with a given name.
	 *
	 * @param name of the strategy to remove
	 */
	public void removeCustomFieldMapper( @NonNull String name ) {
		synchronized ( fieldMappers ) {
			fieldMappers.removeIf( s -> StringUtils.equals( name, s.name() ) );
		}
	}

	/**
	 * Creates a registrar for {@link BeanCreationStrategy}s.
	 */
	public static DozerCustomizationRegistrar<BeanCreationStrategy> beanCreationStrategyRegistrar() {
		return new DozerCustomizationRegistrar<>();
	}

	/**
	 * Creates a registrar for {@link CustomFieldMapper}s.
	 */
	public static DozerCustomizationRegistrar<CustomFieldMapper> customFieldMapperRegistrar() {
		return new DozerCustomizationRegistrar<>();
	}

	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(fluent = true, chain = true)
	public static class DozerCustomizationRegistrar<T>
	{
		private String name;
		private T customization;
		private int order = 1000;
	}
}
