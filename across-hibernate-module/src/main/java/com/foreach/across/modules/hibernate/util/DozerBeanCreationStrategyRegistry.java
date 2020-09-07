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

@ConditionalOnClass(DozerBeanMapper.class)
public class DozerBeanCreationStrategyRegistry
{
	private Map<String, BeanCreationStrategy> strategies = new ConcurrentHashMap<>();

	BeanCreationStrategy getBeanCreationStrategy() {
		return new BeanCreationStrategy()
		{
			@Override
			public boolean isApplicable( BeanCreationDirective directive ) {
				Optional<BeanCreationStrategy> first = strategies.values().stream()
				                                                 .filter( bcs -> bcs.isApplicable( directive ) )
				                                                 .findFirst();
				return first.isPresent();
			}

			@Override
			public Object create( BeanCreationDirective directive ) {
				BeanCreationStrategy beanCreationStrategy = strategies.values().stream()
				                                                      .filter( bcs -> bcs.isApplicable( directive ) )
				                                                      .findFirst()
				                                                      .get();
				return beanCreationStrategy.create( directive );
			}
		};
	}

	public void add( String name, BeanCreationStrategy strategy ) {
		strategies.put( name, strategy );
	}

	public void remove( String name ) {
		strategies.remove( name );
	}

}
