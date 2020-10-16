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
package com.foreach.across.modules.hibernate.jpa.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.DynamicAcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.events.AcrossContextBootstrappedEvent;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.config.HibernatePackageBuilder;
import com.foreach.across.modules.hibernate.config.InterceptorRegistryConfiguration;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import com.foreach.across.modules.hibernate.jpa.repositories.config.ApplicationModuleRepositoryAutoConfiguration;
import com.foreach.across.modules.hibernate.jpa.services.JpaHibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.jpa.unitofwork.JpaUnitOfWorkFactoryImpl;
import com.foreach.across.modules.hibernate.modules.config.EnableTransactionManagementConfiguration;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.strategy.AbstractTableAliasNamingStrategy;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.modules.hibernate.util.DozerConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Configures a JPA EntityManagerFactory.
 *
 * @see com.foreach.across.modules.hibernate.config.HibernateConfiguration
 * @see com.foreach.across.modules.hibernate.jpa.config.JpaModuleSettingsRegistrar
 */
@Configuration
@Import({ JpaModuleSettingsRegistrar.class, InterceptorRegistryConfiguration.class, HibernatePackageBuilder.class,
          PersistenceExceptionTranslationAutoConfiguration.class, DozerConfiguration.class })
public class HibernateJpaConfiguration
{
	public static final String TRANSACTION_MANAGER = "jpaTransactionManager";
	public static final String TRANSACTION_TEMPLATE = "jpaTransactionTemplate";
	public static final BootstrapMode DEFAULT_ACROSS_BOOTSTRAP_MODE = BootstrapMode.DEFAULT;

	private static final Logger LOG = LoggerFactory.getLogger( HibernateJpaConfiguration.class );

	private final AcrossHibernateJpaModule module;
	private final AcrossHibernateJpaModuleSettings settings;
	private final HibernatePackage hibernatePackage;
	private final ListableBeanFactory beanFactory;

	@Autowired
	public HibernateJpaConfiguration( @Module(AcrossModule.CURRENT_MODULE) AcrossHibernateJpaModule module,
	                                  @Module(AcrossModule.CURRENT_MODULE) AcrossHibernateJpaModuleSettings settings,
	                                  HibernatePackage hibernatePackage,
	                                  ListableBeanFactory beanFactory ) {
		this.module = module;
		this.settings = settings;
		this.hibernatePackage = hibernatePackage;
		this.beanFactory = beanFactory;
	}

	@Bean(name = "entityManagerFactory")
	@Exposed
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		DataSource dataSource = retrieveDataSource();
		vendorAdapter.setShowSql( settings.isShowSql() );
		if ( settings.getDatabase() != null ) {
			vendorAdapter.setDatabasePlatform( settings.getDatabasePlatform() );
			vendorAdapter.setDatabase( settings.getDatabase() );
		}
		if ( settings.getDatabasePlatform() != null ) {
			vendorAdapter.setDatabasePlatform( settings.getDatabasePlatform() );
		}
		vendorAdapter.setDatabasePlatform( settings.getDatabasePlatform() );
		vendorAdapter.setGenerateDdl( settings.isGenerateDdl() );

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter( vendorAdapter );
		factory.setDataSource( dataSource );
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.initialize();
		factory.setBootstrapExecutor( threadPoolTaskExecutor );
		factory.setPersistenceUnitName( settings.getPersistenceUnitName() );

		String[] mappingResources = hibernatePackage.getMappingResources();
		if ( mappingResources.length > 0 ) {
			factory.setMappingResources( hibernatePackage.getMappingResources() );
		}
		factory.setPackagesToScan( hibernatePackage.getPackagesToScan() );
		factory.getJpaPropertyMap().putAll( settings.getHibernateProperties( new HibernateSettings() ) );

		Map<String, String> tableAliases = hibernatePackage.getTableAliases();

		if ( !tableAliases.isEmpty() ) {
			// Create a unique naming strategy class referring to the defined table aliases
			factory.getJpaPropertyMap().put( org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY,
			                                 createTableAliasNamingStrategyClass( tableAliases ).getName() );
		}

