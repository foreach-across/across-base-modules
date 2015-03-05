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
import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.query.EntityQueryPageFetcher;
import com.foreach.across.modules.entity.query.jpa.EntityQueryJpaPageFetcher;
import com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslPageFetcher;
import com.foreach.across.modules.entity.registrars.EntityRegistrar;
import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.registry.builders.EntityPropertyRegistryMappingMetaDataBuilder;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.util.ClassUtils;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
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
	private RepositoryEntityAssociationsBuilder associationsBuilder;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private EntityPropertyRegistryMappingMetaDataBuilder mappingMetaDataBuilder;

	@EntityValidator
	private SmartValidator entityValidator;

	@SuppressWarnings("unchecked")
	@Override
	public void registerEntities( MutableEntityRegistry entityRegistry,
	                              AcrossModuleInfo moduleInfo,
	                              AcrossContextBeanRegistry beanRegistry ) {
		ApplicationContext applicationContext = moduleInfo.getApplicationContext();

		mappingMetaDataBuilder.addMappingContexts( applicationContext.getBeansOfType( MappingContext.class ).values() );

		Map<String, RepositoryFactoryInformation> repositoryFactoryInformationMap
				= applicationContext.getBeansOfType( RepositoryFactoryInformation.class );

		List<MutableEntityConfiguration> registered = new ArrayList<>( repositoryFactoryInformationMap.size() );

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

				MutableEntityConfiguration entityConfiguration =
						registerEntity( moduleInfo, entityRegistry, entityType, repositoryFactoryInformation,
						                repository );

				if ( entityConfiguration != null ) {
					registered.add( entityConfiguration );
				}
			}
			else {
				LOG.info( "Skipping auto registration of entity type {} as it is already registered",
				          entityType.getName() );
			}
		}

		for ( MutableEntityConfiguration entityConfiguration : registered ) {
			associationsBuilder.buildAssociations( entityRegistry, entityConfiguration );
		}

		LOG.info( "Registered {} entities from module {}", registered.size(), moduleInfo.getName() );
	}

	@SuppressWarnings("unchecked")
	private MutableEntityConfiguration registerEntity(
			AcrossModuleInfo moduleInfo,
			MutableEntityRegistry entityRegistry,
			Class<?> entityType,
			RepositoryFactoryInformation repositoryFactoryInformation,
			Repository repository ) {
		String entityTypeName = determineUniqueEntityTypeName( entityRegistry, entityType );

		if ( entityTypeName != null ) {
			EntityConfigurationImpl entityConfiguration = new EntityConfigurationImpl<>( entityTypeName, entityType );
			entityConfiguration.addAttribute( AcrossModuleInfo.class, moduleInfo );
			entityConfiguration.addAttribute( RepositoryFactoryInformation.class, repositoryFactoryInformation );
			entityConfiguration.addAttribute( Repository.class, repository );
			entityConfiguration.addAttribute( PersistentEntity.class,
			                                  repositoryFactoryInformation.getPersistentEntity() );

			findDefaultValidatorInModuleContext( entityConfiguration, moduleInfo.getApplicationContext() );

			entityConfiguration.setEntityMessageCodeResolver(
					buildMessageCodeResolver( entityConfiguration, moduleInfo )
			);

			entityConfiguration.setHidden( Modifier.isAbstract( entityType.getModifiers() ) );

			registerEntityQueryPageFetcher( entityConfiguration );

			propertyRegistryBuilder.buildEntityPropertyRegistry( entityConfiguration );
			entityModelBuilder.buildEntityModel( entityConfiguration );
			viewsBuilder.buildViews( entityConfiguration );

			entityRegistry.register( entityConfiguration );

			return entityConfiguration;
		}
		else {
			LOG.warn( "Skipping registration of entity type {} as no unique name could be determined",
			          entityType.getName() );
		}

		return null;
	}

	private void findDefaultValidatorInModuleContext( MutableEntityConfiguration entityConfiguration,
	                                                  ApplicationContext applicationContext ) {
		Validator validatorToUse = entityValidator;

		Map<String, Validator> validatorMap = applicationContext.getBeansOfType( Validator.class );
		List<Validator> candidates = new ArrayList<>();

		for ( Validator validator : validatorMap.values() ) {
			if ( validator != entityValidator && validator.supports( entityConfiguration.getEntityType() ) ) {
				candidates.add( validator );
			}
		}

		if ( candidates.size() > 1 ) {
			LOG.debug(
					"Module has more than one validator that supports {} - unable to decide, sticking to default entity validator",
					entityConfiguration.getEntityType() );
		}
		else if ( !candidates.isEmpty() ) {
			validatorToUse = candidates.get( 0 );
			LOG.debug( "Auto-registering validator bean of type {} as default validator for entity {}",
			           ClassUtils.getUserClass( validatorToUse ).getName(), entityConfiguration.getEntityType() );
		}

		entityConfiguration.addAttribute( Validator.class, validatorToUse );
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

	/**
	 * Determine the best {@link com.foreach.across.modules.entity.query.EntityQueryPageFetcher} implementation
	 * for this entity.
	 */
	private void registerEntityQueryPageFetcher( MutableEntityConfiguration entityConfiguration ) {
		Repository repository = entityConfiguration.getAttribute( Repository.class );

		EntityQueryPageFetcher entityQueryPageFetcher = null;

		// Because of some bugs related to JPA - Hibernate integration, favour the use of QueryDsl if possible,
		// see particular issue: https://hibernate.atlassian.net/browse/HHH-5948
		if ( repository instanceof QueryDslPredicateExecutor ) {
			entityQueryPageFetcher = new EntityQueryQueryDslPageFetcher( (QueryDslPredicateExecutor) repository,
			                                                             entityConfiguration );
		}
		else if ( repository instanceof JpaSpecificationExecutor ) {
			entityQueryPageFetcher = new EntityQueryJpaPageFetcher( (JpaSpecificationExecutor) repository );
		}

		if ( entityQueryPageFetcher != null ) {
			entityConfiguration.addAttribute( EntityQueryPageFetcher.class, entityQueryPageFetcher );
		}
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
