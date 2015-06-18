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
import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.database.DatabaseInfo;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import com.foreach.across.modules.hibernate.jpa.services.JpaHibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.modules.config.ModuleCrudRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.strategy.AbstractTableAliasNamingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
import org.hibernate.engine.jdbc.batch.internal.FixedBatchBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InterfaceMaker;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * @see com.foreach.across.modules.hibernate.config.TransactionManagerConfiguration
 */
@Configuration
@AcrossEventHandler
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

	@Bean
	@Exposed
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase( determineDatabase( module.getDataSource() ) );

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter( vendorAdapter );
		factory.setDataSource( module.getDataSource() );

		String[] mappingResources = hibernatePackage.getMappingResources();
		if ( mappingResources.length > 0 ) {
			factory.setMappingResources( hibernatePackage.getMappingResources() );
		}
		factory.setPackagesToScan( hibernatePackage.getPackagesToScan() );

		factory.getJpaPropertyMap().putAll( hibernateProperties() );

		Map<String, String> tableAliases = hibernatePackage.getTableAliases();

		if ( !tableAliases.isEmpty() ) {
			// Create a unique naming strategy class referring to the defined table aliases
			factory.getJpaPropertyMap().put( AvailableSettings.NAMING_STRATEGY,
			                                 createTableAliasNamingStrategyClass( tableAliases ).getName() );
		}

		return factory;
	}

	private Map<String, Object> hibernateProperties() {
		String version = org.hibernate.Version.getVersionString();
		Map<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.putAll( settings.getHibernateProperties() );

		if ( StringUtils.startsWith( version, "4.2" ) ) {
			if ( hibernateProperties.get( BatchBuilderInitiator.BUILDER ) != null
					|| settings.getProperty( BatchBuilderInitiator.BUILDER ) != null ) {
				LOG.info(
						"Skipping workaround for https://hibernate.atlassian.net/browse/HHH-8853 because you have a custom builder" );
			}
			else {
				// WORKAROUND bug: https://hibernate.atlassian.net/browse/HHH-8853
				Object hibernateJdbcBatchSize = hibernateProperties.get( Environment.STATEMENT_BATCH_SIZE );

				int batchSize = 0;
				if ( hibernateJdbcBatchSize != null ) {
					batchSize = hibernateJdbcBatchSize instanceof Number
							? ( (Number) hibernateJdbcBatchSize ).intValue()
							: Integer.valueOf( hibernateJdbcBatchSize.toString() );
				}

				LOG.info( "Enabling workaround for https://hibernate.atlassian.net/browse/HHH-8853 with batchsize: {}",
				          batchSize );
				FixedBatchBuilderImpl.setSize( batchSize );
				hibernateProperties.put( "hibernate.jdbc.batch.builder", FixedBatchBuilderImpl.class.getName() );
			}
		}

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
			LOG.trace( "Enabling CrudRepositoryInterceptor support in module {}",
			           beforeBootstrapEvent.getModule().getName() );
			beforeBootstrapEvent.addApplicationContextConfigurers(
					new AnnotatedClassConfigurer( ModuleCrudRepositoryInterceptorConfiguration.class )
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
