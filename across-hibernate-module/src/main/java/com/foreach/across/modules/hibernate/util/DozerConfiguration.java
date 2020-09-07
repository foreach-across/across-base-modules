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
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * If Dozer is on the classpath, this configures a {@link DozerBeanMapper} and a {@link DozerBeanCreationStrategyRegistry}.
 * The resulting {@link Mapper} is then configured for {@link DtoUtils#createDto(Object)} so that complex object hierarchies have a better DTO conversion.
 * This means that instead of properties which reference other objects are copied over, a DTO is created for those properties as well.
 * </p>
 * The {@link Mapper} is created with a proxy {@link com.github.dozermapper.core.factory.BeanCreationStrategy} that supports registering additional application-based strategies.
 *
 * @author Steven Gentens
 * @see DozerBeanCreationStrategyRegistry
 * @since 4.0.1
 */
@Configuration
@ConditionalOnClass(DozerBeanMapper.class)
class DozerConfiguration
{
	@Bean
	public DozerBeanCreationStrategyRegistry registry() {
		DozerBeanCreationStrategyRegistry dozerBeanCreationStrategyRegistry = new DozerBeanCreationStrategyRegistry();
		dozerBeanCreationStrategyRegistry.add( DomainTypesBeanCreationStrategy.class.getSimpleName(),
		                                       new DomainTypesBeanCreationStrategy() );
		return dozerBeanCreationStrategyRegistry;
	}

	@Bean
	public Mapper dozerBeanMapper( ConfigurableBeanFactory beanFactory, DozerBeanCreationStrategyRegistry registry ) {
		ClassLoader classLoader = beanFactory.getBeanClassLoader();
		Mapper build = DozerBeanMapperBuilder.create()
		                                     .withBeanMappingsBuilders(
				                                     ( beanContainer, destBeanCreator, propertyDescriptorFactory ) -> {
					                                     // registry
					                                     destBeanCreator.addPluggedStrategy( registry.getBeanCreationStrategy() );
					                                     return Collections.emptyList();
				                                     } )
		                                     .withClassLoader( classLoader )
		                                     .build();
		DtoUtils.dtoMapper = ( entityType, entity ) -> build.map( entity, entityType );
		return build;
	}
}
