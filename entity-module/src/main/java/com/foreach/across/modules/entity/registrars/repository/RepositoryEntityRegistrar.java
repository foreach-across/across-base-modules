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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registrars.EntityRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 * Scans for {@link org.springframework.data.repository.Repository} implementations
 * and creates a default EntityConfiguration for them.  Works for default Spring Data
 * repositories that provide a {@link org.springframework.data.repository.core.support.RepositoryFactoryInformation}
 * bean.
 *
 * @author Arne Vandamme
 */
public class RepositoryEntityRegistrar implements EntityRegistrar
{
	private static final Logger LOG = LoggerFactory.getLogger( RepositoryEntityRegistrar.class );

	@Autowired
	private RepositoryEntityModelBuilder entityModelBuilder;

	@Autowired
	private RepositoryEntityPropertyRegistryBuilder propertyRegistryBuilder;

	@Autowired
	private RepositoryEntityViewsBuilder viewsBuilder;

	@Autowired
	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public void registerEntities( MutableEntityRegistry entityRegistry,
	                              AcrossModuleInfo moduleInfo,
	                              AcrossContextBeanRegistry beanRegistry ) {
		ApplicationContext applicationContext = moduleInfo.getApplicationContext();

		Map<String, RepositoryFactoryInformation> repositoryFactoryInformationMap
				= applicationContext.getBeansOfType( RepositoryFactoryInformation.class );

		for ( Map.Entry<String, RepositoryFactoryInformation> informationBean
				: repositoryFactoryInformationMap.entrySet() ) {
			RepositoryFactoryInformation repositoryFactoryInformation = informationBean.getValue();
			Class<?> entityType = ClassUtils.getUserClass(
					repositoryFactoryInformation.getRepositoryInformation().getDomainType()
			);

			Repository repository = applicationContext.getBean(
					BeanFactoryUtils.transformedBeanName( informationBean.getKey() ), Repository.class
			);

			if ( !entityRegistry.contains( entityType ) ) {
				LOG.debug( "Auto registering entity type {} as repository", entityType.getName() );

				registerEntity( moduleInfo, entityRegistry, entityType, repositoryFactoryInformation, repository );
			}
			else {
				LOG.info( "Skipping auto registration of entity type {} as it is already registered",
				          entityType.getName() );
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void registerEntity(
			AcrossModuleInfo moduleInfo,
			MutableEntityRegistry entityRegistry,
			Class<?> entityType,
			RepositoryFactoryInformation repositoryFactoryInformation,
			Repository repository ) {
		String entityTypeName = determineUniqueEntityTypeName( entityRegistry, entityType );

		if ( entityTypeName != null ) {
			EntityConfigurationImpl entityConfiguration = new EntityConfigurationImpl<>( entityTypeName, entityType );
			entityConfiguration.addAttribute( RepositoryFactoryInformation.class, repositoryFactoryInformation );
			entityConfiguration.addAttribute( Repository.class, repository );

			entityConfiguration.setEntityMessageCodeResolver(
					buildMessageCodeResolver( entityConfiguration, moduleInfo )
			);

			entityConfiguration.setEntityModel(
					entityModelBuilder.buildEntityModel( repositoryFactoryInformation, repository )
			);

			entityConfiguration.setPropertyRegistry(
					propertyRegistryBuilder.buildEntityPropertyRegistry( entityType, repositoryFactoryInformation )
			);

			viewsBuilder.createViews( entityConfiguration );

			entityRegistry.register( entityConfiguration );
		}
		else {
			LOG.warn( "Skipping registration of entity type {} as no unique name could be determined",
			          entityType.getName() );
		}
	}

	private EntityMessageCodeResolver buildMessageCodeResolver( EntityConfiguration entityConfiguration,
	                                                            AcrossModuleInfo moduleInfo ) {
		String name = StringUtils.uncapitalize( entityConfiguration.getEntityType().getSimpleName() );

		EntityMessageCodeResolver resolver = new EntityMessageCodeResolver();
		resolver.setMessageSource( messageSource );
		resolver.setEntityConfiguration( entityConfiguration );
		resolver.setPrefixes( moduleInfo.getName() + ".entities." + name );
		resolver.setFallbackCollections( EntityModule.NAME + ".entities", "" );

		return resolver;
	}

	private String determineUniqueEntityTypeName( EntityRegistry registry, Class<?> entityType ) {
		String name = StringUtils.uncapitalize( entityType.getSimpleName() );

		if ( registry.contains( name ) ) {
			name = entityType.getName();
		}

		if ( registry.contains( name ) ) {
			LOG.error( "Unable to determine unique entity type name for type {}", entityType.getName() );
			return null;
		}

		return name;
	}
}