		return factory;
	}

	private DataSource retrieveDataSource() {
		DataSource moduleDataSource = module.getDataSource();

		if ( moduleDataSource != null ) {
			LOG.info( "Using datasource attached directly to module {} for the EntityManagerFactory", module.getName() );
			return moduleDataSource;
		}

		if ( !StringUtils.isEmpty( settings.getDataSource() ) ) {
			LOG.info( "Resolving datasource bean {} for the EntityManagerFactory", settings.getDataSource() );
			return beanFactory.getBean( settings.getDataSource(), DataSource.class );
		}

		if ( BeanFactoryUtils.beansOfTypeIncludingAncestors( beanFactory, DataSource.class ).size() == 1 ) {
			LOG.info( "Using the single datasource bean for the EntityManagerFactory" );
			return beanFactory.getBean( DataSource.class );
		}

		throw new IllegalStateException(
				"Was unable to resolve the correct datasource bean to use, bean name: " + settings.getDataSource() );
	}

	private Class createTableAliasNamingStrategyClass( Map<String, String> tableAliases ) {
		InterfaceMaker interfaceMaker = new InterfaceMaker();
		Class dynamicInterface = interfaceMaker.create();

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass( AbstractTableAliasNamingStrategy.class );
		enhancer.setInterfaces( new Class[] { dynamicInterface } );
		enhancer.setUseFactory( false );
		enhancer.setCallbackType( NoOp.class );

		Class strategyClass = enhancer.createClass();

		AbstractTableAliasNamingStrategy.registerTableAliases( strategyClass, tableAliases );

		return strategyClass;
	}

	@Bean
	@Exposed
	public HibernateSessionHolder hibernateSessionHolder() {
		return new JpaHibernateSessionHolderImpl();
	}

	@Bean
	@Exposed
	@ConditionalOnExpression("@moduleSettings.createUnitOfWorkFactory")
	public UnitOfWorkFactory unitOfWork( EntityManagerFactory entityManagerFactory ) {
		return new JpaUnitOfWorkFactoryImpl( Collections.singleton( entityManagerFactory ) );
	}

	@EventListener
	@SuppressWarnings("unused")
	public void registerClientModuleRepositoryInterceptors( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		if ( settings.isRegisterRepositoryInterceptor() ) {
			LOG.trace( "Enabling BasicRepository EntityInterceptor support in module {}",
			           beforeBootstrapEvent.getModule().getName() );
			beforeBootstrapEvent.getBootstrapConfig().addApplicationContextConfigurer( true,
			                                                                           ModuleBasicRepositoryInterceptorConfiguration.class );
		}

		if ( settings.getApplicationModule().isRepositoryScan()
				&& beforeBootstrapEvent.getModule().getModule() instanceof DynamicAcrossModule.DynamicApplicationModule ) {
			beforeBootstrapEvent.getBootstrapConfig().addApplicationContextConfigurer( true,
			                                                                           ApplicationModuleRepositoryAutoConfiguration.class );
		}

		LOG.trace( "Enabling @Transaction support in module {}", beforeBootstrapEvent.getModule().getName() );
		beforeBootstrapEvent.getBootstrapConfig()
		                    .addApplicationContextConfigurer( true,
		                                                      EnableTransactionManagementConfiguration.class,
		                                                      PersistenceExceptionTranslationAutoConfiguration.class
		                    );
	}

	@Configuration
	@ConditionalOnProperty(value = "spring.data.jpa.repositories.bootstrap-mode", havingValue = "deferred")
	@Slf4j
	@RequiredArgsConstructor
	public static class DeferredRepositoryInitializer
	{

		private final @Module(AcrossModule.CURRENT_MODULE)
		AcrossHibernateJpaModule module;
		private final ListableBeanFactory beanFactory;

		/***
		 * This method will trigger when the spring.data.jpa.repositories.bootstrap-mode is set to deferred.
		 * The code was originally in {@link org.springframework.data.repository.config.RepositoryConfigurationDelegate}
		 * but registers the {@link org.springframework.data.repository.config.DeferredRepositoryInitializationListener} in each {@code Beanfactory}
		 *
		 * The EventListener below will trigger after {@link com.foreach.across.core.events.AcrossContextBootstrappedEvent}
		 * and activate the Repositories by calling them once (on the first AcrossHibernateJpaModule) that matches.
		 */
		@EventListener
		public void initializeRepositories( AcrossContextBootstrappedEvent contextBootstrappedEvent ) {
			Optional<AcrossModule> first = contextBootstrappedEvent.getContext().getModules().stream()
			                                                       .filter(
					                                                       m -> AcrossHibernateJpaModule.class
							                                                       .isAssignableFrom( m.getModule().getClass() ) )
			                                                       .map( AcrossModuleInfo::getModule )
			                                                       .findFirst();
			if ( first.isPresent() && first.get() == module ) {
				LOG.info( "Triggering deferred initialization of Spring Data repositories from HibernateJpaConfiguration…" );
				StopWatch watch = new StopWatch();
				watch.start();
				beanFactory.getBeansOfType( Repository.class );
				watch.stop();
				LOG.info( "Spring Data repositories initialized in {}ms!", watch.getLastTaskTimeMillis() );
			}
		}
	}
}
