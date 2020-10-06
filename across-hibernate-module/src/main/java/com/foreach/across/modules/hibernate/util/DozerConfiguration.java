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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Optional;

/**
 * If Dozer is on the classpath, this configures a {@link DozerBeanMapper} and a {@link DozerMapperCustomizationRegistry}.
 * The resulting {@link Mapper} is then configured for {@link DtoUtils#createDto(Object)} so that complex object hierarchies have a better DTO conversion.
 * This means that instead of properties which reference other objects are copied over, a DTO is created for those properties as well.
 * </p>
 * The {@link Mapper} is created with a proxy {@link com.github.dozermapper.core.factory.BeanCreationStrategy} that supports registering additional application-based strategies.
 *
 * @author Steven Gentens
 * @see DozerMapperCustomizationRegistry
 * @since 4.0.1
 */
@Configuration
@ConditionalOnClass(DozerBeanMapper.class)
@RequiredArgsConstructor
public class DozerConfiguration
{
	private final AcrossHibernateModuleSettings acrossHibernateModuleSettings;

	@Bean
	@Exposed
	public DozerMapperCustomizationRegistry registry() {
		if ( !acrossHibernateModuleSettings.isAdvancedDtoConversion() ) {
			return null;
		}
		return new DozerMapperCustomizationRegistry();
	}

	@Bean
	@Exposed
	public Mapper dozerBeanMapper( ConfigurableBeanFactory beanFactory, Optional<DozerMapperCustomizationRegistry> oRegistry ) {
		if ( !acrossHibernateModuleSettings.isAdvancedDtoConversion() || !oRegistry.isPresent() ) {
			return null;
		}

		DozerMapperCustomizationRegistry registry = oRegistry.get();
		ClassLoader classLoader = beanFactory.getBeanClassLoader();
		Mapper build = DozerBeanMapperBuilder.create()
		                                     .withCustomFieldMapper( registry.getCustomFieldMapper() )
		                                     .withBeanMappingsBuilders(
				                                     ( beanContainer, destBeanCreator, propertyDescriptorFactory ) -> {
					                                     destBeanCreator.addPluggedStrategy( registry.getBeanCreationStrategy() );
					                                     return Collections.emptyList();
				                                     } )
		                                     .withClassLoader( classLoader )
		                                     .build();

		DtoUtils.dtoMapper = ( entityType, entity ) -> build.map( entity, entityType );

		return build;
	}
}
