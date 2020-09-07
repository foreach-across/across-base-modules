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
import com.github.dozermapper.core.builder.BeanMappingsBuilder;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@ConditionalOnClass(DozerBeanMapper.class)
class DozerConfiguration
{
	@Bean
	public DozerBeanCreationStrategyRegistry registry() {
		return new DozerBeanCreationStrategyRegistry();
	}

	@Bean
	public Mapper dozerBeanMapper( ConfigurableBeanFactory beanFactory, DozerBeanCreationStrategyRegistry registry ) {
		ClassLoader classLoader = beanFactory.getBeanClassLoader();
		Mapper build = DozerBeanMapperBuilder.create()
		                                     .withBeanMappingsBuilders( new BeanMappingsBuilder()
		                                     {
			                                     @Override
			                                     public List<MappingFileData> build( BeanContainer beanContainer,
			                                                                         DestBeanCreator destBeanCreator,
			                                                                         PropertyDescriptorFactory propertyDescriptorFactory ) {
				                                     // registry
				                                     destBeanCreator.addPluggedStrategy( registry.getBeanCreationStrategy() );
				                                     return Collections.emptyList();
			                                     }
		                                     } )
		                                     .withClassLoader( classLoader )
		                                     .build();
		return build;
	}
}
