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

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.database.DatabaseInfo;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.config.HibernatePackageBuilder;
import com.foreach.across.modules.hibernate.config.InterceptorRegistryConfiguration;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import com.foreach.across.modules.hibernate.jpa.services.JpaHibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.strategy.AbstractTableAliasNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configures a JPA EntityManagerFactory.
 *
 * @see com.foreach.across.modules.hibernate.config.HibernateConfiguration
 * @see com.foreach.across.modules.hibernate.config.DynamicConfigurationRegistrar
 */
@Configuration
@Import({ InterceptorRegistryConfiguration.class, HibernatePackageBuilder.class,
          DynamicConfigurationRegistrar.class })
public class HibernateJpaConfiguration
{
	public static final String TRANSACTION_MANAGER = "jpaTransactionManager";

	private static final Logger LOG = LoggerFactory.getLogger( HibernateJpaConfiguration.class );

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossHibernateJpaModule module;

	@Autowired
	private AcrossHibernateJpaModuleSettings settings;

	@Autowired
	private HibernatePackage hibernatePackage;

	@Autowired(required = false)
	@Qualifier(AcrossContext.DATASOURCE)
	private DataSource acrossDataSource;

	@Bean(name = "entityManagerFactory")
	@Exposed
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		DataSource dataSource = retrieveDataSource();
		vendorAdapter.setDatabase( determineDatabase( dataSource ) );

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter( vendorAdapter );
		factory.setDataSource( dataSource );
		factory.setPersistenceUnitName( settings.getPersistenceUnitName() );

		String[] mappingResources = hibernatePackage.getMappingResources();
		if ( mappingResources.length > 0 ) {
			factory.setMappingResources( hibernatePackage.getMappingResources() );
		}
		factory.setPackagesToScan( hibernatePackage.getPackagesToScan() );

		factory.getJpaPropertyMap().putAll( hibernateProperties() );

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

		if ( moduleDataSource == null ) {
			LOG.debug( "No module datasource specified - falling back to default Across datasource" );
			module.setDataSource( acrossDataSource );
			return acrossDataSource;
		}
		else {
			return moduleDataSource;
		}
	}

	private Map<String, String> hibernateProperties() {
		Map<String, String> hibernateProperties = new HashMap<>();
		hibernateProperties.putAll( settings.getHibernateProperties() );
		return hibernateProperties;
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

	@Event
	@SuppressWarnings("unused")
	public void registerClientModuleRepositoryInterceptors( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		if ( settings.isRegisterRepositoryInterceptor() ) {
			LOG.trace( "Enabling BasicRepository EntityInterceptor support in module {}",
			           beforeBootstrapEvent.getModule().getName() );
			beforeBootstrapEvent.addApplicationContextConfigurers(
					new AnnotatedClassConfigurer( ModuleBasicRepositoryInterceptorConfiguration.class )
			);
		}
	}

	private Database determineDatabase( DataSource dataSource ) {
		DatabaseInfo databaseInfo = DatabaseInfo.retrieve( dataSource );

		if ( databaseInfo.isHsql() ) {
			return Database.HSQL;
		}
		if ( databaseInfo.isOracle() ) {
			return Database.ORACLE;
		}
		if ( databaseInfo.isMySQL() ) {
			return Database.MYSQL;
		}
		if ( databaseInfo.isSqlServer() ) {
			return Database.SQL_SERVER;
		}

		return null;
	}
}
