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
package com.foreach.across.modules.hibernate.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.AcrossHibernateModuleSettings;
import com.foreach.across.modules.hibernate.modules.config.EnableTransactionManagementConfiguration;
import com.foreach.across.modules.hibernate.modules.config.ModuleBasicRepositoryInterceptorConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackage;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolderImpl;
import com.foreach.across.modules.hibernate.strategy.TableAliasNamingStrategy;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactory;
import com.foreach.across.modules.hibernate.unitofwork.UnitOfWorkFactoryImpl;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Configures a standard SessionFactory.
 *
 * @see com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration
 * @see com.foreach.across.modules.hibernate.config.ModuleSettingsRegistrar
 */
@Configuration
@Import({ ModuleSettingsRegistrar.class, HibernatePackageBuilder.class, PersistenceExceptionTranslationAutoConfiguration.class })
public class HibernateConfiguration
{
	public static final String TRANSACTION_MANAGER = "transactionManager";
	public static final String SESSION_HOLDER = "hibernateSessionHolder";

	private static final Logger LOG = LoggerFactory.getLogger( HibernateConfiguration.class );

	private final AcrossHibernateModule module;
	private final AcrossHibernateModuleSettings settings;
	private final ListableBeanFactory beanFactory;

	@Autowired
	public HibernateConfiguration( @Module(AcrossModule.CURRENT_MODULE) AcrossHibernateModule module,
	                               @Module(AcrossModule.CURRENT_MODULE) AcrossHibernateModuleSettings settings,
	                               ListableBeanFactory beanFactory ) {
		this.module = module;
		this.settings = settings;
		this.beanFactory = beanFactory;
	}

	@Bean
	@Exposed
	public LocalSessionFactoryBean sessionFactory( HibernatePackage hibernatePackage ) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

		DataSource dataSource = retrieveDataSource();
		sessionFactory.setDataSource( dataSource );
		sessionFactory.setPackagesToScan( hibernatePackage.getPackagesToScan() );
		sessionFactory.setMappingResources( hibernatePackage.getMappingResources() );

		Map<String, String> tableAliases = hibernatePackage.getTableAliases();

		if ( !tableAliases.isEmpty() ) {
			sessionFactory.setPhysicalNamingStrategy( new TableAliasNamingStrategy( tableAliases ) );
		}

		Properties propertiesToSet = new Properties();
		propertiesToSet.putAll( settings.getHibernateProperties( dataSource ) );

		sessionFactory.setHibernateProperties( propertiesToSet );

		return sessionFactory;
	}

	private DataSource retrieveDataSource() {
		DataSource moduleDataSource = module.getDataSource();

		if ( moduleDataSource != null ) {
			LOG.info( "Using datasource attached directly to module {} for the SessionFactory", module.getName() );
			return moduleDataSource;
		}

		if ( !StringUtils.isEmpty( settings.getDataSource() ) ) {
			LOG.info( "Resolving datasource bean {} for the SessionFactory", settings.getDataSource() );
			return beanFactory.getBean( settings.getDataSource(), DataSource.class );
		}

		if ( BeanFactoryUtils.beansOfTypeIncludingAncestors( beanFactory, DataSource.class ).size() == 1 ) {
			LOG.info( "Using the single datasource bean for the SessionFactory" );
			return beanFactory.getBean( DataSource.class );
		}

		throw new IllegalStateException( "Was unable to resolve the correct datasource bean to use, bean name: " + settings.getDataSource() );
	}

	@Bean(name = SESSION_HOLDER)
	@Exposed
	public HibernateSessionHolder hibernateSessionHolder() {
		return new HibernateSessionHolderImpl();
	}

	@ConditionalOnExpression("@moduleSettings.createUnitOfWorkFactory")
	@Bean
	@Exposed
	public UnitOfWorkFactory unitOfWork( SessionFactory sessionFactory ) {
		return new UnitOfWorkFactoryImpl( Collections.singleton( sessionFactory ) );
	}

	@EventListener
	@SuppressWarnings("unused")
	protected void registerClientModuleRepositoryInterceptors( AcrossModuleBeforeBootstrapEvent beforeBootstrapEvent ) {
		if ( settings.isRegisterRepositoryInterceptor() ) {
			LOG.trace( "Enabling BasicRepositoryInterceptor support in module {}",
			           beforeBootstrapEvent.getModule().getName() );
			beforeBootstrapEvent.getBootstrapConfig().addApplicationContextConfigurer( true, ModuleBasicRepositoryInterceptorConfiguration.class );
		}

		LOG.trace( "Enabling @Transaction support in module {}", beforeBootstrapEvent.getModule().getName() );
		beforeBootstrapEvent.getBootstrapConfig()
		                    .addApplicationContextConfigurer( true,
		                                                      EnableTransactionManagementConfiguration.class,
		                                                      PersistenceExceptionTranslationAutoConfiguration.class );
	}
}
